package cz.kromer.restshopdemo.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import cz.kromer.restshopdemo.dto.validation.UniqueOrderProduct;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderDto {

    UUID id;

    OrderState state;

    @Valid
    @NotEmpty
    @UniqueOrderProduct
    List<@NotNull OrderItemDto> items;

    Instant createdOn;
}
