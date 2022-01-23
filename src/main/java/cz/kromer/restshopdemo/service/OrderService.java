package cz.kromer.restshopdemo.service;

import static cz.kromer.restshopdemo.dto.OrderState.CANCELLED;
import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static cz.kromer.restshopdemo.dto.OrderState.PAID;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.kromer.restshopdemo.dto.OrderDto;
import cz.kromer.restshopdemo.dto.OrderState;
import cz.kromer.restshopdemo.entity.Order;
import cz.kromer.restshopdemo.entity.OrderItem;
import cz.kromer.restshopdemo.exception.IllegalOrderStateException;
import cz.kromer.restshopdemo.exception.RootEntityNotFoundException;
import cz.kromer.restshopdemo.mapper.OrderMapper;
import cz.kromer.restshopdemo.repository.OrderLockingRepository;
import cz.kromer.restshopdemo.repository.OrderRepository;
import cz.kromer.restshopdemo.repository.ProductLockingRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    OrderLockingRepository orderLockingRepository;
    ProductLockingRepository productLockingRepository;
    OrderMapper orderMapper;
    BeanFactory beanFactory;

    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return orderRepository.findAllByOrderByStateAscCreatedOnDesc().stream()
                .map(orderMapper::mapToOrderDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDto getById(UUID id) {
        return orderMapper.mapToOrderDto(
                orderRepository.findById(id).orElseThrow(() -> new RootEntityNotFoundException(id)));
    }

    @Retryable(include = { ConcurrencyFailureException.class })
    @Transactional(isolation = READ_COMMITTED)
    public UUID save(OrderDto order) {
        Order entity = orderMapper.mapToOrder(order);

        StockShortageWatcher stock = beanFactory.getBean(StockShortageWatcher.class);
        stock.take(entity.getItems());

        entity = orderRepository.save(entity);
        return entity.getId();
    }

    @Retryable(include = { ConcurrencyFailureException.class })
    @Transactional(isolation = READ_COMMITTED)
    public void cancel(UUID id) {
        Order order = orderLockingRepository.findById(id).orElseThrow(() -> new RootEntityNotFoundException(id));

        validateOrderState(order, NEW);

        order.getItems().forEach(this::returnToStock);
        order.setState(CANCELLED);
    }

    @Transactional
    public void pay(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RootEntityNotFoundException(id));

        validateOrderState(order, NEW);

        order.setState(PAID);
    }

    @Transactional(readOnly = true)
    public List<UUID> findNewOrdersBefore(Instant before) {
        return orderRepository.findNewOrdersBefore(before);
    }

    private void returnToStock(OrderItem item) {
        productLockingRepository.findById(item.getProduct().getId()).ifPresent(product -> {
            BigDecimal newAmount = product.getStock().add(item.getAmount());
            product.setStock(newAmount);
        });
    }

    private static void validateOrderState(Order order, OrderState... allowedStates) {
        EnumSet<OrderState> allowedSet = EnumSet.copyOf(asList(allowedStates));
        if (!allowedSet.contains(order.getState())) {
            throw new IllegalOrderStateException(order.getId(), order.getState(), allowedSet);
        }
    }
}
