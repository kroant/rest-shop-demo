package cz.kromer.restshopdemo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class ProductShortageException extends RuntimeException {

    List<ProductStockShortageDto> productShortages;
}
