package com.example.ecommercebackend.mapper;

import com.example.ecommercebackend.dto.CartDto;
import com.example.ecommercebackend.entity.Cart;
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
