package cz.kromer.restshopdemo.dto.validation;

import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.dto.QuantityUnit;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ProductStockMaxScaleValidator implements ConstraintValidator<ProductStockMaxScale, ProductDto> {

    @Override
    public boolean isValid(ProductDto value, ConstraintValidatorContext context) {
        if (value == null || value.getUnit() == null || value.getStock() == null) {
            return true;
        }
        return isScaleValid(value.getStock(), value.getUnit());
    }

    public static boolean isScaleValid(BigDecimal value, QuantityUnit unit) {
        return value.stripTrailingZeros().scale() <= getMaxScale(unit);
    }

    private static int getMaxScale(QuantityUnit unit) {
        return switch (unit) {
            case LITER, METER -> 3;
            default -> 0;
        };
    }
}
