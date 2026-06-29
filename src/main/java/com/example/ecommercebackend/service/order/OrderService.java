package com.example.ecommercebackend.service.order;

import com.example.ecommercebackend.dto.OrderDto;
import com.example.ecommercebackend.enums.OrderStatus;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.entity.Cart;
import com.example.ecommercebackend.entity.Order;
import com.example.ecommercebackend.entity.OrderItem;
import com.example.ecommercebackend.entity.Product;
import com.example.ecommercebackend.mapper.OrderMapper;
import com.example.ecommercebackend.repository.CartRepository;
import com.example.ecommercebackend.repository.OrderRepository;
import com.example.ecommercebackend.repository.ProductRepository;
import com.example.ecommercebackend.service.cart.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService implements IOrderService{

    private  final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto placeOrder(UUID userId, String shippingAddress) {

        Cart cart = cartRepository.findByUserId(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart.");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);

        List<OrderItem> orderItemList = createOrderItem(order, cart);

        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(cart.getUser().getId());

        return orderMapper.toOrderDto(savedOrder);
    }

    private List<OrderItem> createOrderItem(Order order, Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();

                    if (product.getInventory() < cartItem.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName());
                }

                return new OrderItem(order, product, cartItem.getQuantity(), cartItem.getUnitPrice());
                }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return orderItemList
                .stream()
                .map(item ->
                        item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())
                        )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDto getOrderById(UUID orderId, UUID currentUserId) {
        Order order = orderRepository.findByIdAndUserId(orderId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or access denied"));

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto getOrderByIdForAdmin(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return orderMapper.toOrderDto(order);
    }

    @Override
    public List<OrderDto> getUserOrders(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return  orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return  orders.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    @Override
    public void cancelOrder(UUID orderId, UUID userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or access denied"));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel an order that has already been shipped or delivered.");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setInventory(product.getInventory() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public OrderDto updateOrderStatus(UUID orderId, String status) {
        Order order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus newStatus = OrderStatus.fromString(status)
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + status));

        order.setOrderStatus(newStatus);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }
}
