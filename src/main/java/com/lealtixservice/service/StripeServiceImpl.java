package com.lealtixservice.service;

import com.lealtixservice.config.SendGridTemplates;
import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantPayment;
import com.lealtixservice.repository.PreRegistroRepository;
import com.lealtixservice.repository.TenantPaymentRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.util.DateUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.PriceCreateParams;
import com.lealtixservice.dto.ProductPriceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger log = LoggerFactory.getLogger(StripeServiceImpl.class);
    public static final String COMPLETED = "COMPLETED";
    private final SendGridTemplates sendGridTemplates;
    @Autowired
    private TenantPaymentRepository tenantPaymentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PreRegistroRepository preRegistroRepository;

    @Autowired
    private Emailservice emailservice;

    @Autowired
    private TokenService tokenService;

    private Price lastCreatedPrice;;

    @Value("${stripe.api.key}")
    private String apiKey;

    public StripeServiceImpl(@Value("${stripe.api.key}") String apiKey, SendGridTemplates sendGridTemplates) {
        this.sendGridTemplates = sendGridTemplates;
        Stripe.apiKey = apiKey;
    }

    @Override
    public Product createProductWithPrice(ProductPriceRequest request) throws StripeException {
        ProductCreateParams productParams = ProductCreateParams.builder()
                .setName(request.getName())
                .setDescription(request.getDescription())
                .build();
        Product product = Product.create(productParams);

        PriceCreateParams.Recurring.Interval interval = PriceCreateParams.Recurring.Interval.valueOf(request.getInterval().toUpperCase());
        PriceCreateParams priceParams = PriceCreateParams.builder()
                .setProduct(product.getId())
                .setCurrency(request.getCurrency())
                .setUnitAmount(request.getAmount())
                .setRecurring(PriceCreateParams.Recurring.builder().setInterval(interval).build())
                .build();
        lastCreatedPrice = Price.create(priceParams);
        return product;
    }

    @Override
    public Price getLastCreatedPrice() {
        return lastCreatedPrice;
    }

    @Override
    public Map<String, Object> createCheckoutSession(String priceId, String tenatId) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("mode", "subscription");
        params.put("success_url", "http://localhost:4200/checkout/success?session_id={CHECKOUT_SESSION_ID}");
        params.put("cancel_url", "http://localhost:4200/checkout/cancel?session_id={CHECKOUT_SESSION_ID}");
        params.put("client_reference_id", tenatId);

        List<Object> lineItems = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("price", priceId);
        item.put("quantity", 1);
        lineItems.add(item);
        params.put("line_items", lineItems);
        Session session;
        try {
            session = Session.create(params);
        }catch (Exception e){
            log.error("Error creating checkout session: {}", e.getMessage());
            throw new Exception("Error creating checkout session");
        }
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", session.getId());
        return responseData;
    }

    @Override
    public PagoDto getCheckoutSession(String sessionId) {
        try {
            PagoDto pagoDto = null;
            Session session = Session.retrieve(sessionId);
            String tenantId = session.getClientReferenceId();
            TenantPayment tenantPayment = tenantPaymentRepository.findByUIDTenantAndStatus(tenantId, "INITIATED");
            if (tenantPayment != null) {
                pagoDto = new PagoDto();
                pagoDto.setPlan(tenantPayment.getPlan());
                pagoDto.setInterval("Mensual");
                pagoDto.setName(session.getCustomerDetails().getName());
                pagoDto.setCost("$ " + session.getAmountTotal());
                pagoDto.setCurrency(session.getCurrency());
                pagoDto.setPaymentDate(DateUtils.formatDatefromLong(session.getCreated()));
                pagoDto.setNextPaymentDate(DateUtils.formatDatefromLongNext(session.getCreated()));
                pagoDto.setLink(createSlug(tenantPayment.getTenant()));
                // Actualizar estado del pago
                TenantPayment tp = new TenantPayment();
                tp.setCreatedAt(LocalDateTime.now());
                tp.setEndDate(tenantPayment.getEndDate());
                tp.setPlan(tenantPayment.getPlan());
                tp.setStartDate(tenantPayment.getStartDate());
                tp.setTenant(tenantPayment.getTenant());
                tp.setName(tenantPayment.getName());
                tp.setUIDTenant(tenantPayment.getUIDTenant());
                tp.setDescription("Successful payment");
                tp.setStripeCustomerId(session.getCustomer());
                tp.setStatus(session.getPaymentStatus());
                tp.setUpdatedAt(LocalDateTime.now());
                tp.setAmount(session.getAmountTotal());
                tp.setStripePaymentId(session.getId());
                tp.setStripeMode(session.getMode());
                tp.setStripePaymentMethodId(session.getPaymentMethodConfigurationDetails().getId());
                tp.setUserEmail(tenantPayment.getUserEmail());
                tenantPaymentRepository.save(tp);
                // Activa tenant
                Tenant tenant = tenantPayment.getTenant();
                tenant.setActive(true);
                tenantRepository.save(tenant);
                PreRegistro preRegistro = preRegistroRepository.findByEmail(tenantPayment.getUserEmail()).orElse(null);
                if (preRegistro == null) {
                    log.error("PreRegistro not found for email: {}", session.getCustomerEmail());
                }else{
                    preRegistro.setStatus(COMPLETED); // Payment completed
                    preRegistro.setDescription("Payment completed by Stripe");
                    preRegistro.setUpdatedDate(LocalDateTime.now());
                    preRegistroRepository.save(preRegistro);
                }
                String jwtToken = tokenService.generateToken(tenant.getId(),session.getCustomerEmail());
                log.info("Generated JWT Token for tenantId {}: {}", tenant.getId(), jwtToken);
                // Enviar email de bienvenida
                EmailDTO emailDTO = EmailDTO.builder()
                        .to(tenantPayment.getUserEmail())
                        .subject("Gracias por registrarte en Lealtix")
                        .templateId(sendGridTemplates.getWelcomeTemplate())
                        .dynamicData(Map.of(
                                "name", tenantPayment.getName(),
                                "link", "http://localhost:4200/admin/wizard?token="+jwtToken,
                                "logoUrl", "http://cdn.mcauto-images-production.sendgrid.net/b30f9991de8e45d3/af636f80-aa14-4886-9b12-ff4865e26908/627x465.png",
                                "password", "123qwe...",
                                "username", tenantPayment.getUserEmail()
                        ))
                        .build();
                emailservice.sendEmailWithTemplate(emailDTO);
            }
            return pagoDto;
        } catch (StripeException | IOException e) {
            log.error("Error retrieving checkout session: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public PagoDto getCheckoutSessionCancel(String sessionId) {
        try {
            PagoDto pagoDto = null;
            Session session = Session.retrieve(sessionId);
            String tenantId = session.getClientReferenceId();
            TenantPayment tenantPayment = tenantPaymentRepository.findByUIDTenantAndStatus(tenantId, "INITIATED");
            if (tenantPayment != null) {
                pagoDto = new PagoDto();
                pagoDto.setName(tenantPayment.getName());
                pagoDto.setStatus(session.getPaymentStatus());

                tenantPayment.setDescription("Payment Canceled");
                tenantPayment.setStripeCustomerId(session.getCustomer());
                tenantPayment.setStatus("Canceled");
                tenantPayment.setUpdatedAt(LocalDateTime.now());
                tenantPaymentRepository.save(tenantPayment);
            }

            return pagoDto;
        } catch (StripeException e) {
            log.error("Error retrieving checkout session: {}", e.getMessage());
            return null;
        }
    }

    private String createSlug(Tenant tenant) {
        if (tenant != null) {
            String name = tenant.getNombreNegocio().toLowerCase().replaceAll("[^a-z0-9]+", "-");
            String uniqueId = String.valueOf(tenant.getId());
            String slug = name + "-" + uniqueId;
            tenant.setSlug(slug);
            tenantRepository.save(tenant);
            return slug;
        }
        return null;
    }

}
