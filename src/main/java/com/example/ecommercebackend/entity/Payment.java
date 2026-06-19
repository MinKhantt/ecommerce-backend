package com.example.ecommercebackend.entity;

import com.example.ecommercebackend.enums.PaymentMethod;
import com.example.ecommercebackend.enums.PaymentProvider;
import com.example.ecommercebackend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "payments",
        indexes = {
            @Index(name = "idx_external_id", columnList = "externalTransactionId"),
            @Index(name = "idx_order_id", columnList = "order_id")
        }
)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider;

    private String externalTransactionId;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
