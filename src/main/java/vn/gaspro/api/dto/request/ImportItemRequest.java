package vn.gaspro.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportItemRequest {
    @NotNull(message = "PRODUCT_ID_REQUIRED")
    Long productId;

    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MIN_1")
    Integer quantity;

    @NotNull(message = "UNIT_PRICE_REQUIRED")
    @Min(value = 0, message = "UNIT_PRICE_MIN_0")
    BigDecimal unitPrice;
}
