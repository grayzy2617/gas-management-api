package vn.gaspro.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.gaspro.api.dto.request.ProductRequest;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.dto.response.PagingResponse;
import vn.gaspro.api.dto.response.ProductResponse;
import vn.gaspro.api.service.ProductService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // --- PUBLIC APIs ---

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Object>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ProductResponse> productPage = productService.getProducts(search, categoryId, brandId, page, size);
        
        PagingResponse paging = PagingResponse.builder()
                .page(page)
                .limit(size)
                .totalItems(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(productPage.getContent(), paging));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    // --- ADMIN APIs ---
    // SecurityConfig đã tự động check Role ADMIN với đường dẫn /api/v1/admin/**

    @PostMapping("/admin/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.createProduct(request)));
    }

    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(id, request)));
    }

    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa sản phẩm thành công (Soft delete)"));
    }

    @PatchMapping("/admin/products/{id}/price")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductPrice(
            @PathVariable Long id, 
            @Valid @RequestBody vn.gaspro.api.dto.request.ProductPriceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProductPrice(id, request)));
    }

    @GetMapping("/admin/products/{id}/price-histories")
    public ResponseEntity<ApiResponse<Object>> getPriceHistories(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<vn.gaspro.api.dto.response.ProductPriceHistoryResponse> historyPage = productService.getPriceHistories(id, page, size);
        
        PagingResponse paging = PagingResponse.builder()
                .page(page)
                .limit(size)
                .totalItems(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(historyPage.getContent(), paging));
    }
}
