package cz.kromer.restshopdemo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.entity.Product;

@Mapper
public interface ProductMapper {

    ProductDto mapToProductDto(Product product);

    Product mapToProduct(ProductDto product);

    @Mapping(target = "id", ignore = true)
    void mapToProduct(ProductDto source, @MappingTarget Product entity);

    OrderProductDto mapToOrderProduct(Product product);
}
