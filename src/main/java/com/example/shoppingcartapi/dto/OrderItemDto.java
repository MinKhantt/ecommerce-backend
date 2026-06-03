package com.example.shoppingcartapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemDto {
    private UUID productId;
    private String productName;
    private String productBrand;
    private int quantity;
    private BigDecimal price;
}
