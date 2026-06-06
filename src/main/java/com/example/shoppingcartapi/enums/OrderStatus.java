package com.example.shoppingcartapi.enums;

import java.util.Optional;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public static Optional<OrderStatus> fromString(String status) {
        for (OrderStatus s : OrderStatus.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}
