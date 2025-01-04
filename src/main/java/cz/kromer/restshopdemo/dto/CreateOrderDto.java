package cz.kromer.restshopdemo.dto;

import cz.kromer.restshopdemo.dto.validation.UniqueOrderProduct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CreateOrderDto {

    @NotEmpty
    @UniqueOrderProduct
    List<@Valid @NotNull OrderItemDto> items;
}
