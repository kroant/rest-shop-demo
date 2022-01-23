package cz.kromer.restshopdemo.repository;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import cz.kromer.restshopdemo.entity.Product;

@Repository
public interface ProductLockingRepository extends JpaRepository<Product, UUID> {

    @Override
    @Lock(PESSIMISTIC_WRITE)
    @QueryHints({ @QueryHint(name = "javax.persistence.lock.timeout", value = "4000") })
    Optional<Product> findById(UUID id);
}