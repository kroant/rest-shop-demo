package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.SpringTest;
import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto;
import cz.kromer.restshopdemo.dto.validation.ProductStockMaxScale;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static cz.kromer.restshopdemo.dto.QuantityUnit.PIECE;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.VALIDATION_CODE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.ENTITY_NOT_FOUND;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.REQUEST_VALIDATION_ERROR;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class ProductControllerValidationTest extends SpringTest {

    @Test
    void shouldFail400_whenIdInvalid() {
        when().get("/products/invalid_UUID")
            .then()
            .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldFail404_whenNotFound() {
        UUID productId = UUID.fromString("ea329635-7fae-48ab-816d-3b2255590311");
        ErrorResponseDto response = when()
            .get("/products/{id}", productId)
            .then()
            .statusCode(NOT_FOUND.value())
            .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(ENTITY_NOT_FOUND);
        assertThat(response.getErrorDetails()).satisfiesExactly(detail -> {
            assertThat(detail.getEntityId()).isEqualTo(productId);
            assertThat(detail.getField()).isNull();
            assertThat(detail.getMessage()).isNull();
            assertThat(detail.getValues()).isNull();
        });
    }

    @Test
    void shouldFail400_WhenValuesInFieldsInvalid() {
        ErrorResponseDto response = given()
            .contentType(JSON)
            .body(ProductDto.builder()
                .name("\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t")
                .unit(null)
                .price(BigDecimal.valueOf(-32))
                .stock(BigDecimal.valueOf(1, 4))
                .build())
            .post("/products")
            .then()
            .statusCode(BAD_REQUEST.value())
            .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(REQUEST_VALIDATION_ERROR);
        assertThat(response.getErrorDetails()).allSatisfy(detail -> {
            assertThat(detail.getEntityId()).isNull();
            assertThat(detail.getValues()).satisfiesExactly(detailValue ->
                assertThat(detailValue.getType()).isSameAs(VALIDATION_CODE));
        });
        assertThat(response.getErrorDetails()).satisfiesExactlyInAnyOrder(detail -> {
                assertThat(detail.getField()).isEqualTo("name");
                assertThat(detail.getMessage()).startsWith("must match");
                assertThat(detail.getValues()).satisfiesExactly(detailValue ->
                    assertThat(detailValue.getValue()).isEqualTo(Pattern.class.getSimpleName()));
            }, detail -> {
                assertThat(detail.getField()).isEqualTo("name");
                assertThat(detail.getMessage()).isEqualTo("must not be blank");
                assertThat(detail.getValues()).satisfiesExactly(detailValue ->
                    assertThat(detailValue.getValue()).isEqualTo(NotBlank.class.getSimpleName()));
            }, detail -> {
                assertThat(detail.getField()).isEqualTo("name");
                assertThat(detail.getMessage()).startsWith("size must be between 0 and");
                assertThat(detail.getValues()).satisfiesExactly(detailValue ->
                    assertThat(detailValue.getValue()).isEqualTo(Size.class.getSimpleName()));
            }, detail -> {
                assertThat(detail.getField()).isEqualTo("unit");
                assertThat(detail.getMessage()).isEqualTo("must not be null");
                assertThat(detail.getValues()).satisfiesExactly(detailValue ->
                    assertThat(detailValue.getValue()).isEqualTo(NotNull.class.getSimpleName()));
            }, detail -> {
                assertThat(detail.getField()).isEqualTo("price");
                assertThat(detail.getMessage()).isEqualTo("must be greater than or equal to 0");
                assertThat(detail.getValues()).satisfiesExactly(detailValue ->
                    assertThat(detailValue.getValue()).isEqualTo(PositiveOrZero.class.getSimpleName()));
            }, detail -> {
                assertThat(detail.getField()).isEqualTo("stock");
                assertThat(detail.getMessage()).startsWith("numeric value out of bounds");
                assertThat(detail.getValues()).satisfiesExactly(detailValue ->
                    assertThat(detailValue.getValue()).isEqualTo(Digits.class.getSimpleName()));
            }
        );
    }

    @Test
    void shouldFail400_WhenPieceScaleInvalid() {
        ErrorResponseDto response = given()
            .contentType(JSON)
            .body(ProductDto.builder()
                .name("Invalid scale")
                .unit(PIECE)
                .price(TEN)
                .stock(BigDecimal.valueOf(105, 1))
                .build())
            .post("/products")
            .then()
            .statusCode(BAD_REQUEST.value())
            .extract().as(ErrorResponseDto.class);

        assertThat(response.getErrorCode()).isSameAs(REQUEST_VALIDATION_ERROR);
        assertThat(response.getErrorDetails()).satisfiesExactly(detail -> {
            assertThat(detail.getEntityId()).isNull();
            assertThat(detail.getField()).isNull();
            assertThat(detail.getMessage()).isEqualTo("product stock scale must be less or equal to unit max scale");
            assertThat(detail.getValues()).satisfiesExactly(detailValue -> {
                assertThat(detailValue.getType()).isSameAs(VALIDATION_CODE);
                assertThat(detailValue.getValue()).isEqualTo(ProductStockMaxScale.class.getSimpleName());
            });
        });
    }
}
