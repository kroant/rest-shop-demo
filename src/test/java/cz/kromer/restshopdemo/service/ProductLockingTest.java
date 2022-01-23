package cz.kromer.restshopdemo.service;

import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static java.lang.Thread.sleep;
import static java.math.BigDecimal.ONE;
import static java.util.UUID.fromString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.jdbc.Sql;

import cz.kromer.restshopdemo.E2ETestParent;
import cz.kromer.restshopdemo.dto.OrderDto;
import cz.kromer.restshopdemo.dto.OrderItemDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.dto.ProductDto;

@Disabled("Locking Test uses thread sleeping. Only for proper locking investigation purpose.")
class ProductLockingTest extends E2ETestParent {

    static final UUID PRODUCT_1_ID = fromString("10b10895-cce9-48c6-bc8c-7025d0a7fe57");
    static final UUID PRODUCT_2_ID = fromString("3e752234-0a19-49c0-ba18-cfebf0bb7772");

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductLockingTransaction lockingTransaction;

    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldLockProductForConcurrentOrder() throws InterruptedException {
        taskExecutor.execute(() -> lockingTransaction.lockAndWait(PRODUCT_2_ID, 4500));
        sleep(50);

        orderService.save(OrderDto.builder().items(List.of(
                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_1_ID).build()).amount(ONE).build(),
                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_2_ID).build()).amount(ONE).build()))
                .build());

        ProductDto product1 = productService.getById(PRODUCT_1_ID);
        ProductDto product2 = productService.getById(PRODUCT_2_ID);
        assertThat(product1.getStock(), comparesEqualTo(BigDecimal.valueOf(29)));
        assertThat(product2.getStock(), comparesEqualTo(BigDecimal.valueOf(49)));
    }
}
