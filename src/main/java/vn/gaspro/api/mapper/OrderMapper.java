package vn.gaspro.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.gaspro.api.dto.response.OrderResponse;
import vn.gaspro.api.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "customerName", source = "customer.contactName")
    @Mapping(target = "driverName", source = "driver.fullName")
    OrderResponse toResponse(Order order);
}
