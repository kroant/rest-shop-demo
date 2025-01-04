package cz.kromer.restshopdemo.exception;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class RootEntityNotFoundException extends RuntimeException {

    UUID id;

    public RootEntityNotFoundException(UUID id) {
        super("Entity not found: " + id);
        this.id = id;
    }
}
