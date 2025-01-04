package cz.kromer.restshopdemo.mapper;

import cz.kromer.restshopdemo.dto.CreateOrderDto;
import cz.kromer.restshopdemo.dto.OrderItemDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.entity.Order;
import cz.kromer.restshopdemo.entity.OrderItem;
import cz.kromer.restshopdemo.entity.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.function.Function;

@Mapper
public abstract class OrderMapper {

    @Mapping(target = "state", constant = "NEW")
    @Mapping(target = "price", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    public abstract Order mapFrom(CreateOrderDto order, @Context Function<OrderProductDto, Product> productResolver);

    @Mapping(target = "order", ignore = true)
    protected abstract OrderItem mapFrom(OrderItemDto orderItem, @Context Function<OrderProductDto, Product> productResolver);

    protected Product resolveProduct(OrderProductDto orderProduct, @Context Function<OrderProductDto, Product> productResolver) {
        return productResolver.apply(orderProduct);
    }

    @AfterMapping
    protected void afterMapping(@MappingTarget Order order) {
        order.getItems().forEach(item -> item.setOrder(order));
    }
}
