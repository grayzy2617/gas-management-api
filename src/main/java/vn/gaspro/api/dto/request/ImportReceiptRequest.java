package vn.gaspro.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportReceiptRequest {

    @NotNull(message = "SUPPLIER_ID_REQUIRED")
    Long supplierId;

    @NotNull(message = "AMOUNT_PAID_REQUIRED")
    @Min(value = 0, message = "AMOUNT_PAID_MIN_0")
    BigDecimal amountPaid;

    String note;

    @NotEmpty(message = "ITEMS_REQUIRED")
    @Valid
    List<ImportItemRequest> items;
}
