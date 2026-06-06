package com.example.shoppingcartapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderSummaryDto {
    private UUID id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
}
