package com.example.ecommercebackend.util;

import com.example.ecommercebackend.dto.request.AddPaymentRequest;
import com.example.ecommercebackend.dto.response.PaymentIntentResponse;
import com.example.ecommercebackend.entity.Order;
import com.example.ecommercebackend.entity.Payment;
import com.example.ecommercebackend.enums.OrderStatus;
import com.example.ecommercebackend.enums.PaymentMethod;
import com.example.ecommercebackend.enums.PaymentProvider;
import com.example.ecommercebackend.enums.PaymentStatus;
import com.example.ecommercebackend.repository.OrderRepository;
import com.example.ecommercebackend.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentUtil {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final MailUtil mailUtil;

    public PaymentIntentResponse handleCodPayment(Order order) {
        Payment payment = createBasePayment(order, PaymentMethod.CASH_ON_DELIVERY, PaymentProvider.NONE);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setCurrency("MMK");
        paymentRepository.save(payment);

        // send mail to user
        OrderEmailContextUtil ctx = OrderEmailContextUtil.from(order, payment);
        mailUtil.sendOrderConfirmation(order.getUser().getEmail(), ctx);

        return new PaymentIntentResponse(payment.getId(), null, order.getTotalAmount(), "usd", PaymentStatus.PENDING.name());
    }

    public PaymentIntentResponse createStripePayment(UUID userId, Order order, AddPaymentRequest request) {

        BigDecimal amount = order.getTotalAmount();
        String currency = request.getCurrency() != null ? request.getCurrency().toLowerCase() : "usd";

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency(currency)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build()
                    )
                    .putMetadata("order_id", order.getId().toString())
                    .putMetadata("user_id", userId.toString())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Payment payment = createBasePayment(order, PaymentMethod.valueOf(request.getPaymentMethod()), PaymentProvider.STRIPE);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setExternalTransactionId(paymentIntent.getId());
            payment.setCurrency(currency);

            paymentRepository.save(payment);

            return new PaymentIntentResponse(
                    payment.getId(),
                    paymentIntent.getClientSecret(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    PaymentStatus.PENDING.name()
            );

        } catch (StripeException e) {
            log.error("Stripe payment intent creation failed: {}", e.getMessage());
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    public PaymentIntentResponse handleLocalWalletPayment(Order order, AddPaymentRequest request) {
        Payment payment = createBasePayment(order, PaymentMethod.DIGITAL_WALLET, PaymentProvider.valueOf(request.getPaymentProvider().toUpperCase()));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setCurrency("MMK");
        paymentRepository.save(payment);

        // send mail to user
        OrderEmailContextUtil ctx = OrderEmailContextUtil.from(order, payment);
        mailUtil.sendOrderConfirmation(order.getUser().getEmail(), ctx);

        return new PaymentIntentResponse(payment.getId(), "QR_CODE_URL_OR_DEEP_LINK", order.getTotalAmount(), "usd", PaymentStatus.PENDING.name());
    }

    private Payment createBasePayment(Order order, PaymentMethod method, PaymentProvider provider) {
        Payment payment = new Payment();
        payment.setAmount(order.getTotalAmount());
        payment.setOrder(order);
        payment.setUser(order.getUser());
        payment.setPaymentMethod(method);
        payment.setPaymentProvider(provider);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCurrency("usd");
        return payment;
    }

    @Transactional
    public void processWebhookEvent(Event event) {
        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed(event);
            case "payment_intent.requires_action" -> handlePaymentIntentRequiresAction(event);
            default -> log.info("Unhandled webhook event type: {}", event.getType());
        }
    }

    private String extractPaymentIntentId(Event event) {
        try {
            String rawJson = event.getDataObjectDeserializer().getRawJson();
            JsonNode root = objectMapper.readTree(rawJson);
            return root.get("id").asText();
        } catch (Exception e) {
            log.error("Failed to extract PaymentIntent ID from event: {}", e.getMessage());
            throw new RuntimeException("Failed to extract PaymentIntent ID from event");
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        try {
            String piId = extractPaymentIntentId(event);
            PaymentIntent paymentIntent = PaymentIntent.retrieve(piId);

            paymentRepository.findByExternalTransactionId(paymentIntent.getId())
                    .ifPresent(payment -> {
                        if (payment.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
                            log.info("Payment already marked as succeeded: {}", payment.getId());
                            return;
                        }

                        payment.setPaymentStatus(PaymentStatus.SUCCEEDED);
                        payment.setExternalTransactionId(paymentIntent.getId());
                        paymentRepository.save(payment);

                        Order order = payment.getOrder();
                        order.setOrderStatus(OrderStatus.PROCESSING);
                        orderRepository.save(order);
                        log.info("Payment succeeded for order: {}", order.getId());

                        // send mail to user
                        OrderEmailContextUtil ctx = OrderEmailContextUtil.from(order, payment);
                        mailUtil.sendOrderConfirmation(order.getUser().getEmail(), ctx);
                    });
        } catch (Exception e) {
            log.error("Error handling payment_intent.succeeded: {}", e.getMessage());
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        try {
            String piId = extractPaymentIntentId(event);
            PaymentIntent paymentIntent = PaymentIntent.retrieve(piId);

            paymentRepository.findByExternalTransactionId(paymentIntent.getId())
                    .ifPresent(payment -> {
                        if (payment.getPaymentStatus() == PaymentStatus.FAILED) {
                            log.info("Payment already marked as failed: {}", payment.getId());
                            return;
                        }

                        payment.setPaymentStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);
                        log.info("Payment failed for order: {}", payment.getOrder().getId());
                    });
        } catch (Exception e) {
            log.error("Error handling payment_intent.payment_failed: {}", e.getMessage());
        }
    }

    private void handlePaymentIntentRequiresAction(Event event) {
        try {
            String piId = extractPaymentIntentId(event);
            PaymentIntent paymentIntent = PaymentIntent.retrieve(piId);

            paymentRepository.findByExternalTransactionId(paymentIntent.getId())
                    .ifPresent(payment -> {
                        if (payment.getPaymentStatus() == PaymentStatus.REQUIRES_ACTION) {
                            log.info("Payment already marked as requires action: {}", payment.getId());
                            return;
                        }

                        payment.setPaymentStatus(PaymentStatus.REQUIRES_ACTION);
                        paymentRepository.save(payment);
                        log.info("Payment requires additional action for order: {}", payment.getOrder().getId());
                    });
        } catch (Exception e) {
            log.error("Error handling payment_intent.requires_action: {}", e.getMessage());
        }
    }
}