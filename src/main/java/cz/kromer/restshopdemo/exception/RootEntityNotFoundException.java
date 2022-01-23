package cz.kromer.restshopdemo.exception;

import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class RootEntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    UUID id;

    public RootEntityNotFoundException(UUID id) {
        super("Entity not found: " + id);
        this.id = id;
    }
}
