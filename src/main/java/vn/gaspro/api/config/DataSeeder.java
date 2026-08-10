package vn.gaspro.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.gaspro.api.entity.Role;
import vn.gaspro.api.enums.RoleCode;
import vn.gaspro.api.repository.RoleRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
    }

    private void seedRoles() {
        // 1. Định nghĩa danh sách các Role mặc định của hệ thống Gas Pro
        List<Role> defaultRoles = List.of(
                Role.builder()
                        .code(RoleCode.ADMIN)
                        .name("Quản trị viên")
                        .description("Quản trị toàn bộ hệ thống, phân quyền và cấu hình")
                        .build(),

                Role.builder()
                        .code(RoleCode.OPERATOR)
                        .name("Nhân viên Tổng đài / Điều hành")
                        .description("Quản lý đơn hàng, điều phối tài xế và nhập/xuất kho")
                        .build(),

                Role.builder()
                        .code(RoleCode.DRIVER)
                        .name("Tài xế giao hàng")
                        .description("Nhận đơn, giao gas, thu tiền và hoàn trả vỏ rỗng")
                        .build(),

                Role.builder()
                        .code(RoleCode.CUSTOMER)
                        .name("Khách hàng")
                        .description("Đặt hàng, xem công nợ và yêu cầu bảo hành/sửa chữa")
                        .build()
        );

        // 2. Lặp qua danh sách và lưu vào Database nếu chưa tồn tại
        for (Role role : defaultRoles) {
            if (!roleRepository.existsByCode(role.getCode())) {
                roleRepository.save(role);
                log.info("Seeded Role successfully: {}", role.getCode());
            }
        }
    }
}