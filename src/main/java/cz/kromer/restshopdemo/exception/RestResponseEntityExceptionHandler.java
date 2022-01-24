package cz.kromer.restshopdemo.exception;

import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.ALLOWED_STATE;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.CURRENT_STATE;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.MISSING_AMOUNT;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.VALIDATION_CODE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ENTITY_NOT_FOUND;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ILLEGAL_AMOUNT_SCALE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ILLEGAL_ORDER_STATE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.PRODUCT_STOCK_SHORTAGE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.REQUEST_VALIDATION_ERROR;
import static java.util.stream.Stream.concat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.status;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import cz.kromer.restshopdemo.dto.error.ErrorDetailDto;
import cz.kromer.restshopdemo.dto.error.ErrorDetailValueDto;
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RootEntityNotFoundException.class)
    protected ResponseEntity<?> handleRootEntityNotFound(RootEntityNotFoundException e) {
        return status(NOT_FOUND).body(mapToEntityNotFoundResponse(e.getId()));
    }

    @ExceptionHandler(AssociatedEntityNotFoundException.class)
    protected ResponseEntity<ErrorResponseDto> handleAssociatedEntityNotFound(AssociatedEntityNotFoundException e) {
        return badRequest().body(mapToEntityNotFoundResponse(e.getId()));
    }

    @ExceptionHandler(ProductShortageException.class)
    protected ResponseEntity<ErrorResponseDto> handleProductShortage(ProductShortageException e) {
        return badRequest().body(ErrorResponseDto.builder()
                .errorCode(PRODUCT_STOCK_SHORTAGE)
                .errorDetails(e.getProductShortages().stream()
                        .map(RestResponseEntityExceptionHandler::mapToErrorDetail).toList())
                .build());
    }

    @ExceptionHandler(IllegalOrderStateException.class)
    protected ResponseEntity<ErrorResponseDto> handleIllegalOrderState(IllegalOrderStateException e) {
        return badRequest().body(ErrorResponseDto.builder()
                .errorCode(ILLEGAL_ORDER_STATE)
                .errorDetails(List.of(ErrorDetailDto.builder()
                        .entityId(e.getId())
                        .values(mapToDetailValues(e))
                        .build()))
                .build());
    }

    @ExceptionHandler(IllegalAmountScaleException.class)
    protected ResponseEntity<ErrorResponseDto> handleIllegalAmountScale(IllegalAmountScaleException e) {
        return badRequest().body(ErrorResponseDto.builder()
                .errorCode(ILLEGAL_AMOUNT_SCALE)
                .errorDetails(List.of(ErrorDetailDto.builder()
                        .entityId(e.getProductId())
                        .build()))
                .build());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return badRequest().body(ErrorResponseDto.builder()
                .errorCode(REQUEST_VALIDATION_ERROR)
                .errorDetails(e.getAllErrors().stream()
                        .map(RestResponseEntityExceptionHandler::mapToErrorDetail).toList())
                .build());
    }

    private static ErrorResponseDto mapToEntityNotFoundResponse(UUID entityId) {
        return ErrorResponseDto.builder()
                .errorCode(ENTITY_NOT_FOUND)
                .errorDetails(List.of(ErrorDetailDto.builder()
                        .entityId(entityId)
                        .build()))
                .build();
    }

    private static ErrorDetailDto mapToErrorDetail(ObjectError objectError) {
        return ErrorDetailDto.builder()
                .field(objectError instanceof FieldError fieldError ? fieldError.getField() : null)
                .values(List.of(ErrorDetailValueDto.builder()
                            .type(VALIDATION_CODE)
                            .value(objectError.getCode())
                            .build()))
                .build();
    }

    private static List<ErrorDetailValueDto> mapToDetailValues(IllegalOrderStateException e) {
        return concat(
                List.of(ErrorDetailValueDto.builder()
                        .type(CURRENT_STATE)
                        .value(e.getCurrentState().name()).build())
                        .stream(),
                e.getAllowedStates().stream()
                        .map(state -> ErrorDetailValueDto.builder()
                                .type(ALLOWED_STATE)
                                .value(state.name()).build()))
                .toList();
    }

    private static ErrorDetailDto mapToErrorDetail(ProductStockShortageDto shortage) {
        return ErrorDetailDto.builder()
                .entityId(shortage.getProduct().getId())
                .values(List.of(ErrorDetailValueDto.builder()
                        .type(MISSING_AMOUNT)
                        .value(shortage.getMissingAmount().toPlainString())
                        .build()))
                .build();
    }
}
