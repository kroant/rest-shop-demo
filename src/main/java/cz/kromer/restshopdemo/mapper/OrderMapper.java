package cz.kromer.restshopdemo.mapper;

import org.mapstruct.Mapper;

import cz.kromer.restshopdemo.dto.OrderDto;
import cz.kromer.restshopdemo.entity.Order;

@Mapper(uses = { ProductMapper.class })
public interface OrderMapper {

    OrderDto mapToOrderDto(Order order);

    Order mapToOrder(OrderDto order);
}
