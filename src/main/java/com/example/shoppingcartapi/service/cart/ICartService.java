package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.entity.Cart;
import com.example.shoppingcartapi.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public interface ICartService {
    Cart getCart(UUID id);
    void clearCart(UUID id);
    BigDecimal getTotalPrice(UUID id);

    Cart initNewCart(User user);

    Cart getCartByUserId(UUID userId);
}
