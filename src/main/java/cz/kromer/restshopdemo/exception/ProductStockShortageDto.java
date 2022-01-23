package cz.kromer.restshopdemo.exception;

import java.math.BigDecimal;

import cz.kromer.restshopdemo.dto.OrderProductDto;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductStockShortageDto {

    OrderProductDto product;
    BigDecimal missingAmount;
}
