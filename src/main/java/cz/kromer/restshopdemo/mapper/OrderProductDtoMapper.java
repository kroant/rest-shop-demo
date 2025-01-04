package cz.kromer.restshopdemo.mapper;

import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.entity.Product;
import org.mapstruct.Mapper;

@Mapper
public interface OrderProductDtoMapper {

    OrderProductDto mapFrom(Product product);
}
