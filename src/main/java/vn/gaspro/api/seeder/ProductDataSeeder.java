package vn.gaspro.api.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.gaspro.api.entity.Brand;
import vn.gaspro.api.entity.Category;
import vn.gaspro.api.entity.Product;
import vn.gaspro.api.enums.ProductStatus;
import vn.gaspro.api.repository.BrandRepository;
import vn.gaspro.api.repository.CategoryRepository;
import vn.gaspro.api.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductDataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            log.info("Seeding Categories...");
            categoryRepository.saveAll(List.of(
                    Category.builder().code("BINH_GAS").name("Bình gas").description("Các loại bình gas dân dụng và công nghiệp").build(),
                    Category.builder().code("BEP_GAS").name("Bếp gas").description("Các loại bếp gas").build(),
                    Category.builder().code("PHU_KIEN").name("Phụ kiện").description("Van gas, dây dẫn, linh kiện thay thế").build()
            ));
        }

        if (brandRepository.count() == 0) {
            log.info("Seeding Brands...");
            brandRepository.saveAll(List.of(
                    Brand.builder().code("PETROLIMEX").name("Petrolimex").build(),
                    Brand.builder().code("PV_GAS").name("PV Gas").build(),
                    Brand.builder().code("RINNAI").name("Rinnai").build(),
                    Brand.builder().code("NAMILUX").name("Namilux").build()
            ));
        }

        if (productRepository.count() == 0) {
            log.info("Seeding Products...");
            Category binhGas = categoryRepository.findByCode("BINH_GAS").orElseThrow();
            Category bepGas = categoryRepository.findByCode("BEP_GAS").orElseThrow();
            Category phuKien = categoryRepository.findByCode("PHU_KIEN").orElseThrow();

            Brand petrolimex = brandRepository.findByCode("PETROLIMEX").orElseThrow();
            Brand pvGas = brandRepository.findByCode("PV_GAS").orElseThrow();
            Brand rinnai = brandRepository.findByCode("RINNAI").orElseThrow();
            Brand namilux = brandRepository.findByCode("NAMILUX").orElseThrow();

            productRepository.saveAll(List.of(
                    Product.builder().code("PG-12KG-001").name("Bình gas Petrolimex 12kg").category(binhGas).brand(petrolimex).price(new BigDecimal("420000")).stockQuantity(100).status(ProductStatus.ACTIVE).build(),
                    Product.builder().code("PG-12KG-002").name("Bình gas PV Gas 12kg").category(binhGas).brand(pvGas).price(new BigDecimal("410000")).stockQuantity(50).status(ProductStatus.ACTIVE).build(),
                    Product.builder().code("PG-50KG-001").name("Bình gas Petrolimex 50kg công nghiệp").category(binhGas).brand(petrolimex).price(new BigDecimal("1150000")).stockQuantity(20).status(ProductStatus.ACTIVE).build(),
                    Product.builder().code("BEP-RIN-365").name("Bếp gas Rinnai RV-365").category(bepGas).brand(rinnai).price(new BigDecimal("2800000")).stockQuantity(15).status(ProductStatus.ACTIVE).build(),
                    Product.builder().code("BEP-NAM-101").name("Bếp gas Namilux mini").category(bepGas).brand(namilux).price(new BigDecimal("350000")).stockQuantity(30).status(ProductStatus.ACTIVE).build(),
                    Product.builder().code("PK-VAN-001").name("Van gas Namilux tự động").category(phuKien).brand(namilux).price(new BigDecimal("150000")).stockQuantity(100).status(ProductStatus.ACTIVE).build(),
                    Product.builder().code("PK-DAY-001").name("Dây gas Petrolimex 1.5m").category(phuKien).brand(petrolimex).price(new BigDecimal("80000")).stockQuantity(200).status(ProductStatus.ACTIVE).build(),
                    Product.builder().code("PK-IC-001").name("IC đánh lửa bếp gas").category(phuKien).brand(rinnai).price(new BigDecimal("150000")).stockQuantity(50).status(ProductStatus.ACTIVE).build()
            ));
        }
    }
}
