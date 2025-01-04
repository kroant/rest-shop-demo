package cz.kromer.restshopdemo.config;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@Configuration
@ConfigurationProperties(prefix = "app.scheduling")
public class SchedulingProps {

    final OrderCancellationProps orderCancellation = new OrderCancellationProps();
}
