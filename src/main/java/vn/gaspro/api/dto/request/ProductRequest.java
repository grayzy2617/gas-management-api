package vn.gaspro.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class ProductRequest {

    @NotBlank(message = "INVALID_REQUEST_BODY")
    String code;

    @NotBlank(message = "INVALID_REQUEST_BODY")
    String name;

    @NotNull(message = "INVALID_REQUEST_BODY")
    Long categoryId;

    @NotNull(message = "INVALID_REQUEST_BODY")
    Long brandId;

    @NotNull(message = "INVALID_REQUEST_BODY")
    @Min(value = 0, message = "INVALID_REQUEST_BODY")
    BigDecimal price;

    @NotNull(message = "INVALID_REQUEST_BODY")
    @Min(value = 0, message = "INVALID_REQUEST_BODY")
    Integer stockQuantity;
}
