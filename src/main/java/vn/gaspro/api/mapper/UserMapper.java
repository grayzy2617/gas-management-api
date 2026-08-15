package vn.gaspro.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.gaspro.api.dto.response.UserResponse;
import vn.gaspro.api.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.code", target = "roleCode")
    UserResponse toUserResponse(User user);
}
