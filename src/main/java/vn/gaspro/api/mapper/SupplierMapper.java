package vn.gaspro.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.gaspro.api.dto.response.SupplierDebtHistoryResponse;
import vn.gaspro.api.dto.response.SupplierResponse;
import vn.gaspro.api.entity.Supplier;
import vn.gaspro.api.entity.SupplierDebtHistory;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponse toSupplierResponse(Supplier supplier);

    @Mapping(source = "supplier.name", target = "supplierName")
    SupplierDebtHistoryResponse toSupplierDebtHistoryResponse(SupplierDebtHistory history);
}
