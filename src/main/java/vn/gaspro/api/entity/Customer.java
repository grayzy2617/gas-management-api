package vn.gaspro.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.gaspro.api.enums.CustomerType;
import vn.gaspro.api.enums.DebtStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    CustomerType customerType = CustomerType.RETAIL_B2C;

    @Column(nullable = false, length = 100)
    String contactName;

    @Column(nullable = false, length = 15)
    String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    String deliveryAddress;

    @Column
    LocalDate firstOrderDate;

    @Column(nullable = false)
    @Builder.Default
    Integer totalCylindersPurchased = 0;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal currentDebt = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    @Builder.Default
    DebtStatus debtStatus = DebtStatus.INELIGIBLE;

    @Column(nullable = false)
    @Builder.Default
    Boolean isSpamLocked = false;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    CustomerVatInfo vatInfo;
}
