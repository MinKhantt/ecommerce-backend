package com.example.shoppingcartapi.service.order;

import com.example.shoppingcartapi.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderDto placeOrder(UUID userId, String shippingAddress);

    List<OrderDto> getAllOrders();

    OrderDto getOrderById(UUID orderId, UUID currentUserId);

    List<OrderDto> getUserOrders(UUID userId);

    OrderDto updateOrderStatus(UUID orderId, String status);

    void cancelOrder(UUID orderId, UUID userId);
}
