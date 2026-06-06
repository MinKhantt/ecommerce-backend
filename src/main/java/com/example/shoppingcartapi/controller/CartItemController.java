package com.example.shoppingcartapi.controller;

import com.example.shoppingcartapi.dto.UserDto;
import com.example.shoppingcartapi.dto.response.ApiResponse;
import com.example.shoppingcartapi.entity.User;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.mapper.UserMapper;
import com.example.shoppingcartapi.service.cart.ICartItemService;
import com.example.shoppingcartapi.service.cart.ICartService;
import com.example.shoppingcartapi.service.user.IUserService;
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
    private final ICartService cartService;
    private final IUserService userService;
    private final UserMapper userMapper;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(
            @RequestParam UUID productId,
            @RequestParam Integer quantity
    ) {
        try {
            UserDto userDto = userService.getAuthenticatedUser();
            User user = userMapper.userDtoToUser(userDto);
            cartService.initNewCart(user);

            cartItemService.addItemToCart(productId, quantity, userDto);
            return ResponseEntity.ok(new ApiResponse("Add item success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
        catch (JwtException e) {
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
            UserDto user = userService.getAuthenticatedUser();
            cartItemService.updateItemQuantity(productId, quantity, user);
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
            UserDto user = userService.getAuthenticatedUser();

            cartItemService.removeItemFromCart(productId, user);
            return ResponseEntity.ok(new ApiResponse("Delete item success", null));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("Access Denied: " + e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
