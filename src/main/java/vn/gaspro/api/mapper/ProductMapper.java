package vn.gaspro.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.gaspro.api.dto.response.ProductPriceHistoryResponse;
import vn.gaspro.api.dto.response.ProductResponse;
import vn.gaspro.api.entity.Product;
import vn.gaspro.api.entity.ProductPriceHistory;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    ProductResponse toProductResponse(Product product);

    ProductPriceHistoryResponse toProductPriceHistoryResponse(ProductPriceHistory history);
}
