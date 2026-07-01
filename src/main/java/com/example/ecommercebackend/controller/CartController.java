package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.CartDto;
import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.UserSummaryDto;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.cart.ICartService;
import com.example.ecommercebackend.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Get my cart", description = "Retrieve the authenticated user's cart with items")
    public ResponseEntity<ApiResponse> getMyCart() {
        UserSummaryDto user = userService.getAuthenticatedUser();
        CartDto cart = cartService.getCartByUserId(user.getId());
        return ResponseEntity.ok(new ApiResponse("success", cart));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Remove all items from the authenticated user's cart")
    public ResponseEntity<ApiResponse> clearMyCart() {
        UserSummaryDto user = userService.getAuthenticatedUser();
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(new ApiResponse("Clear cart success", null));
    }

    @GetMapping("/total-price")
    @Operation(summary = "Get cart total", description = "Get the total price of the authenticated user's cart")
    public ResponseEntity<ApiResponse> getTotalAmount() {
        UserSummaryDto user = userService.getAuthenticatedUser();
        BigDecimal totalPrice = cartService.getTotalPrice(user.getId());
        return ResponseEntity.ok(new ApiResponse("Total Price", totalPrice));
    }
}
