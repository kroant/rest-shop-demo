package cz.kromer.restshopdemo.dto.error;

public enum ErrorResponseCode {

    ENTITY_NOT_FOUND,
    REQUEST_VALIDATION_ERROR,
    PRODUCT_STOCK_SHORTAGE,
    ILLEGAL_ORDER_STATE,
    ILLEGAL_AMOUNT_SCALE
}
