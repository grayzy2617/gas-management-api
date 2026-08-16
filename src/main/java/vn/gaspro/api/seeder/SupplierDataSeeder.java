package vn.gaspro.api.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import vn.gaspro.api.entity.Supplier;
import vn.gaspro.api.repository.SupplierRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(3) // Seed sau Role và Product (nếu Product có Order < 3)
@RequiredArgsConstructor
public class SupplierDataSeeder implements CommandLineRunner {

    private final SupplierRepository supplierRepository;

    @Override
    public void run(String... args) throws Exception {
        if (supplierRepository.count() == 0) {
            System.out.println("Seeding Suppliers...");

            Supplier s1 = Supplier.builder()
                    .code("PETROLIMEX")
                    .name("Tổng Công ty Gas Petrolimex")
                    .phone("19002828")
                    .address("229 Tây Sơn, Đống Đa, Hà Nội")
                    .debtBalance(BigDecimal.ZERO)
                    .build();

            Supplier s2 = Supplier.builder()
                    .code("PVGAS")
                    .name("Tổng Công ty Khí Việt Nam (PV Gas)")
                    .phone("02837816777")
                    .address("673 Nguyễn Hữu Thọ, Phước Kiển, Nhà Bè, TP.HCM")
                    .debtBalance(BigDecimal.ZERO)
                    .build();

            Supplier s3 = Supplier.builder()
                    .code("SAIGONPETRO")
                    .name("Công ty TNHH MTV Dầu khí TP.HCM (Saigon Petro)")
                    .phone("02838241031")
                    .address("27 Nguyễn Thông, Phường 7, Quận 3, TP.HCM")
                    .debtBalance(BigDecimal.ZERO)
                    .build();

            supplierRepository.saveAll(List.of(s1, s2, s3));
            System.out.println("Seeded 3 Suppliers successfully.");
        }
    }
}
