package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.cart.ICartItemService;
import com.example.ecommercebackend.service.user.IUserService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cartItems")
@Validated
public class CartItemController {

    private final ICartItemService cartItemService;
    private final IUserService userService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(
            @RequestParam UUID productId,
            @RequestParam @Min(1) int quantity
    ) {
        UUID userId = userService.getAuthenticatedUser().getId();
        cartItemService.addItemToCart(productId, quantity, userId);
        return ResponseEntity.ok(new ApiResponse("Add item success", null));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse> updateItemQuantity(
            @PathVariable UUID productId,
            @RequestParam Integer quantity
    ) {
        UUID userId = userService.getAuthenticatedUser().getId();
        cartItemService.updateItemQuantity(productId, quantity, userId);
        return ResponseEntity.ok(new ApiResponse("Update item success", null));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse> removeItemFromCart(
            @PathVariable UUID productId
    ) {
        UUID userId = userService.getAuthenticatedUser().getId();
        cartItemService.removeItemFromCart(productId, userId);
        return ResponseEntity.ok(new ApiResponse("Delete item success", null));
    }
}
