package com.example.shoppingcartapi.repository;

import com.example.shoppingcartapi.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    void deleteAllByCartId(UUID id);
}
