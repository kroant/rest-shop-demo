package cz.kromer.restshopdemo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cz.kromer.restshopdemo.dto.OrderDto;
import cz.kromer.restshopdemo.entity.Order;

@Mapper(uses = { ProductMapper.class })
public interface OrderMapper {

    OrderDto mapToOrderDto(Order order);

    @Mapping(target = "id", ignore = true)
    Order mapToOrder(OrderDto order);
}
