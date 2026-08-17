package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    String orderCode;
    String customerName;
    String driverName;
    String deliveryAddress;
    BigDecimal distanceKm;
    String paymentMethod;
    String paymentStatus;
    String orderStatus;
    BigDecimal totalGoodsAmount;
    BigDecimal totalDepositAmount;
    BigDecimal shippingFee;
    BigDecimal grandTotal;
    LocalDateTime createdAt;
}
