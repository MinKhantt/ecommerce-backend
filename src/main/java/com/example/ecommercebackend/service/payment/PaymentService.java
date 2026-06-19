package com.example.ecommercebackend.service.payment;

import com.example.ecommercebackend.config.StripeConfig;
import com.example.ecommercebackend.dto.PaymentDto;
import com.example.ecommercebackend.dto.request.AddPaymentRequest;
import com.example.ecommercebackend.dto.response.PaymentIntentResponse;
import com.example.ecommercebackend.entity.Order;
import com.example.ecommercebackend.entity.Payment;
import com.example.ecommercebackend.enums.OrderStatus;
import com.example.ecommercebackend.enums.PaymentProvider;
import com.example.ecommercebackend.enums.PaymentStatus;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.util.PaymentUtil;
import com.example.ecommercebackend.mapper.PaymentMapper;
import com.example.ecommercebackend.repository.OrderRepository;
import com.example.ecommercebackend.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCancelParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final StripeConfig stripeConfig;
    private final PaymentUtil paymentUtil;

    @Override
    public PaymentIntentResponse processPayment(UUID userId, AddPaymentRequest request) {
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Payment can only be created for pending orders");
        }
        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalStateException("A payment already exists for this order");
        }

        return switch (PaymentProvider.valueOf(request.getPaymentProvider().toUpperCase())) {
            case STRIPE -> paymentUtil.createStripePayment(userId, order, request);
            case K_PAY -> paymentUtil.handleLocalWalletPayment(order, request);
            case CASH_ON_DELIVERY -> paymentUtil.handleCodPayment(order);
            default -> paymentUtil.handleCodPayment(order);
        };
    }

    @Override
    public List<PaymentDto> getUserPayments(UUID userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toPaymentDto)
                .toList();
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toPaymentDto)
                .toList();
    }

    @Override
    public PaymentDto getPaymentById(UUID id, UUID userId) {
        Payment payment = paymentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found or access denied"));
        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    public PaymentDto updatePaymentStatus(UUID id, String paymentStatus) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        PaymentStatus newStatus = PaymentStatus.valueOf(paymentStatus.toUpperCase());
        payment.setPaymentStatus(newStatus);
        paymentRepository.save(payment);
        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    public void cancelPayment(UUID id, UUID userId) {
        Payment payment = paymentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found or access denied"));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING
                && payment.getPaymentStatus() != PaymentStatus.REQUIRES_ACTION) {
            throw new IllegalStateException("Only pending payments can be cancelled");
        }

        try {
            PaymentIntent resource = PaymentIntent.retrieve(payment.getExternalTransactionId());
            PaymentIntentCancelParams params = PaymentIntentCancelParams.builder().build();
            resource.cancel(params);
        } catch (StripeException e) {
            log.error("Failed to cancel Stripe PaymentIntent: {}", e.getMessage());
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    @Override
    public void handleWebhookEvent(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
            paymentUtil.processWebhookEvent(event);
        } catch (Exception e) {
            log.error("Webhook processing error: ", e);
            throw new RuntimeException(e);
        }
    }
}
