package cz.kromer.restshopdemo.dto.error;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDetailDto {

    UUID entityId;
    String field;
    List<ErrorDetailValueDto> values;
}
