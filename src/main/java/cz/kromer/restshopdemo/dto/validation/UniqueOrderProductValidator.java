package cz.kromer.restshopdemo.dto.validation;

import cz.kromer.restshopdemo.dto.OrderItemDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static java.util.HashSet.newHashSet;
import static org.springframework.util.CollectionUtils.isEmpty;

public class UniqueOrderProductValidator implements ConstraintValidator<UniqueOrderProduct, List<OrderItemDto>> {

    @Override
    public boolean isValid(List<OrderItemDto> items, ConstraintValidatorContext context) {
        if (!isEmpty(items)) {
            HashSet<UUID> uuidSet = newHashSet(items.size());
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
