package com.example.shoppingcartapi.controller;

import com.example.shoppingcartapi.dto.OrderDto;
import com.example.shoppingcartapi.dto.UserDto;
import com.example.shoppingcartapi.dto.request.AddOrderRequest;
import com.example.shoppingcartapi.dto.response.ApiResponse;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.service.order.IOrderService;
import com.example.shoppingcartapi.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;
    private final IUserService userService;

    @PostMapping()
    public ResponseEntity<ApiResponse> createOrder(
            @Valid @RequestBody AddOrderRequest request
            ) {
        try {
            UserDto user = userService.getAuthenticatedUser();
            OrderDto order = orderService.placeOrder(user.getId(), request.getShippingAddress());
            return ResponseEntity.ok(new ApiResponse("Order created successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse("Success", orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable UUID orderId) {
        try {
            UserDto user = userService.getAuthenticatedUser();
            OrderDto order = orderService.getOrderById(orderId, user.getId());
            return ResponseEntity.ok(new ApiResponse("Success", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getOrderByIdForAdmin(@PathVariable UUID orderId) {
        try {
            OrderDto order = orderService.getOrderByIdForAdmin(orderId);
            return ResponseEntity.ok(new ApiResponse("Success", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse> getMyOrders() {
        try {
            UserDto user = userService.getAuthenticatedUser();
            List<OrderDto> order = orderService.getUserOrders(user.getId());
            return ResponseEntity.ok(new ApiResponse("Item order success", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable UUID orderId) {
        try {
            UserDto user = userService.getAuthenticatedUser();
            orderService.cancelOrder(orderId, user.getId());
            return ResponseEntity.ok(new ApiResponse("Order cancelled successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PatchMapping("/{orderId}/order-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status
    ) {
        try {
            OrderDto order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(new ApiResponse("Order status updated to " + status, order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Invalid status provided", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An unexpected error occurred", null));
        }
    }
}
