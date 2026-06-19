package com.example.ecommercebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private UUID id;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String paymentProvider;
    private String currency;
    private OrderSummaryDto order;
}
