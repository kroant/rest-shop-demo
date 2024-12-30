package cz.kromer.restshopdemo.dto.validation;

import java.math.BigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.dto.QuantityUnit;

public class ProductStockMaxScaleValidator implements ConstraintValidator<ProductStockMaxScale, ProductDto> {

    @Override
    public boolean isValid(ProductDto value, ConstraintValidatorContext context) {
        if (value == null || value.getUnit() == null || value.getStock() == null) {
            return true;
        }
        return isScaleValid(value.getStock(), value.getUnit());
    }

    public static boolean isScaleValid(BigDecimal value, QuantityUnit unit) {
        return value.stripTrailingZeros().scale() <= unit.getMaxScale();
    }
}
