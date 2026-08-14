package vn.gaspro.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.gaspro.api.enums.RoleCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    @NotBlank(message = "INVALID_PHONE_FORMAT")
    @Pattern(regexp = "^\\d{10,11}$", message = "INVALID_PHONE_FORMAT")
    String phone;

    @NotBlank(message = "WEAK_PASSWORD")
    @Size(min = 6, message = "WEAK_PASSWORD")
    String password;

    @NotBlank(message = "INVALID_REQUEST_BODY")
    String fullName;
    
    // User chọn đăng ký là DRIVER hoặc CUSTOMER
    RoleCode roleCode;
}
