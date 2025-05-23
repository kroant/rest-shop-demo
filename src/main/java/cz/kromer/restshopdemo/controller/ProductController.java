package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto;
import cz.kromer.restshopdemo.service.ProductService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class ProductController {

    ProductService productService;

    @GetMapping
    List<ProductDto> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = ProductDto.class)) })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    ProductDto getById(@PathVariable UUID id) {
        return productService.getById(id);
    }

    @PostMapping
    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = ProductDto.class)) })
    @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    ProductDto save(@Valid @RequestBody ProductDto product) {
        return productService.getById(productService.save(product));
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = ProductDto.class)) })
    @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    ProductDto update(@PathVariable UUID id, @Valid @RequestBody ProductDto product) {
        productService.update(id, product);
        return productService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiResponse(responseCode = "204", content = { @Content })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    void delete(@PathVariable UUID id) {
        productService.delete(id);
    }
}
