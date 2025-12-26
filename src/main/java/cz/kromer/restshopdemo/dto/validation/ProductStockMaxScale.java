package cz.kromer.restshopdemo.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ProductStockMaxScaleValidator.class)
@Documented
public @interface ProductStockMaxScale {

    String message() default "{validation.ProductStockMaxScale.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
