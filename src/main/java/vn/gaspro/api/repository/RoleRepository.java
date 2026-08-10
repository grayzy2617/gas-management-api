package vn.gaspro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.Role;
import vn.gaspro.api.enums.RoleCode;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Kiểm tra RoleCode đã tồn tại trong CSDL chưa
    boolean existsByCode(RoleCode code);

    // Tìm kiếm Role theo RoleCode (dùng cho các Service Auth/User sau này)
    Optional<Role> findByCode(RoleCode code);
}
