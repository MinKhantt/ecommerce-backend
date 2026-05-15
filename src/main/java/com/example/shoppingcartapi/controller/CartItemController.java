package com.example.shoppingcartapi.controller;


import com.example.shoppingcartapi.dto.response.ApiResponse;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.service.cart.ICartItemService;
import com.example.shoppingcartapi.service.cart.ICartService;
import com.example.shoppingcartapi.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cartItems")
public class CartItemController {
    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IUserService userService;

//    @PostMapping("/item/add")
//    public ResponseEntity<ApiResponse> addItemToCart(
//            @RequestParam Long productId,
//            @RequestParam Integer quantity
//    ) {
//        try {
//            UserDto user = userService.getAuthenticatedUser();
//            Cart cart = cartService.initNewCart(user);
//
//            cartItemService.addItemToCart(cart.getId(), productId, quantity);
//            return ResponseEntity.ok(new ApiResponse("Add item success", null));
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.status(NOT_FOUND)
//                    .body(new ApiResponse(e.getMessage(), null));
//        }
//        catch (JwtException e) {
//            return ResponseEntity.status(UNAUTHORIZED)
//                    .body(new ApiResponse(e.getMessage(), null));
//        }
//    }

    @DeleteMapping("/cart/{cartId}/item/{itemId}/remove")
    public ResponseEntity<ApiResponse> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long itemId
    ) {
        try {
            cartItemService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok(new ApiResponse("Delete item success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/cart/{cartId}/item/{itemId}/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity
    ) {
        try {
            cartItemService.updateItemQuantity(cartId, itemId, quantity);
            return ResponseEntity.ok(new ApiResponse("Update item success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }

    }
}
