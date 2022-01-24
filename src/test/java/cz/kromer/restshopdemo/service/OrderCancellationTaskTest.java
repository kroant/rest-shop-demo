package cz.kromer.restshopdemo.service;

import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static cz.kromer.restshopdemo.dto.OrderState.CANCELLED;
import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static cz.kromer.restshopdemo.dto.OrderState.PAID;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import cz.kromer.restshopdemo.E2ETestParent;
import cz.kromer.restshopdemo.dto.OrderState;

class OrderCancellationTaskTest extends E2ETestParent {

    @Autowired
    private OrderCancellationTask task;

    @Autowired
    private OrderService orderService;

    @Test
    @Sql({ SQL_CLEANUP, "/sql/orders-recently-created.sql" })
    void shouldCancelObsoleteOrders() {
        task.cancelObsoleteOrders();

        assertOrderInState(fromString("0d3ea9d7-9c23-46c7-9c76-861586a4eeb1"), NEW);
        assertOrderInState(fromString("122c1845-1647-47ba-8254-7cfde64261bd"), CANCELLED);
        assertOrderInState(fromString("8b731ad0-35f8-4af1-b8a2-b4d874bf1dc8"), CANCELLED);
        assertOrderInState(fromString("959000c0-0fe5-478e-8ccf-bb47c4c07c6c"), PAID);
        assertOrderInState(fromString("4774fbe9-a259-4ac7-a6df-a160038c077d"), CANCELLED);
    }

    private void assertOrderInState(UUID id, OrderState state) {
        assertSame(state, orderService.getById(id).getState());
    }
}
