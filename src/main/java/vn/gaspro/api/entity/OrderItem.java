package vn.gaspro.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(nullable = false, length = 150)
    String productName;

    @Column(nullable = false)
    Integer quantity;

    @Column(precision = 15, scale = 2, nullable = false)
    BigDecimal unitPrice;

    @Column(nullable = false)
    @Builder.Default
    Boolean hasExchangeShell = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivered_brand_id")
    Brand deliveredBrand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_brand_id")
    Brand collectedBrand;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    BigDecimal unitDepositFee = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    BigDecimal subtotal;
}
