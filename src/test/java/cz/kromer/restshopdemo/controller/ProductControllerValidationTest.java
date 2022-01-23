package cz.kromer.restshopdemo.controller;

import static cz.kromer.restshopdemo.dto.QuantityUnit.PIECE;
import static cz.kromer.restshopdemo.dto.error.ErrorDetailValueType.VALIDATION_CODE;
import static cz.kromer.restshopdemo.dto.error.ErrorResponseCode.REQUEST_VALIDATION_ERROR;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.junit.jupiter.api.Test;

import cz.kromer.restshopdemo.E2ETestParent;
import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.dto.validation.ProductStockMaxScale;
import io.restassured.RestAssured;

class ProductControllerValidationTest extends E2ETestParent {

    @Test
    void shouldFail400_whenIdInvalid() {
        RestAssured.given().when()
            .get("/products/{id}", "invalid_UUID").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value());
    }

    @Test
    void shouldFail404_whenNotFound() {
        RestAssured.given().when()
            .get("/products/{id}", new UUID(0, 0)).then().assertThat()
            .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldFail400_WhenValuesInFieldsInvalid() {
        RestAssured.given().contentType(JSON).log().all()
            .body(ProductDto.builder()
                    .name("\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n"
                            + "\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t\n\t")
                    .unit(null)
                    .price(BigDecimal.valueOf(-32))
                    .stock(null)
                    .build()).when()
            .post("/products").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(REQUEST_VALIDATION_ERROR.name()))
            .body("errorDetails", hasSize(6))
            .body(containsString("name"))
            .body(containsString("unit"))
            .body(containsString("price"))
            .body(containsString("stock"))
            .body(containsString(NotBlank.class.getSimpleName()))
            .body(containsString(NotNull.class.getSimpleName()))
            .body(containsString(Size.class.getSimpleName()))
            .body(containsString(Pattern.class.getSimpleName()))
            .body(containsString(Positive.class.getSimpleName()))
            .body(containsString(VALIDATION_CODE.name()));
    }

    @Test
    void shouldFail400_WhenPieceScaleInvalid() {
        RestAssured.given().contentType(JSON).log().all()
            .body(ProductDto.builder()
                    .name("Invalid scale")
                    .unit(PIECE)
                    .price(TEN)
                    .stock(BigDecimal.valueOf(105, 1))
                    .build()).when()
            .post("/products").then().log().all().assertThat()
            .statusCode(BAD_REQUEST.value())
            .body("errorCode", is(REQUEST_VALIDATION_ERROR.name()))
            .body("errorDetails", hasSize(1))
            .body("errorDetails[0].field", nullValue())
            .body("errorDetails[0].values", hasSize(1))
            .body("errorDetails[0].values[0].type", is(VALIDATION_CODE.name()))
            .body("errorDetails[0].values[0].value", is(ProductStockMaxScale.class.getSimpleName()));
    }
}
