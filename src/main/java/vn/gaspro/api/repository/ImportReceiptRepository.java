package vn.gaspro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.ImportReceipt;

@Repository
public interface ImportReceiptRepository extends JpaRepository<ImportReceipt, Long> {
}
