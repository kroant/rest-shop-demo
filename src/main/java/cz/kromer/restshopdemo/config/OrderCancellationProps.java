package cz.kromer.restshopdemo.config;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Duration;

import static java.time.Duration.ofMinutes;
import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class OrderCancellationProps {

    Duration newOrderRetentionDuration = ofMinutes(30);
}
