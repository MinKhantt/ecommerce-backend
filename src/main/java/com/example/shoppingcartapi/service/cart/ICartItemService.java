package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.dto.CartItemDto;
import com.example.shoppingcartapi.dto.UserDto;

import java.util.UUID;

public interface ICartItemService {
    CartItemDto addItemToCart(UUID productId, int quantity, UserDto userDto);

    void updateItemQuantity(UUID productId, int quantity, UserDto userDto);

    void removeItemFromCart(UUID productId, UserDto userDto);

    CartItemDto getCartItem(UUID cartId, UUID productId);
}
