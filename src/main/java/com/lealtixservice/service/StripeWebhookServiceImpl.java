package com.lealtixservice.service;

import com.lealtixservice.config.SendGridTemplates;
import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.*;
import com.lealtixservice.repository.PreRegistroRepository;
import com.lealtixservice.repository.TenantPaymentRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.util.DateUtils;
import com.lealtixservice.util.EncrypUtils;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class StripeWebhookServiceImpl implements StripeWebhookService {

    public static final String COMPLETED = "COMPLETED";
    private final SendGridTemplates sendGridTemplates;


    @Autowired
    private AppUserService appUserService;

    @Autowired
    private TenantPaymentRepository tenantPaymentRepository;

    @Autowired
    private PreRegistroRepository preRegistroRepository;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Emailservice emailservice;

    @Autowired
    private TenantRepository tenantRepository;

    public StripeWebhookServiceImpl(@Value("${stripe.api.key}") String apiKey, SendGridTemplates sendGridTemplates) {
        this.sendGridTemplates = sendGridTemplates;
        Stripe.apiKey = apiKey;
    }

    @Override
    public PagoDto handleCheckoutSessionCompleted(Event event) {
        PagoDto pagoDto = null;
        try {
            StripeObject obj = event.getDataObjectDeserializer().getObject()
                    .orElseThrow(() -> new IllegalArgumentException("Stripe object is missing"));
            if (!(obj instanceof Session)) {
                throw new IllegalArgumentException("Expected Session object");
            }
            Session session = (Session) obj;

            log.info("✅ checkout.session.completed recibido:");
            log.info("ID de sesión: {}", session.getId());
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
            try {
                if (session.getPaymentMethodConfigurationDetails() != null) {
                    tp.setStripePaymentMethodId(session.getPaymentMethodConfigurationDetails().getId());
                }
            } catch (Exception e) {
                log.debug("paymentMethodConfigurationDetails missing or not expanded: {}", e.getMessage());
            }
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

        } catch (Exception e) {
            log.error("Error retrieving checkout session: {}", e.getMessage());
            return null;
        }

        return pagoDto;
    }

    @Override
    public void handlePaymentIntentSucceeded(Event event) {
        PagoDto pagoDto = null;
        try {
            StripeObject obj = event.getDataObjectDeserializer().getObject()
                    .orElseThrow(() -> new IllegalArgumentException("Stripe object is missing"));
            if (!(obj instanceof PaymentIntent)) {
                throw new IllegalArgumentException("Expected PaymentIntent object");
            }
            PaymentIntent intent = (PaymentIntent) obj;

            Long userId = Long.valueOf(intent.getMetadata().get("userId"));
            String plan = intent.getMetadata().get("plan");

            log.info("✅ checkout.session.completed recibido:");
            log.info("ID de intent: {}", intent.getId());
            AppUser user = appUserService.findById(userId).orElseThrow(
                    () -> new RuntimeException("User not found with id: " + userId)
            );

            TenantPayment tp = TenantPayment.builder().build();
            tp.setCreatedAt(LocalDateTime.now());
            tp.setEndDate(LocalDateTime.now());
            tp.setPlan(plan);
            tp.setStartDate(LocalDateTime.now());
            tp.setTenant(null);
            tp.setName(user.getFullName());
            tp.setUIDTenant(null);
            tp.setDescription("Successful payment");
            tp.setStripeCustomerId(intent.getCustomer());
            tp.setStatus(intent.getStatus());
            tp.setUpdatedAt(LocalDateTime.now());
            tp.setAmount(intent.getAmount());
            tp.setStripePaymentId(intent.getId());
            tp.setStripeMode("payment_intent");
            tp.setStripeSubscriptionId("");
            try {
                if (intent.getPaymentMethodConfigurationDetails() != null) {
                    tp.setStripePaymentMethodId(intent.getPaymentMethodConfigurationDetails().getId());
                }
            } catch (Exception e) {
                log.debug("paymentMethodConfigurationDetails missing or not expanded: {}", e.getMessage());
            }
            tp.setUserEmail(user.getEmail());
            tp.setAppUser(user);
            tenantPaymentRepository.save(tp);

            // llena pagoDto con datos de tenantPayment
            pagoDto = new PagoDto();
            pagoDto.setName(tp.getName());
            pagoDto.setStatus(tp.getStatus());
            pagoDto.setCost(tp.getAmount().toString());
            pagoDto.setCurrency(intent.getCurrency());
            pagoDto.setPaymentDate(DateUtils.formatDatefromLong(intent.getCreated()));
            pagoDto.setNextPaymentDate(DateUtils.formatDatefromLongNext(intent.getCreated()));
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

        } catch (Exception e) {
            log.error("Error retrieving checkout session: {}", e.getMessage());
        }
    }

    @Override
    public void handlePaymentIntentFailed(Event event) {
        try {
            StripeObject obj = event.getDataObjectDeserializer().getObject()
                    .orElseThrow(() -> new IllegalArgumentException("Stripe object is missing"));
            if (!(obj instanceof PaymentIntent)) {
                throw new IllegalArgumentException("Expected PaymentIntent object");
            }
            PaymentIntent intent = (PaymentIntent) obj;

            System.out.println("❌ Pago fallido:");
            System.out.println("ID: " + intent.getId());
            System.out.println("Error: " + intent.getLastPaymentError());

            // Aquí podrías marcar el pago como fallido o notificar al usuario

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}