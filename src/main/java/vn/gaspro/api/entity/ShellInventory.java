package vn.gaspro.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "shell_inventories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShellInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false, unique = true)
    Brand brand;

    @Column(nullable = false)
    @Builder.Default
    Integer emptyQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    Integer safetyStock = 10;
}
