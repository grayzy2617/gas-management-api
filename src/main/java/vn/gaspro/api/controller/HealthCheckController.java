package vn.gaspro.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.gaspro.api.dto.response.ApiResponse;
import vn.gaspro.api.enums.ErrorCode;
import vn.gaspro.api.exception.AppException;

/**
 * Controller kiểm tra trạng thái hoạt động của Service (Health Check)
 * và test luồng xử lý Exception tập trung.
 */
import lombok.RequiredArgsConstructor;
import vn.gaspro.api.repository.*;

@RestController
@RequestMapping("/api/v1/health-check")
@RequiredArgsConstructor
public class HealthCheckController {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Gas Pro API Service is running smoothly!", "Thành công");
    }

    @GetMapping("/test-error")
    public ApiResponse<Void> testError() {
        throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
    }
    
    @org.springframework.web.bind.annotation.PostMapping("/init-test-data")
    public ApiResponse<String> initTestData() {

        vn.gaspro.api.entity.User user = userRepository.findByPhone("0987654321").orElse(null);
        if (user != null && customerRepository.findByPhone("0987654321").isEmpty()) {
            vn.gaspro.api.entity.Customer customer = vn.gaspro.api.entity.Customer.builder()
                    .user(user)
                    .phone(user.getPhone())
                    .contactName(user.getFullName())
                    .customerType(vn.gaspro.api.enums.CustomerType.RETAIL_B2C)
                    .debtStatus(vn.gaspro.api.enums.DebtStatus.ELIGIBLE)
                    .creditLimit(java.math.BigDecimal.valueOf(10000000))
                    .currentDebt(java.math.BigDecimal.ZERO)
                    .totalCylindersPurchased(0)
                    .deliveryAddress("123 Test Street")
                    .build();
            customerRepository.save(customer);
        }

        if (productRepository.findById(1L).isEmpty()) {
            vn.gaspro.api.entity.Category cat = vn.gaspro.api.entity.Category.builder().code("GAS").name("Bình Gas").build();
            categoryRepository.save(cat);

            vn.gaspro.api.entity.Brand brand = vn.gaspro.api.entity.Brand.builder().code("PETRO").name("PetroVietnam").build();
            brandRepository.save(brand);

            vn.gaspro.api.entity.Product product = vn.gaspro.api.entity.Product.builder()
                    .code("P-12KG")
                    .name("Bình Gas 12KG")
                    .category(cat)
                    .brand(brand)
                    .price(java.math.BigDecimal.valueOf(450000))
                    .stockQuantity(100)
                    .defaultDepositFee(java.math.BigDecimal.valueOf(300000))
                    .status(vn.gaspro.api.enums.ProductStatus.ACTIVE)
                    .build();
            productRepository.save(product);
        }

        return ApiResponse.success("Dữ liệu test đã được khởi tạo!", "Thành công");
    }
}