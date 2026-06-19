package com.example.ecommercebackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "carts")
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Set<CartItem> getItems() {
        return cartItems;
    }

    @PrePersist
    @PreUpdate
    public void calculateTotalAmount() {
        this.totalAmount = cartItems.stream()
                .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
        updateTotalAmount();
    }

    public void removeItem(CartItem cartItems) {
        this.cartItems.remove(cartItems);
        cartItems.setCart(null);
        updateTotalAmount();
    }

    public void updateTotalAmount() {
        this.totalAmount = this.cartItems.stream().map(item -> {
            BigDecimal unitPrice = item.getUnitPrice();
            if (unitPrice == null) {
                return  BigDecimal.ZERO;
            }
            return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
