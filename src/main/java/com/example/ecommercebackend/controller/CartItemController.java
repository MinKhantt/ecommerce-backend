package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.service.cart.ICartItemService;
import com.example.ecommercebackend.service.user.IUserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.ietf.jgss.GSSException.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cartItems")
public class CartItemController {

    private final ICartItemService cartItemService;
    private final IUserService userService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(
            @RequestParam UUID productId,
            @RequestParam Integer quantity
    ) {
        try {
            UUID userId = userService.getAuthenticatedUser().getId();
            cartItemService.addItemToCart(productId, quantity, userId);
            return ResponseEntity.ok(new ApiResponse("Add item success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (JwtException e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse> updateItemQuantity(
            @PathVariable UUID productId,
            @RequestParam Integer quantity
    ) {
        try {
            UUID userId = userService.getAuthenticatedUser().getId();
            cartItemService.updateItemQuantity(productId, quantity, userId);
            return ResponseEntity.ok(new ApiResponse("Update item success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse> removeItemFromCart(
            @PathVariable UUID productId
    ) {
        try {
            UUID userId = userService.getAuthenticatedUser().getId();
            cartItemService.removeItemFromCart(productId, userId);
            return ResponseEntity.ok(new ApiResponse("Delete item success", null));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("Access Denied: " + e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
