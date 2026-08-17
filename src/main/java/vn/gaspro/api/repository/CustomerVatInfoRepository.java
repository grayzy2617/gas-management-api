package vn.gaspro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.CustomerVatInfo;

import java.util.Optional;

@Repository
public interface CustomerVatInfoRepository extends JpaRepository<CustomerVatInfo, Long> {
    Optional<CustomerVatInfo> findByCustomerId(Long customerId);
}
