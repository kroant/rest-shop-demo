package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.SpringTest;
import cz.kromer.restshopdemo.dto.CreateOrderDto;
import cz.kromer.restshopdemo.dto.OrderItemDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.dto.OrderResponseDto;
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto;
import cz.kromer.restshopdemo.dto.validation.UniqueOrderProduct;
import cz.kromer.restshopdemo.service.OrderService;
import cz.kromer.restshopdemo.service.ProductService;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static cz.kromer.restshopdemo.TestConstants.CASHEW_NUTS_PRODUCT_ID;
import static cz.kromer.restshopdemo.TestConstants.MILK_1_L_PRODUCT_ID;
import static cz.kromer.restshopdemo.TestConstants.MILK_500_ML_PRODUCT_ID;
import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static cz.kromer.restshopdemo.TestConstants.SQL_COMPLEX_TEST_DATA;
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
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class OrderControllerValidationTest extends SpringTest {

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Test
    void shouldFail400_WhenOrderItemNull() {
        ErrorResponseDto response = given()
                .contentType(JSON)
                .body(CreateOrderDto.builder()
                        .items(asList(null,
                                OrderItemDto.builder().product(OrderProductDto.builder().id(CASHEW_NUTS_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(110_000)).build()))
                        .build())
                .post("/orders")
                .then()
                .statusCode(BAD_REQUEST.value())
                .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(REQUEST_VALIDATION_ERROR);
        assertThat(response.getErrorDetails()).satisfiesExactly(detail -> {
            assertThat(detail.getEntityId()).isNull();
            assertThat(detail.getField()).isEqualTo("items[0]");
            assertThat(detail.getMessage()).isEqualTo("must not be null");
            assertThat(detail.getValues()).satisfiesExactly(detailValue -> {
                assertThat(detailValue.getType()).isSameAs(VALIDATION_CODE);
                assertThat(detailValue.getValue()).isEqualTo(NotNull.class.getSimpleName());
            });
        });
    }

    @Test
    void shouldFail400_WhenProductIdNull() {
        ErrorResponseDto response = given()
                .contentType(JSON)
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(null).build())
                                        .amount(TEN).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(CASHEW_NUTS_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(110_000)).build()))
                        .build())
                .post("/orders")
                .then()
                .statusCode(BAD_REQUEST.value())
                .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(REQUEST_VALIDATION_ERROR);
        assertThat(response.getErrorDetails()).satisfiesExactly(detail -> {
            assertThat(detail.getEntityId()).isNull();
            assertThat(detail.getField()).isEqualTo("items[0].product.id");
            assertThat(detail.getMessage()).isEqualTo("must not be null");
            assertThat(detail.getValues()).satisfiesExactly(detailValue -> {
                assertThat(detailValue.getType()).isSameAs(VALIDATION_CODE);
                assertThat(detailValue.getValue()).isEqualTo(NotNull.class.getSimpleName());
            });
        });
    }

    @Test
    void shouldFail400_WhenProductIdDuplicate() {
        ErrorResponseDto response = given()
                .contentType(JSON)
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(MILK_1_L_PRODUCT_ID).build())
                                        .amount(TEN).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(MILK_1_L_PRODUCT_ID).build())
                                        .amount(ONE).build()))
                        .build())
                .post("/orders")
                .then()
                .statusCode(BAD_REQUEST.value())
                .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(REQUEST_VALIDATION_ERROR);
        assertThat(response.getErrorDetails()).satisfiesExactly(detail -> {
            assertThat(detail.getEntityId()).isNull();
            assertThat(detail.getField()).isEqualTo("items");
            assertThat(detail.getMessage()).isEqualTo("product must be unique");
            assertThat(detail.getValues()).satisfiesExactly(detailValue -> {
                assertThat(detailValue.getType()).isSameAs(VALIDATION_CODE);
                assertThat(detailValue.getValue()).isEqualTo(UniqueOrderProduct.class.getSimpleName());
            });
        });
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldFail400AndLeaveStockUnchanged_WhenIllegalAmountScale() {
        ErrorResponseDto response = given()
                .contentType(JSON)
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(MILK_500_ML_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(4)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(CASHEW_NUTS_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(10_005, 1)).build()))
                        .build())
                .post("/orders")
                .then()
                .statusCode(BAD_REQUEST.value())
                .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(ILLEGAL_AMOUNT_SCALE);
        assertThat(response.getErrorDetails()).satisfiesExactly(detail -> {
            assertThat(detail.getEntityId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID);
            assertThat(detail.getField()).isNull();
            assertThat(detail.getMessage()).isNull();
            assertThat(detail.getValues()).isNull();
        });
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).getStock()).isEqualByComparingTo("30");
        assertThat(productService.getById(CASHEW_NUTS_PRODUCT_ID).getStock()).isEqualByComparingTo("100000");
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldFail400AndLeaveStockUnchanged_WhenProductStockShortage() {
        ErrorResponseDto response = given()
                .contentType(JSON)
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(MILK_500_ML_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(32)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(CASHEW_NUTS_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(110_000)).build()))
                        .build())
                .post("/orders")
                .then()
                .statusCode(BAD_REQUEST.value())
                .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(PRODUCT_STOCK_SHORTAGE);
        assertThat(response.getErrorDetails()).satisfiesExactly(
                detail -> {
                    assertThat(detail.getEntityId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
                    assertThat(detail.getField()).isNull();
                    assertThat(detail.getMessage()).isNull();
                    assertThat(detail.getValues()).satisfiesExactly(value -> {
                        assertThat(value.getType()).isSameAs(MISSING_AMOUNT);
                        assertThat(value.getValue()).isEqualTo("2.000");
                    });
                }, detail -> {
                    assertThat(detail.getEntityId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID);
                    assertThat(detail.getField()).isNull();
                    assertThat(detail.getMessage()).isNull();
                    assertThat(detail.getValues()).satisfiesExactly(value -> {
                        assertThat(value.getType()).isSameAs(MISSING_AMOUNT);
                        assertThat(value.getValue()).isEqualTo("10000.000");
                    });
                }
        );
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).getStock()).isEqualByComparingTo("30");
        assertThat(productService.getById(CASHEW_NUTS_PRODUCT_ID).getStock()).isEqualByComparingTo("100000");
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldFail400AndLeaveStockUnchanged_WhenProductNotFound() {
        UUID productId = UUID.fromString("c51933b3-60bb-4b50-a29b-ad71f962095a");

        ErrorResponseDto response = given()
                .contentType(JSON)
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(MILK_500_ML_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(4)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(productId).build())
                                        .amount(BigDecimal.valueOf(1000)).build()))
                        .build())
                .post("/orders")
                .then()
                .statusCode(BAD_REQUEST.value())
                .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(ENTITY_NOT_FOUND);
        assertThat(response.getErrorDetails()).satisfiesExactly(detail -> {
            assertThat(detail.getEntityId()).isEqualTo(productId);
            assertThat(detail.getField()).isNull();
            assertThat(detail.getMessage()).isNull();
            assertThat(detail.getValues()).isNull();
        });
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).getStock()).isEqualByComparingTo("30");
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldFail400AndNotCancel_WhenIllegalOrderState() {
        UUID paidOrderId = UUID.fromString("e2a878e6-72c6-49f5-b391-cb60fbca944e");

        ErrorResponseDto response = when()
                .put("/orders/{id}/cancel", paidOrderId)
                .then()
                .statusCode(BAD_REQUEST.value())
                .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(ILLEGAL_ORDER_STATE);
        assertThat(response.getErrorDetails()).satisfiesExactly(detail -> {
            assertThat(detail.getEntityId()).isEqualTo(paidOrderId);
            assertThat(detail.getField()).isNull();
            assertThat(detail.getMessage()).isNull();
            assertThat(detail.getValues()).satisfiesExactly(
                    value -> {
                        assertThat(value.getType()).isSameAs(CURRENT_STATE);
                        assertThat(value.getValue()).isEqualTo(PAID.name());
                    }, value -> {
                        assertThat(value.getType()).isSameAs(ALLOWED_STATE);
                        assertThat(value.getValue()).isEqualTo(NEW.name());
                    }
            );
        });
        OrderResponseDto order = orderService.getById(paidOrderId);
        assertThat(order.getId()).isEqualTo(paidOrderId);
        assertThat(order.getState()).isSameAs(PAID);
    }
}
