package cz.kromer.restshopdemo.exception;

import cz.kromer.restshopdemo.dto.OrderState;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class IllegalOrderStateException extends RuntimeException {

    UUID id;

    OrderState currentState;
    EnumSet<OrderState> allowedStates;

    public IllegalOrderStateException(UUID id, OrderState currentState, EnumSet<OrderState> allowedStates) {
        super("Illegal Order state. Order ID: %s, state: %s".formatted(id, currentState));
        this.id = id;
        this.currentState = currentState;
        this.allowedStates = allowedStates;
    }
}
