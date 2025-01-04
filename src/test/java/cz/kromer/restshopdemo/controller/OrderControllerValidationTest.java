package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.SpringTest;
import cz.kromer.restshopdemo.dto.CreateOrderDto;
import cz.kromer.restshopdemo.dto.OrderItemDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.dto.validation.UniqueOrderProduct;
import cz.kromer.restshopdemo.service.ProductService;
import io.restassured.RestAssured;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static cz.kromer.restshopdemo.controller.OrderControllerTest.PRODUCT_1_ID;
import static cz.kromer.restshopdemo.controller.OrderControllerTest.PRODUCT_2_ID;
import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static cz.kromer.restshopdemo.dto.OrderState.PAID;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.ALLOWED_STATE;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.CURRENT_STATE;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.MISSING_AMOUNT;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.VALIDATION_CODE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ENTITY_NOT_FOUND;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ILLEGAL_AMOUNT_SCALE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ILLEGAL_ORDER_STATE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.PRODUCT_STOCK_SHORTAGE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.REQUEST_VALIDATION_ERROR;
import static io.restassured.http.ContentType.JSON;
import static java.util.Arrays.asList;
import static java.util.UUID.fromString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

class OrderControllerValidationTest extends SpringTest {

    static final UUID ORDER_PAID_ID = fromString("e2a878e6-72c6-49f5-b391-cb60fbca944e");

    @Autowired
    private ProductService productService;

