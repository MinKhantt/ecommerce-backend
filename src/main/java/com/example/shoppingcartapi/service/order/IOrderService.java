package com.example.shoppingcartapi.service.order;

import com.example.shoppingcartapi.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface IOrderService {
    OrderDto placeOrder(UUID userId); // after implement user stuff replace OrderItem userId
    OrderDto getOrder(UUID orderId);

    List<OrderDto> getUserOrders(UUID userId);
}
