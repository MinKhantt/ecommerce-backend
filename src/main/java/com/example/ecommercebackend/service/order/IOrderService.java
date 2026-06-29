package com.example.ecommercebackend.service.order;

import com.example.ecommercebackend.dto.OrderDto;
import com.example.ecommercebackend.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderDto placeOrder(UUID userId, String shippingAddress);

    PageResponse<OrderDto> getAllOrders(Pageable pageable);

    OrderDto getOrderById(UUID orderId, UUID currentUserId);

    OrderDto getOrderByIdForAdmin(UUID orderId);

    List<OrderDto> getUserOrders(UUID userId);

    OrderDto updateOrderStatus(UUID orderId, String status);

    void cancelOrder(UUID orderId, UUID userId);
}
