package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.entity.Cart;
import com.example.shoppingcartapi.entity.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initNewCart(User user);

    Cart getCartByUserId(Long userId);
}
