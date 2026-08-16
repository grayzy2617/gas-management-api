package vn.gaspro.api.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import vn.gaspro.api.entity.Brand;
import vn.gaspro.api.entity.ShellInventory;
import vn.gaspro.api.repository.BrandRepository;
import vn.gaspro.api.repository.ShellInventoryRepository;

import java.util.List;

@Component
@Order(4) // Seed sau khi Brand đã được tạo
@RequiredArgsConstructor
@Slf4j
public class ShellDataSeeder implements CommandLineRunner {

    private final ShellInventoryRepository shellInventoryRepository;
    private final BrandRepository brandRepository;

    @Override
    public void run(String... args) throws Exception {
        if (shellInventoryRepository.count() == 0) {
            log.info("Seeding Shell Inventories...");

            List<Brand> brands = brandRepository.findAll();
            for (Brand brand : brands) {
                ShellInventory inventory = ShellInventory.builder()
                        .brand(brand)
                        .emptyQuantity(100) // Khởi tạo mỗi hãng có sẵn 100 vỏ rỗng để test
                        .safetyStock(10)
                        .build();
                shellInventoryRepository.save(inventory);
            }
            log.info("Seeded Shell Inventories for " + brands.size() + " brands.");
        }
    }
}
