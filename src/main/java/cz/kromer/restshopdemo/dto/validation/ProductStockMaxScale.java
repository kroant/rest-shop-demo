package cz.kromer.restshopdemo.dto.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ProductStockMaxScaleValidator.class)
@Documented
public @interface ProductStockMaxScale {

    String message() default "{validation.ProductStockMaxScale.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
