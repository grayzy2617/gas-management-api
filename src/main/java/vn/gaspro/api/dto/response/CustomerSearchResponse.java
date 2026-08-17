package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerSearchResponse {
    Long customerId;
    String contactName;
    String phone;
    String customerType;
    String deliveryAddress;
    String debtStatus;
    List<OrderResponse> orderHistory;
}
