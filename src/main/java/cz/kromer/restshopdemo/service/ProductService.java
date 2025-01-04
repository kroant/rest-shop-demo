package cz.kromer.restshopdemo.service;

import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.entity.Product;
import cz.kromer.restshopdemo.exception.RootEntityNotFoundException;
import cz.kromer.restshopdemo.mapper.ProductDtoMapper;
import cz.kromer.restshopdemo.mapper.ProductMapper;
import cz.kromer.restshopdemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;
    ProductDtoMapper productDtoMapper;

    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream()
                .map(productDtoMapper::mapFrom)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getById(UUID id) {
        return productDtoMapper.mapFrom(
                productRepository.findById(id)
                        .orElseThrow(() -> new RootEntityNotFoundException(id))
        );
    }

    @Transactional
    public UUID save(ProductDto product) {
        Product entity = productMapper.mapFrom(product);
        entity = productRepository.save(entity);
        return entity.getId();
    }

    @Retryable(retryFor = { ConcurrencyFailureException.class })
    @Transactional
    public void update(UUID id, ProductDto product) {
        Product entity = productRepository.findAndLockById(id)
                .orElseThrow(() -> new RootEntityNotFoundException(id));
        productMapper.mapToProduct(product, entity);
    }

    @Retryable(retryFor = { ConcurrencyFailureException.class })
    @Transactional
    public void delete(UUID id) {
        Product entity = productRepository.findAndLockById(id)
                .orElseThrow(() -> new RootEntityNotFoundException(id));
        entity.setDeleted(true);
    }
}
