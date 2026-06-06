package com.example.shoppingcartapi.mapper;

import com.example.shoppingcartapi.dto.CartDto;
import com.example.shoppingcartapi.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartMapper {
    private final ModelMapper modelMapper;

    public CartDto ToCartDto(Cart cart) {
        return modelMapper.map(cart, CartDto.class);
    }

    public Cart ToCart(CartDto cartDto) {
        return modelMapper.map(cartDto, Cart.class);
    }
}
