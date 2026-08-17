package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long cartItemId;
    Long productId;
    String productName;
    Integer quantity;
    BigDecimal unitPrice;
    Boolean hasExchangeShell;
    BigDecimal unitDepositFee;
    BigDecimal subtotal;
}
