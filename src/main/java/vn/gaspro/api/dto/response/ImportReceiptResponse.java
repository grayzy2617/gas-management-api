package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.gaspro.api.enums.PaymentStatus;
import vn.gaspro.api.enums.ReceiptType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportReceiptResponse {
    Long id;
    String receiptCode;
    String supplierName;
    LocalDateTime receiptDate;
    ReceiptType type;
    BigDecimal totalAmount;
    BigDecimal amountPaid;
    BigDecimal debtAmount;
    PaymentStatus paymentStatus;
    String note;
    String createdBy;
    List<ImportReceiptItemResponse> items;
}
