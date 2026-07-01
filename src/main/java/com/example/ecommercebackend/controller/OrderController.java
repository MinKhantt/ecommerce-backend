package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.OrderDto;
import com.example.ecommercebackend.dto.UserDto;
import com.example.ecommercebackend.dto.UserSummaryDto;
import com.example.ecommercebackend.dto.request.AddOrderRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.order.IOrderService;
import com.example.ecommercebackend.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @Operation(summary = "Create order", description = "Place a new order from current cart contents")
    public ResponseEntity<ApiResponse> createOrder(
            @Valid @RequestBody AddOrderRequest request
            ) {
        UserSummaryDto user = userService.getAuthenticatedUser();
        OrderDto order = orderService.placeOrder(user.getId(), request.getShippingAddress());
        return ResponseEntity.ok(new ApiResponse("Order created successfully", order));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders", description = "Paginated list of all orders, admin only")
    public ResponseEntity<ApiResponse> getAllOrders(
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse("Success", orderService.getAllOrders(pageable)));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve a user's own order by UUID")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable UUID orderId) {
        UserSummaryDto user = userService.getAuthenticatedUser();
        OrderDto order = orderService.getOrderById(orderId, user.getId());
        return ResponseEntity.ok(new ApiResponse("Success", order));
    }

    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get order by ID (admin)", description = "Retrieve any order by UUID, admin only")
    public ResponseEntity<ApiResponse> getOrderByIdForAdmin(@PathVariable UUID orderId) {
        OrderDto order = orderService.getOrderByIdForAdmin(orderId);
        return ResponseEntity.ok(new ApiResponse("Success", order));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get my orders", description = "Retrieve the authenticated user's orders")
    public ResponseEntity<ApiResponse> getMyOrders() {
        UserSummaryDto user = userService.getAuthenticatedUser();
        List<OrderDto> order = orderService.getUserOrders(user.getId());
        return ResponseEntity.ok(new ApiResponse("Item order success", order));
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel order", description = "Cancel a pending order")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable UUID orderId) {
        UserSummaryDto user = userService.getAuthenticatedUser();
        orderService.cancelOrder(orderId, user.getId());
        return ResponseEntity.ok(new ApiResponse("Order cancelled successfully", null));
    }

    @PatchMapping("/{orderId}/order-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Update order status, admin only")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status
    ) {
        OrderDto order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(new ApiResponse("Order status updated to " + status, order));
    }
}
