package com.example.shoppingcartapi.repository;

import com.example.shoppingcartapi.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByUserId(UUID userId);

    Optional<Payment> findByIdAndUserId(UUID id, UUID userId);

    Optional<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findByExternalTransactionId(String externalTransactionId);
}
