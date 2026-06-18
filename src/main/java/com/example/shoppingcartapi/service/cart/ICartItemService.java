package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.dto.CartItemDto;

import java.util.UUID;

public interface ICartItemService {
    CartItemDto addItemToCart(UUID productId, int quantity, UUID userId);

    void updateItemQuantity(UUID productId, int quantity, UUID userId);

    void removeItemFromCart(UUID productId, UUID userId);

    CartItemDto getCartItem(UUID cartId, UUID productId);
}
