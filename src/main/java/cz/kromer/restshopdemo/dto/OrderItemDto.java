package cz.kromer.restshopdemo.dto;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
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
    @Digits(integer = 16, fraction = 3)
    BigDecimal amount;
}
