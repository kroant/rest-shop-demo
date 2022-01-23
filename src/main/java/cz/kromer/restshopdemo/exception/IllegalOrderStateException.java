package cz.kromer.restshopdemo.exception;

import static lombok.AccessLevel.PRIVATE;

import java.util.EnumSet;
import java.util.UUID;

import cz.kromer.restshopdemo.dto.OrderState;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class IllegalOrderStateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    UUID id;

    OrderState currentState;
    EnumSet<OrderState> allowedStates;

    public IllegalOrderStateException(UUID id, OrderState currentState, EnumSet<OrderState> allowedStates) {
        super("Illegal Order state. Order ID: " + id + ", state: " + currentState);
        this.id = id;
        this.currentState = currentState;
        this.allowedStates = allowedStates;
    }
}
