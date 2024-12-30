package cz.kromer.restshopdemo.repository;

import cz.kromer.restshopdemo.entity.Product;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;
import java.util.UUID;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import static org.hibernate.cfg.AvailableSettings.JAKARTA_LOCK_TIMEOUT;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Lock(PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = JAKARTA_LOCK_TIMEOUT, value = "2000")})
    Optional<Product> findAndLockById(UUID id);
}