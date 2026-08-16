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
public class SupplierPayRequest {

    @NotNull(message = "AMOUNT_REQUIRED")
    @Min(value = 1, message = "AMOUNT_MIN_1")
    BigDecimal amount;

    String note;
}
