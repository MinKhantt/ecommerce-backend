package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.entity.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);

    void updateItemQuantity(Long cartId, Long productId, int quantity);

    void removeItemFromCart(Long cartId, Long productId);

    CartItem getCartItem(Long cartId, Long productId);
}
