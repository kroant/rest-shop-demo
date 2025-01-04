package cz.kromer.restshopdemo.exception;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class IllegalAmountScaleException extends RuntimeException {

    UUID productId;

    public IllegalAmountScaleException(UUID productId) {
        super("Illegal amount scale for product: " + productId);
        this.productId = productId;
    }
}
