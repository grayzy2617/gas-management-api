package vn.gaspro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.ShellInventory;

import java.util.Optional;

@Repository
public interface ShellInventoryRepository extends JpaRepository<ShellInventory, Long> {
    Optional<ShellInventory> findByBrandId(Long brandId);
}
