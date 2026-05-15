package com.example.shoppingcartapi.repository;

import com.example.shoppingcartapi.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
