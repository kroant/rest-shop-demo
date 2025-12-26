package cz.kromer.restshopdemo.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Workaround. OpenAPI model generator doesn't handle @NotNull annotation for items.
 */
@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = NotNullItemsValidator.class)
@Documented
public @interface NotNullItems {

    String message() default "{validation.NotNullItems.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
