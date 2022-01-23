package cz.kromer.restshopdemo.dto.error;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDetailValueDto {

    ErrorDetailValueType type;
    String value;
}
