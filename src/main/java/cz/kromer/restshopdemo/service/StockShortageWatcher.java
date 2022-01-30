package cz.kromer.restshopdemo.service;

import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cz.kromer.restshopdemo.entity.OrderItem;
import cz.kromer.restshopdemo.entity.Product;
import cz.kromer.restshopdemo.exception.ProductShortageException;
import cz.kromer.restshopdemo.exception.ProductStockShortageDto;
import cz.kromer.restshopdemo.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@Scope(SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class StockShortageWatcher {

    ProductMapper productMapper;

    List<ProductStockShortageDto> shortages = new LinkedList<>();

    void take(List<OrderItem> orderItems) {
        orderItems.stream().forEach(this::take);
        if (!shortages.isEmpty()) {
            throw new ProductShortageException(shortages);
        }
    }

    private void take(OrderItem item) {
        Product product = item.getProduct();
        final BigDecimal newAmount = product.getStock().subtract(item.getAmount());
        if (newAmount.compareTo(ZERO) < 0) {
            shortages.add(ProductStockShortageDto.builder()
                    .product(productMapper.mapToOrderProduct(product))
                    .missingAmount(newAmount.negate())
                    .build());
        } else {
            product.setStock(newAmount);
        }
    }
}
