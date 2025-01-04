package cz.kromer.restshopdemo.mapper;

import cz.kromer.restshopdemo.dto.OrderResponseDto;
import cz.kromer.restshopdemo.entity.Order;
import org.mapstruct.Mapper;

@Mapper(uses = OrderProductDtoMapper.class)
public interface OrderResponseDtoMapper {

    OrderResponseDto mapFrom(Order order);
}
