package com.example.shoppingcartapi.helper;

import com.example.shoppingcartapi.entity.Order;
import com.example.shoppingcartapi.entity.OrderItem;
import com.example.shoppingcartapi.entity.Payment;
import com.example.shoppingcartapi.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

        String customerName = order.getUser().getFirstName() + " " + order.getUser().getLastName();

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
