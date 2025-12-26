package cz.kromer.restshopdemo.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotNullItemsValidator implements ConstraintValidator<NotNullItems, Iterable<?>> {

    @Override
    public boolean isValid(Iterable<?> value, ConstraintValidatorContext context) {
        if (value != null) {
            for (Object o : value) {
                if (o == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
