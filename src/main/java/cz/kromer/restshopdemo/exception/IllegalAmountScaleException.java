package cz.kromer.restshopdemo.exception;

import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class IllegalAmountScaleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    UUID productId;

    public IllegalAmountScaleException(UUID productId) {
        super("Illegal amount scale for product: " + productId);
        this.productId = productId;
    }
}
