package vn.gaspro.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.ProductPriceHistory;

@Repository
public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long> {
    Page<ProductPriceHistory> findByProductIdOrderByEffectiveDateDesc(Long productId, Pageable pageable);
}
