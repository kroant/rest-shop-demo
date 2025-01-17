package cz.kromer.restshopdemo.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import cz.kromer.restshopdemo.dto.validation.ProductStockMaxScale;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@ProductStockMaxScale
public class ProductDto {

    UUID id;

    @NotBlank
    // Single line text
    @Pattern(regexp = "^[\\p{L}\\p{S}\\p{N}\\p{P}\\x20]*$")
    @Size(max = 100)
    String name;

    @NotNull
    QuantityUnit unit;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 17, fraction = 2)
    BigDecimal price;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 16, fraction = 3)
    BigDecimal stock;
}
