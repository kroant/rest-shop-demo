package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.api.ProductsApi;
import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class ProductController implements ProductsApi {

    ProductService productService;

    public List<ProductDto> getAllProducts() {
        return productService.findAll();
    }

    public ProductDto getProductById(UUID id) {
        return productService.getById(id);
    }

    public ProductDto saveProduct(ProductDto product) {
        return productService.getById(productService.save(product));
    }

    public ProductDto updateProduct(UUID id, ProductDto product) {
        productService.update(id, product);
        return productService.getById(id);
    }

    public void deleteProduct(UUID id) {
        productService.delete(id);
    }
}
