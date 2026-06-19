package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.CartDto;
import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.service.cart.ICartService;
import com.example.ecommercebackend.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/carts")
public class CartController {

    private final ICartService cartService;
    private final IUserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse> getMyCart() {
        try {
            UserDto user = userService.getAuthenticatedUser();
            CartDto cart = cartService.getCartByUserId(user.getId());
            return ResponseEntity.ok(new ApiResponse("success", cart));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearMyCart() {
        try {
            UserDto user = userService.getAuthenticatedUser();
            cartService.clearCart(user.getId());
            return ResponseEntity.ok(new ApiResponse("Clear cart success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/total-price")
    public ResponseEntity<ApiResponse> getTotalAmount() {
        try {
            UserDto user = userService.getAuthenticatedUser();
            BigDecimal totalPrice = cartService.getTotalPrice(user.getId());
            return ResponseEntity.ok(new ApiResponse("Total Price", totalPrice));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}
