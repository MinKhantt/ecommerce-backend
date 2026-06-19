package com.example.ecommercebackend.service.cart;

import com.example.ecommercebackend.dto.CartDto;
import com.example.ecommercebackend.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public interface ICartService {

    CartDto getCartByUserId(UUID userId);

    void clearCart(UUID userId);

    BigDecimal getTotalPrice(UUID userId);

    void initNewCart(User userId);
}
