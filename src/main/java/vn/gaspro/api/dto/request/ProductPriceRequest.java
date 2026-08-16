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
public class ProductPriceRequest {

    @NotNull(message = "INVALID_REQUEST_BODY")
    @Min(value = 0, message = "INVALID_REQUEST_BODY")
    BigDecimal newPrice;
}
