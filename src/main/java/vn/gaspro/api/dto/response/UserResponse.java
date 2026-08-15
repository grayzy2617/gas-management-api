package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.gaspro.api.enums.RoleCode;
import vn.gaspro.api.enums.UserStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String phone;
    String fullName;
    String email;
    UserStatus status;
    RoleCode roleCode;
    LocalDateTime createdAt;
}
