package cz.kromer.restshopdemo.exception;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class AssociatedEntityNotFoundException extends RuntimeException {

    UUID id;

    public AssociatedEntityNotFoundException(UUID id) {
        super("Entity not found: " + id);
        this.id = id;
    }
}
