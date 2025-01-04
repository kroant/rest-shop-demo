package cz.kromer.restshopdemo.exception;

import cz.kromer.restshopdemo.dto.OrderProductDto;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductStockShortageDto {

    OrderProductDto product;
    BigDecimal missingAmount;
}
