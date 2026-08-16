package vn.gaspro.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShellExchangeRequest {

    @NotNull(message = "SUPPLIER_ID_REQUIRED")
    Long supplierId;

    @NotNull(message = "SHELL_BRAND_ID_REQUIRED")
    Long shellBrandId;

    @NotNull(message = "EXPORTED_SHELL_QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MIN_1")
    Integer exportedShellQuantity;

    @NotNull(message = "FULL_PRODUCT_ID_REQUIRED")
    Long fullProductId;

    @NotNull(message = "IMPORTED_CYLINDER_QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MIN_1")
    Integer importedCylinderQuantity;

    String note;
}
