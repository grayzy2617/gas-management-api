package vn.gaspro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.ImportReceiptItem;

@Repository
public interface ImportReceiptItemRepository extends JpaRepository<ImportReceiptItem, Long> {
}
