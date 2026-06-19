package com.example.ecommercebackend.repository;

import com.example.ecommercebackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);

    Optional<Order> findByIdAndUserId(UUID orderId, UUID userId);
}
