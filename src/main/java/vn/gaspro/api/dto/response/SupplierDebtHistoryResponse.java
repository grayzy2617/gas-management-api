package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierDebtHistoryResponse {
    Long id;
    String supplierName;
    BigDecimal amountPaid;
    BigDecimal remainingDebt;
    String note;
    String createdBy;
    LocalDateTime paymentDate;
}
