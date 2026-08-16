package vn.gaspro.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_debt_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierDebtHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    Supplier supplier;

    @Column(precision = 15, scale = 2, nullable = false)
    BigDecimal amountPaid;

    @Column(precision = 15, scale = 2, nullable = false)
    BigDecimal remainingDebt;

    @Column(length = 255)
    String note;

    @Column(length = 50, nullable = false)
    String createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime paymentDate;
}
