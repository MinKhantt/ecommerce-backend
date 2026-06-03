package com.example.shoppingcartapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
public class CartDto {
    private UUID id;
    private BigDecimal totalAmount;
    private Set<CartItemDto> cartItems;
}
