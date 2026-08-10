package vn.gaspro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.Permission;
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
}
