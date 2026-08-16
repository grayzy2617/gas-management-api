package vn.gaspro.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.gaspro.api.dto.request.ProductRequest;
import vn.gaspro.api.dto.response.ProductResponse;
import vn.gaspro.api.entity.Brand;
import vn.gaspro.api.entity.Category;
import vn.gaspro.api.entity.Product;
import vn.gaspro.api.enums.ProductStatus;
import vn.gaspro.api.mapper.ProductMapper;
import vn.gaspro.api.repository.BrandRepository;
import vn.gaspro.api.repository.CategoryRepository;
import vn.gaspro.api.repository.ProductPriceHistoryRepository;
import vn.gaspro.api.repository.ProductRepository;
import vn.gaspro.api.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponse> getProducts(String name, Long categoryId, Long brandId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productRepository.filterProducts(name, categoryId, brandId, pageable);
        return products.map(productMapper::toProductResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PRODUCT_NOT_FOUND"));
        
        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new RuntimeException("PRODUCT_INACTIVE");
        }
        
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("PRODUCT_CODE_EXISTED");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("BRAND_NOT_FOUND"));

        ProductStatus status = request.getStockQuantity() > 0 ? ProductStatus.ACTIVE : ProductStatus.OUT_OF_STOCK;

        Product product = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .category(category)
                .brand(brand)
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .status(status)
                .build();

        product = productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PRODUCT_NOT_FOUND"));

        // Nếu cập nhật mã SP, phải đảm bảo không trùng
        if (!product.getCode().equals(request.getCode()) && productRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("PRODUCT_CODE_EXISTED");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("BRAND_NOT_FOUND"));

        ProductStatus status = request.getStockQuantity() > 0 ? ProductStatus.ACTIVE : ProductStatus.OUT_OF_STOCK;

        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setCategory(category);
        product.setBrand(brand);
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setStatus(status);

        product = productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PRODUCT_NOT_FOUND"));
        
        // Soft delete
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProductPrice(Long id, vn.gaspro.api.dto.request.ProductPriceRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PRODUCT_NOT_FOUND"));

        if (product.getPrice().compareTo(request.getNewPrice()) == 0) {
            throw new RuntimeException("PRICE_NOT_CHANGED");
        }

        // Lấy thông tin user đăng nhập
        String changedBy = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();

        vn.gaspro.api.entity.ProductPriceHistory history = vn.gaspro.api.entity.ProductPriceHistory.builder()
                .product(product)
                .oldPrice(product.getPrice())
                .newPrice(request.getNewPrice())
                .effectiveDate(java.time.LocalDateTime.now())
                .changedBy(changedBy)
                .build();
        
        productPriceHistoryRepository.save(history);

        product.setPrice(request.getNewPrice());
        product = productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    @Override
    public Page<vn.gaspro.api.dto.response.ProductPriceHistoryResponse> getPriceHistories(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<vn.gaspro.api.entity.ProductPriceHistory> histories = productPriceHistoryRepository.findByProductIdOrderByEffectiveDateDesc(productId, pageable);
        return histories.map(productMapper::toProductPriceHistoryResponse);
    }
}
