package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.CartDto;
import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.cart.ICartService;
import com.example.ecommercebackend.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
public class CartController {

    private final ICartService cartService;
    private final IUserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse> getMyCart() {
        UserDto user = userService.getAuthenticatedUser();
        CartDto cart = cartService.getCartByUserId(user.getId());
        return ResponseEntity.ok(new ApiResponse("success", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearMyCart() {
        UserDto user = userService.getAuthenticatedUser();
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(new ApiResponse("Clear cart success", null));
    }

    @GetMapping("/total-price")
    public ResponseEntity<ApiResponse> getTotalAmount() {
        UserDto user = userService.getAuthenticatedUser();
        BigDecimal totalPrice = cartService.getTotalPrice(user.getId());
        return ResponseEntity.ok(new ApiResponse("Total Price", totalPrice));
    }
}
