package cz.kromer.restshopdemo.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public enum QuantityUnit {
    PIECE(0),
    GRAM(0),
    LITER(3),
    METER(3);

    int maxScale;
}
