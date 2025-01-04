package cz.kromer.restshopdemo.service;

import cz.kromer.restshopdemo.dto.CreateOrderDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.dto.OrderResponseDto;
import cz.kromer.restshopdemo.dto.OrderState;
import cz.kromer.restshopdemo.entity.Order;
import cz.kromer.restshopdemo.entity.OrderItem;
import cz.kromer.restshopdemo.entity.Product;
import cz.kromer.restshopdemo.exception.AssociatedEntityNotFoundException;
import cz.kromer.restshopdemo.exception.IllegalAmountScaleException;
import cz.kromer.restshopdemo.exception.IllegalOrderStateException;
import cz.kromer.restshopdemo.exception.RootEntityNotFoundException;
import cz.kromer.restshopdemo.mapper.OrderMapper;
import cz.kromer.restshopdemo.mapper.OrderResponseDtoMapper;
import cz.kromer.restshopdemo.repository.OrderRepository;
import cz.kromer.restshopdemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static cz.kromer.restshopdemo.dto.OrderState.CANCELLED;
import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static cz.kromer.restshopdemo.dto.OrderState.PAID;
import static cz.kromer.restshopdemo.dto.validation.ProductStockMaxScaleValidator.isScaleValid;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderMapper orderMapper;
    OrderResponseDtoMapper orderResponseDtoMapper;
    ObjectFactory<StockShortageWatcher> stockShortageWatcherFactory;

    @Transactional(readOnly = true)
    public List<OrderResponseDto> findAll() {
        return orderRepository.findAllByOrderByStateAscCreatedOnDesc().stream()
                .map(orderResponseDtoMapper::mapFrom)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getById(UUID id) {
        return orderResponseDtoMapper.mapFrom(
                orderRepository.findById(id)
                        .orElseThrow(() -> new RootEntityNotFoundException(id))
        );
    }

    @Retryable(retryFor = { ConcurrencyFailureException.class })
    @Transactional(isolation = READ_COMMITTED)
    public UUID save(CreateOrderDto order) {
        Order entity = orderMapper.mapFrom(order, this::findPersistentProductAndLock);
        List<OrderItem> items = entity.getItems();

        items.forEach(this::validateAmountScale);

        StockShortageWatcher stock = stockShortageWatcherFactory.getObject();
        stock.take(items);

        entity.setPrice(items.stream()
                .map(this::countPrice)
                .reduce(ZERO, BigDecimal::add)
        );
        entity = orderRepository.save(entity);
        return entity.getId();
    }

    @Retryable(retryFor = { ConcurrencyFailureException.class })
    @Transactional(isolation = READ_COMMITTED)
    public void cancel(UUID id) {
        Order order = orderRepository.findAndLockById(id)
                .orElseThrow(() -> new RootEntityNotFoundException(id));

        validateOrderState(order, NEW);

        order.getItems().forEach(this::returnToStock);
        order.setState(CANCELLED);
    }

    @Retryable(retryFor = { ConcurrencyFailureException.class })
    @Transactional(isolation = READ_COMMITTED)
    public void pay(UUID id) {
        Order order = orderRepository.findAndLockById(id)
                .orElseThrow(() -> new RootEntityNotFoundException(id));

        validateOrderState(order, NEW);

        order.setState(PAID);
    }

    @Transactional(readOnly = true)
    public List<UUID> findNewOrdersBefore(Instant before) {
        return orderRepository.findNewOrdersBefore(before);
    }

    private Product findPersistentProductAndLock(OrderProductDto orderProduct) {
        UUID id = orderProduct.getId();
        return productRepository.findAndLockById(id)
                .orElseThrow(() -> new AssociatedEntityNotFoundException(id));
    }

    private void returnToStock(OrderItem item) {
        productRepository.findAndLockById(item.getProduct().getId())
                .ifPresent(product -> {
                    BigDecimal newAmount = product.getStock().add(item.getAmount());
                    product.setStock(newAmount);
                });
    }

    private void validateAmountScale(OrderItem item) {
        Product product = item.getProduct();
        if (!isScaleValid(item.getAmount(), product.getUnit())) {
            throw new IllegalAmountScaleException(product.getId());
        }
    }

    private BigDecimal countPrice(OrderItem item) {
        return item.getAmount().multiply(item.getProduct().getPrice());
    }

    private void validateOrderState(Order order, OrderState... allowedStates) {
        EnumSet<OrderState> allowedSet = EnumSet.copyOf(asList(allowedStates));
        if (!allowedSet.contains(order.getState())) {
            throw new IllegalOrderStateException(order.getId(), order.getState(), allowedSet);
        }
    }
}
