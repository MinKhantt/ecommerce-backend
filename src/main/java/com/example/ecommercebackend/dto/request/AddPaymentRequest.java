package com.example.ecommercebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddPaymentRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Payment provider is required")
    private String paymentProvider;

    private String currency = "USD";
}
