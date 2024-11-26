package cz.kromer.restshopdemo.service;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cz.kromer.restshopdemo.config.SchedulingProps;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class OrderCancellationTask {

    OrderService orderService;
    SchedulingProps schedulingProps;

    @Scheduled(cron = "${app.scheduling.order-cancellation.cron}")
    @SchedulerLock(name = "cancelObsoleteOrders")
    public void cancelObsoleteOrders() {
        final Instant before = Instant.now()
                .minus(schedulingProps.getOrderCancellation().getNewOrderRetentionMinutes(), MINUTES)
                .truncatedTo(SECONDS);
        log.info("Cancelling NEW orders created before {}", before);
        orderService.findNewOrdersBefore(before)
                .forEach(this::cancelOrder);
    }

    private void cancelOrder(UUID id) {
        log.info("Cancelling order {}", id);
        try {
            orderService.cancel(id);
        } catch (RuntimeException e) {
            log.error("Exception during order cancelling: {}", id);
            log.error("Exception detail follows.", e);
        }
    }
}
