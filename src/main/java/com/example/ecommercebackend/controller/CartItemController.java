package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.cart.ICartItemService;
import com.example.ecommercebackend.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Add item to cart", description = "Add a product to the authenticated user's cart")
    public ResponseEntity<ApiResponse> addItemToCart(
            @RequestParam UUID productId,
            @RequestParam @Min(1) int quantity
    ) {
        UUID userId = userService.getAuthenticatedUser().getId();
        cartItemService.addItemToCart(productId, quantity, userId);
        return ResponseEntity.ok(new ApiResponse("Add item success", null));
    }

    @PutMapping("/update/{productId}")
    @Operation(summary = "Update cart item quantity", description = "Update the quantity of a cart item")
    public ResponseEntity<ApiResponse> updateItemQuantity(
            @PathVariable UUID productId,
            @RequestParam Integer quantity
    ) {
        UUID userId = userService.getAuthenticatedUser().getId();
        cartItemService.updateItemQuantity(productId, quantity, userId);
        return ResponseEntity.ok(new ApiResponse("Update item success", null));
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "Remove item from cart", description = "Remove a product from the authenticated user's cart")
    public ResponseEntity<ApiResponse> removeItemFromCart(
            @PathVariable UUID productId
    ) {
        UUID userId = userService.getAuthenticatedUser().getId();
        cartItemService.removeItemFromCart(productId, userId);
        return ResponseEntity.ok(new ApiResponse("Delete item success", null));
    }
}
