package cz.kromer.restshopdemo.dto.error;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponseDto {

    ErrorResponseCode errorCode;
    List<ErrorDetailDto> errorDetails;
}
