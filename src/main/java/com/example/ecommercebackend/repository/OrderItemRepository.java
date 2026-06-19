package com.example.ecommercebackend.repository;

import com.example.ecommercebackend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
