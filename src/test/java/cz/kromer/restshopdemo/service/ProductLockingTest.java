package cz.kromer.restshopdemo.service;

import cz.kromer.restshopdemo.SpringTest;
import cz.kromer.restshopdemo.dto.CreateOrderDto;
import cz.kromer.restshopdemo.dto.OrderItemDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static cz.kromer.restshopdemo.TestConstants.MILK_1_L_PRODUCT_ID;
import static cz.kromer.restshopdemo.TestConstants.MILK_500_ML_PRODUCT_ID;
import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static cz.kromer.restshopdemo.TestConstants.SQL_COMPLEX_TEST_DATA;
import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Locking Test uses thread sleeping. Only for proper locking investigation purpose.")
class ProductLockingTest extends SpringTest {

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @Autowired
    ObjectFactory<ProductLockingTransaction> lockingTransactionFactory;

    @Autowired
    TaskExecutor taskExecutor;

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldLockProductForConcurrentOrder() {
        ProductLockingTransaction lockingTransaction = lockingTransactionFactory.getObject();

        taskExecutor.execute(() -> lockingTransaction.lockAndSleep(MILK_500_ML_PRODUCT_ID, 2200));

        lockingTransaction.waitUntilProductLocked();

        orderService.save(CreateOrderDto.builder().items(List.of(
                    OrderItemDto.builder().product(OrderProductDto.builder().id(MILK_1_L_PRODUCT_ID).build()).amount(ONE).build(),
                    OrderItemDto.builder().product(OrderProductDto.builder().id(MILK_500_ML_PRODUCT_ID).build()).amount(ONE).build()))
                .build());

        assertThat(productService.getById(MILK_1_L_PRODUCT_ID).getStock()).isEqualByComparingTo("49");
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).getStock()).isEqualByComparingTo("29");
    }
}
