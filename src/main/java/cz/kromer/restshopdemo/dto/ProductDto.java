package cz.kromer.restshopdemo.dto;

import java.math.BigDecimal;
import java.util.UUID;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
    @Positive
    @Digits(integer = 17, fraction = 2)
    BigDecimal price;

    @NotNull
    @Positive
    @Digits(integer = 16, fraction = 3)
    BigDecimal stock;
}
