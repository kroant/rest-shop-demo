package cz.kromer.restshopdemo.service;

import cz.kromer.restshopdemo.config.SchedulingProps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCancellationTaskExceptionHandlingTest {

    @Mock
    private OrderService orderService;

    @Spy
    private Clock clock = Clock.systemDefaultZone();

    @Spy
    private SchedulingProps schedulingProps = new SchedulingProps();

    @InjectMocks
    private OrderCancellationTask orderCancellationTask;

    @Test
    void shouldProcessSecondOrder_WhenFirstOrderFails() {
        UUID firstOrderId = UUID.fromString("daa9498b-c136-4fe1-8684-721ef41c15d1");
        UUID secondOrderId = UUID.fromString("104c5d5e-f4fc-49e1-92b4-14b9a50e7110");

        when(orderService.findNewOrdersBefore(any()))
                .thenReturn(List.of(firstOrderId, secondOrderId));

        doThrow(new RuntimeException("Exception during cancelling an order"))
                .when(orderService).cancel(firstOrderId);

        orderCancellationTask.cancelObsoleteOrders();

        verify(orderService).cancel(firstOrderId);
        verify(orderService).cancel(secondOrderId);
    }
}
