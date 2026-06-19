package com.example.ecommercebackend.service.order;

import com.example.ecommercebackend.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderDto placeOrder(UUID userId, String shippingAddress);

    List<OrderDto> getAllOrders();

    OrderDto getOrderById(UUID orderId, UUID currentUserId);

    OrderDto getOrderByIdForAdmin(UUID orderId);

    List<OrderDto> getUserOrders(UUID userId);

    OrderDto updateOrderStatus(UUID orderId, String status);

    void cancelOrder(UUID orderId, UUID userId);
}
