package com.example.shoppingcartapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingAddress;
    private List<OrderItemDto> orderItems;
    private UserSummaryDto user;
}
