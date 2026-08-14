package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.gaspro.api.enums.RoleCode;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    Long userId;
    String phone;
    RoleCode roleCode;
    String accessToken;
    String refreshToken;
}
