package vn.gaspro.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "product_price_histories")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "old_price", precision = 15, scale = 2)
    BigDecimal oldPrice;

    @Column(name = "new_price", nullable = false, precision = 15, scale = 2)
    BigDecimal newPrice;

    @Column(name = "effective_date", nullable = false)
    @Builder.Default
    LocalDateTime effectiveDate = LocalDateTime.now();

    @Column(name = "changed_by")
    String changedBy; // Có thể lưu username hoặc userId của người đổi giá
}
