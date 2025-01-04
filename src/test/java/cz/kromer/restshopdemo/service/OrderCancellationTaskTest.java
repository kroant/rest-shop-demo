package cz.kromer.restshopdemo.service;

import cz.kromer.restshopdemo.SpringTest;
import cz.kromer.restshopdemo.dto.OrderState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static cz.kromer.restshopdemo.TestConstants.SQL_COMPLEX_TEST_DATA;
import static cz.kromer.restshopdemo.dto.OrderState.CANCELLED;
import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static cz.kromer.restshopdemo.dto.OrderState.PAID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class OrderCancellationTaskTest extends SpringTest {

    @Autowired
    private OrderCancellationTask task;

    @Autowired
    private OrderService orderService;

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldCancelObsoleteOrders() {
        when(clock.instant()).thenReturn(Instant.parse("2022-01-05T16:00:00Z"));

        task.cancelObsoleteOrders();

        assertOrderInState("27408323-1031-4658-8995-7ecff8f2b26f", CANCELLED);
        assertOrderInState("fa254654-bdbc-431b-8b9e-f6bf34540ee9", CANCELLED);
        assertOrderInState("b3a48eee-65a4-431b-a11a-e770a7f0ba8b", NEW);
        assertOrderInState("e2a878e6-72c6-49f5-b391-cb60fbca944e", PAID);
    }

    private void assertOrderInState(String id, OrderState state) {
        assertThat(orderService.getById(UUID.fromString(id)).getState()).isSameAs(state);
    }
}
