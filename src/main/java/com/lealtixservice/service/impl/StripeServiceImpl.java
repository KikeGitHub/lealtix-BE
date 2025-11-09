package com.lealtixservice.service.impl;

import com.lealtixservice.config.SendGridTemplates;
import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.*;
import com.lealtixservice.repository.PreRegistroRepository;
import com.lealtixservice.repository.TenantPaymentRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.service.*;
import com.lealtixservice.util.DateUtils;
import com.lealtixservice.util.EncrypUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
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
    private AppUserService appUserService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PreRegistroRepository preRegistroRepository;

    @Autowired
    private Emailservice emailservice;

    @Autowired
    private TokenService tokenService;

    private Price lastCreatedPrice;
    ;

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
        } catch (Exception e) {
            log.error("Error creating checkout session: {}", e.getMessage());
            throw new Exception("Error creating checkout session");
        }
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", session.getId());
        return responseData;
    }

    @Override
    public PagoDto getCheckoutSession(String sessionId) throws StripeException {
        PagoDto pagoDto = null;
        try {
            Session session = Session.retrieve(sessionId);
            Long userId = Long.valueOf(session.getClientReferenceId());
            AppUser user = appUserService.findById(userId).orElseThrow(
                    () -> new RuntimeException("User not found with id: " + userId)
            );

            TenantPayment tp = TenantPayment.builder().build();
            tp.setCreatedAt(LocalDateTime.now());
            tp.setEndDate(LocalDateTime.now());
            tp.setPlan("Básico"); // TODO Asignar plan básico
            tp.setStartDate(LocalDateTime.now());
            tp.setTenant(null);
            tp.setName(user.getFullName());
            tp.setUIDTenant("");
            tp.setDescription("Successful payment");
            tp.setStripeCustomerId(session.getCustomer());
            tp.setStatus(session.getPaymentStatus());
            tp.setUpdatedAt(LocalDateTime.now());
            tp.setAmount(session.getAmountTotal());
            tp.setStripePaymentId(session.getId());
            tp.setStripeMode(session.getMode());
            tp.setStripeSubscriptionId(session.getSubscription());
            tp.setStripePaymentMethodId(session.getPaymentMethodConfigurationDetails().getId());
            tp.setUserEmail(user.getEmail());
            tp.setAppUser(user);
            tenantPaymentRepository.save(tp);

            // llena pagoDto con datos de tenantPayment
            pagoDto = new PagoDto();
            pagoDto.setName(tp.getName());
            pagoDto.setStatus(tp.getStatus());
            pagoDto.setCost(tp.getAmount().toString());
            pagoDto.setCurrency(session.getCurrency());
            pagoDto.setPaymentDate(DateUtils.formatDatefromLong(session.getCreated()));
            pagoDto.setNextPaymentDate(DateUtils.formatDatefromLongNext(session.getCreated()));
            pagoDto.setDescription(tp.getDescription());
            pagoDto.setPlan(tp.getPlan());

            PreRegistro preRegistro = preRegistroRepository.findByEmail(user.getEmail()).orElse(null);
            if (preRegistro == null) {
                throw new RuntimeException("preRegistro not found with id: " + user.getEmail());
            } else {
                preRegistro.setStatus(COMPLETED); // Payment completed
                preRegistro.setDescription("Payment completed by Stripe");
                preRegistro.setUpdatedDate(LocalDateTime.now());
                preRegistroRepository.save(preRegistro);
            }

            // Actualiza invitacion usada
            Invitation invitation = invitationService.getInviteByEmail(user.getEmail());
            if (invitation != null) {
                invitation.setUsedAt(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant());
                invitationService.save(invitation);
            }


            String jwtToken = tokenService.generateToken(user.getId(), user.getEmail());
            log.info("Generated JWT Token for tenantId {}: {}", user.getId(), jwtToken);
            // Enviar email de bienvenida
            EmailDTO emailDTO = EmailDTO.builder()
                    .to(user.getEmail())
                    .subject("Gracias por registrarte en Lealtix")
                    .templateId(sendGridTemplates.getWelcomeTemplate())
                    .dynamicData(Map.of(
                            "name", user.getFullName(),
                            "link", "http://localhost:4200/admin/wizard?token=" + jwtToken,
                            "logoUrl", "http://cdn.mcauto-images-production.sendgrid.net/b30f9991de8e45d3/af636f80-aa14-4886-9b12-ff4865e26908/627x465.png",
                            "password", EncrypUtils.decrypPassword(user.getPasswordHash()),
                            "username", user.getEmail()
                    ))
                    .build();
            emailservice.sendEmailWithTemplate(emailDTO);
        } catch (StripeException | IOException e) {
            log.error("Error retrieving checkout session: {}", e.getMessage());
            return null;
        }
        return pagoDto;
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

    @Override
    public Map<String, Object> createPaymentIntent(PagoDto pagoDto) throws Exception {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", String.valueOf(pagoDto.getUserId()));

        // Primero crea (o busca) un customer en Stripe
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("email", pagoDto.getEmail());
        customerParams.put("name", pagoDto.getName());
        Customer customer = Customer.create(customerParams);

        Map<String, Object> params = new HashMap<>();
        params.put("amount", pagoDto.getAmount()); // en centavos
        params.put("currency", pagoDto.getCurrency());
        params.put("description", "Suscripción Lealtix - " + pagoDto.getPlan());
        params.put("receipt_email", pagoDto.getEmail());
        params.put("customer", customer.getId());
        params.put("metadata", metadata);

        Map<String, Object> autoPayment = new HashMap<>();
        autoPayment.put("enabled", true);
        params.put("automatic_payment_methods", autoPayment);

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        return response;
    }

}
