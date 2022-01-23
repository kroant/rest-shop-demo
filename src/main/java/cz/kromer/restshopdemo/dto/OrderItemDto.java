package cz.kromer.restshopdemo.dto;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderItemDto {

    @NotNull
    @Valid
    OrderProductDto product;

    @NotNull
    @Positive
    BigDecimal amount;
}
