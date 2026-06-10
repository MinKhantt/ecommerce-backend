package com.example.shoppingcartapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentIntentResponse {
    private UUID paymentId;
    private String clientSecret;
    private BigDecimal amount;
    private String currency;
    private String paymentStatus;
}
