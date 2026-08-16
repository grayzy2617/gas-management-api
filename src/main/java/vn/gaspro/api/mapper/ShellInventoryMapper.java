package vn.gaspro.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.gaspro.api.dto.response.ShellInventoryResponse;
import vn.gaspro.api.entity.ShellInventory;

@Mapper(componentModel = "spring")
public interface ShellInventoryMapper {

    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(target = "status", expression = "java(inventory.getEmptyQuantity() >= inventory.getSafetyStock() ? \"AN_TOAN\" : \"DUOI_MUC_AN_TOAN\")")
    ShellInventoryResponse toShellInventoryResponse(ShellInventory inventory);
}
