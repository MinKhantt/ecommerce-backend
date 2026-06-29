package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.OrderDto;
import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.UserSummaryDto;
import com.example.ecommercebackend.dto.request.AddOrderRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.order.IOrderService;
import com.example.ecommercebackend.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        UserSummaryDto user = userService.getAuthenticatedUser();
        OrderDto order = orderService.placeOrder(user.getId(), request.getShippingAddress());
        return ResponseEntity.ok(new ApiResponse("Order created successfully", order));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(new ApiResponse("Success", orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable UUID orderId) {
        UserSummaryDto user = userService.getAuthenticatedUser();
        OrderDto order = orderService.getOrderById(orderId, user.getId());
        return ResponseEntity.ok(new ApiResponse("Success", order));
    }

    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getOrderByIdForAdmin(@PathVariable UUID orderId) {
        OrderDto order = orderService.getOrderByIdForAdmin(orderId);
        return ResponseEntity.ok(new ApiResponse("Success", order));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse> getMyOrders() {
        UserSummaryDto user = userService.getAuthenticatedUser();
        List<OrderDto> order = orderService.getUserOrders(user.getId());
        return ResponseEntity.ok(new ApiResponse("Item order success", order));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable UUID orderId) {
        UserSummaryDto user = userService.getAuthenticatedUser();
        orderService.cancelOrder(orderId, user.getId());
        return ResponseEntity.ok(new ApiResponse("Order cancelled successfully", null));
    }

    @PatchMapping("/{orderId}/order-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status
    ) {
        OrderDto order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(new ApiResponse("Order status updated to " + status, order));
    }
}
