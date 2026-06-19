package com.example.ecommercebackend.helper;

import com.example.ecommercebackend.entity.Order;
import com.example.ecommercebackend.entity.OrderItem;
import com.example.ecommercebackend.entity.Payment;
import com.example.ecommercebackend.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record OrderEmailContext(
    String customerName,
    UUID orderId,
    LocalDateTime orderDate,
    String orderStatus,
    BigDecimal totalAmount,
    String shippingAddress,
    List<Item> items,
    PaymentInfo payment
) {
    public record Item(String productName, String brand, String imageUrl, int quantity, BigDecimal price) {}
    public record PaymentInfo(String method, String status, boolean paid, String transactionId) {}

    public static OrderEmailContext from(Order order, Payment payment) {
        List<Item> items = order.getOrderItems().stream()
            .map(OrderEmailContext::toItem)
            .toList();

        PaymentInfo paymentInfo = payment != null
            ? new PaymentInfo(
                payment.getPaymentMethod().name(),
                payment.getPaymentStatus().name(),
                payment.getPaymentStatus() == PaymentStatus.SUCCEEDED,
                payment.getExternalTransactionId()
            ) : null;

        String customerName = Stream.of(order.getUser().getFirstName(), order.getUser().getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "))
                .trim();
        if (customerName.isEmpty()) {
            customerName = "Customer";
        }

        return new OrderEmailContext(
            customerName,
            order.getId(),
            order.getOrderDate(),
            order.getOrderStatus().name(),
            order.getTotalAmount(),
            order.getShippingAddress(),
            items,
            paymentInfo
        );
    }

    private static Item toItem(OrderItem oi) {
        String imageUrl = oi.getProduct().getImages() != null && !oi.getProduct().getImages().isEmpty()
            ? oi.getProduct().getImages().getFirst().getDownloadUrl() : null;
        return new Item(oi.getProduct().getName(), oi.getProduct().getBrand(), imageUrl, oi.getQuantity(), oi.getPrice());
    }
}
