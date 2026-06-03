package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.entity.CartItem;

import java.util.UUID;

public interface ICartItemService {
    void addItemToCart(UUID cartId, UUID productId, int quantity);

    void updateItemQuantity(UUID cartId, UUID productId, int quantity);

    void removeItemFromCart(UUID cartId, UUID productId);

    CartItem getCartItem(UUID cartId, UUID productId);
}
