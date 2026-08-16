package vn.gaspro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.SupplierDebtHistory;

@Repository
public interface SupplierDebtHistoryRepository extends JpaRepository<SupplierDebtHistory, Long> {
}
