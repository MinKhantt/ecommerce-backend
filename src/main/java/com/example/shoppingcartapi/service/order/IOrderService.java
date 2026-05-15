package com.example.shoppingcartapi.service.order;

import com.example.shoppingcartapi.dto.OrderDto;

import java.util.List;

public interface IOrderService {
    OrderDto placeOrder(Long userId); // after implement user stuff replace OrderItem userId
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);
}
