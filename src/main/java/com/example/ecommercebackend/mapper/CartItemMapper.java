package com.example.ecommercebackend.mapper;

import com.example.ecommercebackend.dto.CartItemDto;
import com.example.ecommercebackend.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemMapper {

    private final ModelMapper modelMapper;

    public CartItemDto ToCartItemDto(CartItem cartItem) {
        return modelMapper.map(cartItem, CartItemDto.class);
    }

    public CartItem ToCartItem(CartItemDto cartItemDto) {
        return modelMapper.map(cartItemDto, CartItem.class);
    }
}
