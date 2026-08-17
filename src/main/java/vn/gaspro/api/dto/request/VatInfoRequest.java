package vn.gaspro.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VatInfoRequest {
    @NotBlank(message = "Mã số thuế không được để trống")
    @Pattern(regexp = "^\\d{10}(\\d{3})?$", message = "INVALID_TAX_CODE")
    String taxCode;

    @NotBlank(message = "Tên công ty không được để trống")
    String companyName;

    @NotBlank(message = "Địa chỉ xuất hóa đơn không được để trống")
    String invoiceAddress;
}
