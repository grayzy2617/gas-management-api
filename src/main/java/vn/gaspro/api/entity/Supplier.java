package vn.gaspro.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true, length = 50)
    String code; // Mã NSX, ví dụ: PETROLIMEX, PVGAS

    @Column(nullable = false, length = 100)
    String name;

    @Column(length = 20)
    String phone;

    @Column(length = 255)
    String address;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal debtBalance = BigDecimal.ZERO; // Tổng công nợ hiện tại

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