    @Test
    void shouldFail400_WhenOrderItemNull() {
        RestAssured.given().contentType(JSON).log().all()
                .body(CreateOrderDto.builder()
                        .items(asList(null,
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_2_ID).build())
                                        .amount(BigDecimal.valueOf(110000)).build()))
                        .build())
                .when()
            .post("/orders").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(REQUEST_VALIDATION_ERROR.name()))
            .body("errorDetails", hasSize(1))
            .body("errorDetails[0].field", is("items[0]"))
            .body("errorDetails[0].values", hasSize(1))
            .body("errorDetails[0].values[0].type", is(VALIDATION_CODE.name()))
            .body("errorDetails[0].values[0].value", is(NotNull.class.getSimpleName()));
    }

    @Test
    void shouldFail400_WhenProductIdNull() {
        RestAssured.given().contentType(JSON).log().all()
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(null).build())
                                        .amount(BigDecimal.valueOf(32)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_2_ID).build())
                                        .amount(BigDecimal.valueOf(110000)).build()))
                        .build())
                .when()
            .post("/orders").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(REQUEST_VALIDATION_ERROR.name()))
            .body("errorDetails", hasSize(1))
            .body("errorDetails[0].field", is("items[0].product.id"))
            .body("errorDetails[0].values", hasSize(1))
            .body("errorDetails[0].values[0].type", is(VALIDATION_CODE.name()))
            .body("errorDetails[0].values[0].value", is(NotNull.class.getSimpleName()));
    }

    @Test
    void shouldFail400_WhenProductIdDuplicate() {
        RestAssured.given().contentType(JSON).log().all()
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_1_ID).build())
                                        .amount(BigDecimal.valueOf(32)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_1_ID).build())
                                        .amount(BigDecimal.valueOf(110000)).build()))
                        .build())
                .when()
            .post("/orders").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(REQUEST_VALIDATION_ERROR.name()))
            .body("errorDetails", hasSize(1))
            .body("errorDetails[0].field", is("items"))
            .body("errorDetails[0].values", hasSize(1))
            .body("errorDetails[0].values[0].type", is(VALIDATION_CODE.name()))
            .body("errorDetails[0].values[0].value", is(UniqueOrderProduct.class.getSimpleName()));
    }

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldFail400AndLeaveStockUnchanged_WhenIllegalAmountScale() {
        RestAssured.given().contentType(JSON).log().all()
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_1_ID).build())
                                        .amount(BigDecimal.valueOf(32)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_2_ID).build())
                                        .amount(BigDecimal.valueOf(1100005, 1)).build()))
                        .build())
                .when()
            .post("/orders").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(ILLEGAL_AMOUNT_SCALE.name()))
            .body("errorDetails", hasSize(1))
            .body("errorDetails[0].entityId", is(PRODUCT_2_ID.toString()))
            .body("errorDetails[0].values", nullValue());

        ProductDto product1 = productService.getById(PRODUCT_1_ID);
        ProductDto product2 = productService.getById(PRODUCT_2_ID);
        assertThat(product1.getStock(), comparesEqualTo(BigDecimal.valueOf(30)));
        assertThat(product2.getStock(), comparesEqualTo(BigDecimal.valueOf(100000)));
    }

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldFail400AndLeaveStockUnchanged_WhenProductStockShortage() {
        RestAssured.given().contentType(JSON).log().all()
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_1_ID).build())
                                        .amount(BigDecimal.valueOf(32)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_2_ID).build())
                                        .amount(BigDecimal.valueOf(110000)).build()))
                        .build())
                .when()
            .post("/orders").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(PRODUCT_STOCK_SHORTAGE.name()))
            .body("errorDetails", hasSize(2))
            .body("errorDetails[0].entityId", is(PRODUCT_1_ID.toString()))
            .body("errorDetails[0].values", hasSize(1))
            .body("errorDetails[0].values[0].type", is(MISSING_AMOUNT.name()))
            .body("errorDetails[0].values[0].value", is("2.000"))
            .body("errorDetails[1].entityId", is(PRODUCT_2_ID.toString()))
            .body("errorDetails[1].values", hasSize(1))
            .body("errorDetails[1].values[0].type", is(MISSING_AMOUNT.name()))
            .body("errorDetails[1].values[0].value", is("10000.000"));

        ProductDto product1 = productService.getById(PRODUCT_1_ID);
        ProductDto product2 = productService.getById(PRODUCT_2_ID);
        assertThat(product1.getStock(), comparesEqualTo(BigDecimal.valueOf(30)));
        assertThat(product2.getStock(), comparesEqualTo(BigDecimal.valueOf(100000)));
    }

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldFail400AndLeaveStockUnchanged_WhenProductNotFound() {
        RestAssured.given().contentType(JSON).log().all()
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_1_ID).build())
                                        .amount(BigDecimal.valueOf(32)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(new UUID(0, 0)).build())
                                        .amount(BigDecimal.valueOf(110000)).build()))
                        .build())
                .when()
            .post("/orders").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(ENTITY_NOT_FOUND.name()))
            .body("errorDetails", hasSize(1))
            .body("errorDetails[0].entityId", is(new UUID(0, 0).toString()))
            .body("errorDetails[0].values", nullValue());

        ProductDto product1 = productService.getById(PRODUCT_1_ID);
        assertThat(product1.getStock(), comparesEqualTo(BigDecimal.valueOf(30)));
    }

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldFail400AndNotCancel_WhenIllegalOrderState() {
        RestAssured.given().when()
            .put("/orders/{id}/cancel", ORDER_PAID_ID).then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(ILLEGAL_ORDER_STATE.name()))
            .body("errorDetails", hasSize(1))
            .body("errorDetails[0].entityId", is(ORDER_PAID_ID.toString()))
            .body("errorDetails[0].values", hasSize(2))
            .body("errorDetails[0].values[0].type", is(CURRENT_STATE.name()))
            .body("errorDetails[0].values[0].value", is(PAID.name()))
            .body("errorDetails[0].values[1].type", is(ALLOWED_STATE.name()))
            .body("errorDetails[0].values[1].value", is(NEW.name()));

        RestAssured.given().when()
            .get("/orders/{id}", ORDER_PAID_ID).then().log().all().assertThat()
            .statusCode(OK.value())
            .body("id", is(ORDER_PAID_ID.toString()))
            .body("state", is(PAID.name()));
    }
}
