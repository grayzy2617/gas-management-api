package vn.gaspro.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.gaspro.api.enums.FaultParty;
import vn.gaspro.api.enums.OrderStatus;
import vn.gaspro.api.enums.PaymentMethod;
import vn.gaspro.api.enums.PaymentStatus;
import vn.gaspro.api.enums.SealStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true, length = 50)
    String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy;

    @Column(nullable = false, length = 20)
    @Builder.Default
    String orderType = "NORMAL";

    @Column(nullable = false, columnDefinition = "TEXT")
    String deliveryAddress;

    @Column(precision = 5, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal distanceKm = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal totalGoodsAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal totalDepositAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal grandTotal = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    String notes;

    @Column(length = 255)
    String pendingPaymentProof;

    @Column(length = 20)
    String cancelledBy;

    @Column(columnDefinition = "TEXT")
    String cancellationReason;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    FaultParty faultParty;

    @Column(nullable = false)
    @Builder.Default
    Boolean isDriverCompensated = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    SealStatus sealStatus;

    @Column(columnDefinition = "TEXT")
    String sealViolationNote;

    @Column
    LocalDateTime acceptedAt;

    @Column
    LocalDateTime deliveringAt;

    @Column
    LocalDateTime completedAt;

    @Column
    LocalDateTime cancelledAt;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<OrderItem> items = new ArrayList<>();
}
