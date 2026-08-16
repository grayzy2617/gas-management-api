package vn.gaspro.api.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShellInventoryResponse {
    Long id;
    Long brandId;
    String brandName;
    Integer emptyQuantity;
    Integer safetyStock;
    String status; // AN_TOAN, DUOI_MUC_AN_TOAN
}
