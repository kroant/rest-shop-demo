package cz.kromer.restshopdemo.exception;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class ProductShortageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    List<ProductStockShortageDto> productShortages;
}
