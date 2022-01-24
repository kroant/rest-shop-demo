package cz.kromer.restshopdemo.service;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import cz.kromer.restshopdemo.config.SchedulingProps;

@ExtendWith(MockitoExtension.class)
class OrderCancellationTaskMockedTest {

    private static final UUID ORDER_1_ID = randomUUID();
    private static final UUID ORDER_2_ID = randomUUID();

    @Mock
    private OrderService orderService;

    @Spy
    private SchedulingProps schedulingProps = new SchedulingProps();

    @InjectMocks
    private OrderCancellationTask task;

    @Test
    void test() {
        when(orderService.findNewOrdersBefore(any())).thenReturn(List.of(ORDER_1_ID, ORDER_2_ID));
        doThrow(new RuntimeException("Exception during cancelling an order")).when(orderService).cancel(eq(ORDER_1_ID));

        task.cancelObsoleteOrders();

        verify(orderService, times(1)).cancel(eq(ORDER_2_ID));
    }
}
