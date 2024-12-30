package cz.kromer.restshopdemo.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
