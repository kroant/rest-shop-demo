package cz.kromer.restshopdemo.dto.validation;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import cz.kromer.restshopdemo.dto.OrderItemDto;

public class UniqueOrderProductValidator implements ConstraintValidator<UniqueOrderProduct, List<OrderItemDto>> {

    @Override
    public boolean isValid(List<OrderItemDto> items, ConstraintValidatorContext context) {
        if (!isEmpty(items)) {
            HashSet<UUID> uuidSet = new HashSet<>();
            for (OrderItemDto item : items) {
                if (item != null && item.getProduct() != null && item.getProduct().getId() != null) {
                    if (!uuidSet.add(item.getProduct().getId())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
