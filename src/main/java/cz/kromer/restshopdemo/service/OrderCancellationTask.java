package cz.kromer.restshopdemo.service;

import cz.kromer.restshopdemo.config.SchedulingProps;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class OrderCancellationTask {

    Clock clock;
    OrderService orderService;
    SchedulingProps schedulingProps;

    @Scheduled(cron = "${app.scheduling.order-cancellation.cron}")
    @SchedulerLock(name = "cancelObsoleteOrders")
    public void cancelObsoleteOrders() {
        final Instant before = now(clock)
                .minus(schedulingProps.getOrderCancellation().getNewOrderRetentionDuration())
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
