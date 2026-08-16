package vn.gaspro.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.gaspro.api.dto.response.ImportReceiptItemResponse;
import vn.gaspro.api.dto.response.ImportReceiptResponse;
import vn.gaspro.api.entity.ImportReceipt;
import vn.gaspro.api.entity.ImportReceiptItem;

@Mapper(componentModel = "spring")
public interface ImportMapper {

    @Mapping(source = "supplier.name", target = "supplierName")
    ImportReceiptResponse toImportReceiptResponse(ImportReceipt importReceipt);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.code", target = "productCode")
    @Mapping(source = "product.name", target = "productName")
    ImportReceiptItemResponse toImportReceiptItemResponse(ImportReceiptItem item);
}
