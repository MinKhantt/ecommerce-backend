package com.example.ecommercebackend.repository;

import com.example.ecommercebackend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    void deleteAllByCartId(UUID id);
}
