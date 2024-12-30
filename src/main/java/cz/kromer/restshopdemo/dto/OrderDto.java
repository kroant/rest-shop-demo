package cz.kromer.restshopdemo.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import cz.kromer.restshopdemo.dto.validation.UniqueOrderProduct;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderDto {

    UUID id;

    OrderState state;

    BigDecimal price;

    @Valid
    @NotEmpty
    @UniqueOrderProduct
    List<@NotNull OrderItemDto> items;

    Instant createdOn;
}
