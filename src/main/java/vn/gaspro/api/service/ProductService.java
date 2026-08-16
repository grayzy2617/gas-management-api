package vn.gaspro.api.service;

import org.springframework.data.domain.Page;
import vn.gaspro.api.dto.request.ProductPriceRequest;
import vn.gaspro.api.dto.request.ProductRequest;
import vn.gaspro.api.dto.response.ProductPriceHistoryResponse;
import vn.gaspro.api.dto.response.ProductResponse;

public interface ProductService {
    Page<ProductResponse> getProducts(String name, Long categoryId, Long brandId, int page, int size);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);

    ProductResponse updateProductPrice(Long id, ProductPriceRequest request);
    Page<ProductPriceHistoryResponse> getPriceHistories(Long productId, int page, int size);
}
