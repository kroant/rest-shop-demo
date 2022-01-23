package cz.kromer.restshopdemo.config;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
@Configuration
@ConfigurationProperties(prefix = "app.scheduling")
public class SchedulingProps {

    final OrderCancellationProps orderCancellation = new OrderCancellationProps();

    @Data
    @FieldDefaults(level = PRIVATE)
    public static class OrderCancellationProps {

        long newOrderRetentionMinutes = 30;
    }
}
