package cz.kromer.restshopdemo.mapper;

import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.entity.Product;
import org.mapstruct.Mapper;

@Mapper
public interface ProductDtoMapper {

    ProductDto mapFrom(Product product);
}
