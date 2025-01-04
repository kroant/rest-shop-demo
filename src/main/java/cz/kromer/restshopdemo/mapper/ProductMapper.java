package cz.kromer.restshopdemo.mapper;

import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.entity.Product;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Product mapFrom(ProductDto product);

    @InheritConfiguration
    void mapToProduct(ProductDto source, @MappingTarget Product entity);
}
