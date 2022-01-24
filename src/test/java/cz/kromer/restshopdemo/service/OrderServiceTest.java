package cz.kromer.restshopdemo.service;

import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static java.math.BigDecimal.ONE;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import cz.kromer.restshopdemo.E2ETestParent;
import cz.kromer.restshopdemo.dto.OrderDto;
import cz.kromer.restshopdemo.dto.OrderItemDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;

class OrderServiceTest extends E2ETestParent {

    static final UUID PRODUCT_1_ID = fromString("10b10895-cce9-48c6-bc8c-7025d0a7fe57");

    @Autowired
    private OrderService orderService;

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldSaveProductAndCreateOrder_WhenSomeOrderIdProvided() {
        UUID orderId = orderService.save(OrderDto.builder()
                        .id(new UUID(0, 0))
                        .items(List.of(OrderItemDto.builder()
                                .product(OrderProductDto.builder().id(PRODUCT_1_ID).build())
                                .amount(ONE).build()))
                        .build());
        assertNotEquals(new UUID(0, 0), orderId);
    }
}
