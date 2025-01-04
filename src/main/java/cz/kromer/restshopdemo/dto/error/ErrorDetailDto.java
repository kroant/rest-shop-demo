package cz.kromer.restshopdemo.dto.error;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ErrorDetailDto {

    UUID entityId;
    String field;
    String message;
    List<ErrorDetailValueDto> values;
}
