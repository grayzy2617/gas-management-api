package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartSummaryResponse {
    Long cartId;
    List<CartItemResponse> items;
    CartSummary summary;

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CartSummary {
        BigDecimal totalGoodsAmount;
        BigDecimal totalDepositAmount;
        BigDecimal grandTotal;
    }
}
