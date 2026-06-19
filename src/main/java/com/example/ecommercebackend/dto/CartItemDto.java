package com.example.ecommercebackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartItemDto {
    private UUID id;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private ProductDto product;
}
