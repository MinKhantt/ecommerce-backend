package com.example.ecommercebackend.service.payment;

import com.example.ecommercebackend.dto.PaymentDto;
import com.example.ecommercebackend.dto.request.AddPaymentRequest;
import com.example.ecommercebackend.dto.response.PaymentIntentResponse;

import java.util.List;
import java.util.UUID;

public interface IPaymentService {

    PaymentIntentResponse processPayment(UUID userId, AddPaymentRequest request);

    List<PaymentDto> getUserPayments(UUID userId);

    List<PaymentDto> getAllPayments();

    PaymentDto getPaymentById(UUID id, UUID userId);

    PaymentDto updatePaymentStatus(UUID id, String paymentStatus);

    void cancelPayment(UUID id, UUID userId);

    void handleWebhookEvent(String payload, String sigHeader);
}
