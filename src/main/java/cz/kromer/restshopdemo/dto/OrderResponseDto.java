package cz.kromer.restshopdemo.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderResponseDto {

    UUID id;
    OrderState state;
    BigDecimal price;
    List<OrderItemDto> items;
    Instant createdOn;
}
