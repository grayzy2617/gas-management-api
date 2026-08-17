package vn.gaspro.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import vn.gaspro.api.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreateRequest {
    @NotNull(message = "Địa chỉ giao hàng không được để trống")
    String deliveryAddress;

    @NotNull(message = "Khoảng cách không được để trống")
    BigDecimal distanceKm;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    PaymentMethod paymentMethod;

    String notes;

    @NotNull(message = "Danh sách sản phẩm không được để trống")
    List<OrderItemRequest> items;
}
