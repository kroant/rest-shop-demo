package cz.kromer.restshopdemo.repository;

import cz.kromer.restshopdemo.entity.Order;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import static org.hibernate.cfg.AvailableSettings.JAKARTA_LOCK_TIMEOUT;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Lock(PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = JAKARTA_LOCK_TIMEOUT, value = "2000")})
    Optional<Order> findAndLockById(UUID id);

    List<Order> findAllByOrderByStateAscCreatedOnDesc();

    @Query("select o.id from Order o where o.createdOn < :before and o.state = 'NEW'")
    List<UUID> findNewOrdersBefore(Instant before);
}
