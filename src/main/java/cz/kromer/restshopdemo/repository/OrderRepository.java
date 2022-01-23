package cz.kromer.restshopdemo.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cz.kromer.restshopdemo.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByOrderByStateAscCreatedOnDesc();

    @Query("select o.id from Order o where o.createdOn < :before and o.state = 'NEW'")
    List<UUID> findNewOrdersBefore(@Param("before") Instant before);
}
