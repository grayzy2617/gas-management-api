package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerProfileResponse {
    Long customerId;
    String contactName;
    String phone;
    String customerType;
    String deliveryAddress;
    LocalDate firstOrderDate;
    Integer totalCylindersPurchased;
    BigDecimal creditLimit;
    BigDecimal currentDebt;
    String debtStatus;
    String debtStatusLabel;
    VatInfoResponse vatInfo;
}
