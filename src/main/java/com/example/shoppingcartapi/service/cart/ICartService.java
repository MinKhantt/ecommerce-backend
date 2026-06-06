package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.dto.CartDto;
import com.example.shoppingcartapi.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public interface ICartService {

    CartDto getCartByUserId(UUID userId);

    void clearCart(UUID userId);

    BigDecimal getTotalPrice(UUID userId);

    void initNewCart(User userId);
}
