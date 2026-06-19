package com.example.ecommercebackend.service.cart;

import com.example.ecommercebackend.dto.CartDto;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.entity.Cart;
import com.example.ecommercebackend.entity.User;
import com.example.ecommercebackend.mapper.CartMapper;
import com.example.ecommercebackend.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService implements ICartService{

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Override
    public CartDto getCartByUserId(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found!"));
        return cartMapper.ToCartDto(cart);
    }

    @Override
    public void clearCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found!"));

        cart.getItems().clear();
        cart.updateTotalAmount();
    }

    @Override
    public BigDecimal getTotalPrice(UUID userId) {
        return cartRepository.findByUserId(userId)
                .map(Cart::getTotalAmount)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void initNewCart(User user){
        cartRepository.findByUserId(user.getId())
                .map(cartMapper::ToCartDto)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartMapper.ToCartDto(cartRepository.save(newCart));
                });
    }
}
