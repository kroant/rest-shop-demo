package cz.kromer.restshopdemo.dto.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueOrderProductValidator.class)
@Documented
public @interface UniqueOrderProduct {

    String message() default "{validation.UniqueOrderProduct.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
