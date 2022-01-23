package cz.kromer.restshopdemo.service;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.entity.Product;
import cz.kromer.restshopdemo.exception.RootEntityNotFoundException;
import cz.kromer.restshopdemo.mapper.ProductMapper;
import cz.kromer.restshopdemo.repository.ProductLockingRepository;
import cz.kromer.restshopdemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    ProductLockingRepository productLockingRepository;
    ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::mapToProductDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getById(UUID id) {
        return productMapper.mapToProductDto(
                productRepository.findById(id).orElseThrow(() -> new RootEntityNotFoundException(id)));
    }

    @Transactional
    public UUID save(ProductDto product) {
        Product entity = productMapper.mapToProduct(product);
        entity = productRepository.save(entity);
        return entity.getId();
    }

    @Retryable(include = { ConcurrencyFailureException.class })
    @Transactional
    public void update(UUID id, ProductDto product) {
        Product entity = productLockingRepository.findById(id).orElseThrow(() -> new RootEntityNotFoundException(id));
        productMapper.mapToProduct(product, entity);
    }

    @Transactional
    public void delete(UUID id) {
        Product entity = productRepository.findById(id).orElseThrow(() -> new RootEntityNotFoundException(id));
        entity.setDeleted(true);
    }
}
