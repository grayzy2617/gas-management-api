package vn.gaspro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.gaspro.api.entity.User;

import java.util.Optional;
@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
