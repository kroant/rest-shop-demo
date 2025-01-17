package cz.kromer.restshopdemo.service;

import cz.kromer.restshopdemo.entity.OrderItem;
import cz.kromer.restshopdemo.entity.Product;
import cz.kromer.restshopdemo.exception.ProductShortageException;
import cz.kromer.restshopdemo.exception.ProductStockShortageDto;
import cz.kromer.restshopdemo.mapper.OrderProductDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class StockShortageWatcher {

    OrderProductDtoMapper orderProductDtoMapper;

    List<ProductStockShortageDto> shortages = new LinkedList<>();

    void take(List<OrderItem> orderItems) {
        orderItems.forEach(this::take);
        if (!shortages.isEmpty()) {
            throw new ProductShortageException(shortages);
        }
    }

    private void take(OrderItem item) {
        Product product = item.getProduct();
        final BigDecimal newAmount = product.getStock().subtract(item.getAmount());
        if (newAmount.compareTo(ZERO) < 0) {
            shortages.add(ProductStockShortageDto.builder()
                .product(orderProductDtoMapper.mapFrom(product))
                .missingAmount(newAmount.negate())
                .build());
        } else {
            product.setStock(newAmount);
        }
    }
}
