package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.SpringTest;
import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.service.ProductService;
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
import static cz.kromer.restshopdemo.dto.QuantityUnit.GRAM;
import static cz.kromer.restshopdemo.dto.QuantityUnit.LITER;
import static cz.kromer.restshopdemo.dto.QuantityUnit.PIECE;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

class ProductControllerTest extends SpringTest {

    @Autowired
    ProductService productService;

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldGetAllProducts_WhenExist() {
        List<ProductDto> response = when()
                .get("/products")
                .then()
                .statusCode(OK.value())
                .extract().jsonPath().getList(".", ProductDto.class);

        assertThat(response).satisfiesExactly(
                product -> {
                        assertThat(product.getId()).isEqualTo(MILK_1_L_PRODUCT_ID);
                        assertThat(product.getName()).isEqualTo("Milk 1 l");
                        assertThat(product.getUnit()).isSameAs(PIECE);
                        assertThat(product.getPrice()).isEqualByComparingTo("18.5");
                        assertThat(product.getStock()).isEqualByComparingTo("50");
                }, product -> {
                        assertThat(product.getId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
                        assertThat(product.getName()).isEqualTo("Milk 500 ml");
                        assertThat(product.getUnit()).isSameAs(PIECE);
                        assertThat(product.getPrice()).isEqualByComparingTo("12");
                        assertThat(product.getStock()).isEqualByComparingTo("30");
                }, product -> {
                        assertThat(product.getId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID);
                        assertThat(product.getName()).isEqualTo("Cashew Nuts");
                        assertThat(product.getUnit()).isSameAs(GRAM);
                        assertThat(product.getPrice()).isEqualByComparingTo("0.3");
                        assertThat(product.getStock()).isEqualByComparingTo("100000");
                }
        );
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldGetProductById_WhenExists() {
        ProductDto response = when()
                .get("/products/{id}", MILK_500_ML_PRODUCT_ID)
                .then()
                .statusCode(OK.value()).extract().as(ProductDto.class);

        assertThat(response.getId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
        assertThat(response.getName()).isEqualTo("Milk 500 ml");
        assertThat(response.getUnit()).isSameAs(PIECE);
        assertThat(response.getPrice()).isEqualByComparingTo("12");
        assertThat(response.getStock()).isEqualByComparingTo("30");
    }

    @Test
    void shouldSaveProduct_WhenValid() {
        ProductDto response = given()
                .contentType(JSON)
                .body(ProductDto.builder()
                        .name("Bread 1 kg")
                        .unit(PIECE)
                        .price(BigDecimal.valueOf(32))
                        .stock(BigDecimal.valueOf(6000, 2))
                        .build())
                .post("/products")
                .then()
                .statusCode(OK.value())
                .extract().as(ProductDto.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Bread 1 kg");
        assertThat(response.getUnit()).isSameAs(PIECE);
        assertThat(response.getPrice()).isEqualByComparingTo("32");
        assertThat(response.getStock()).isEqualByComparingTo("60");
    }

    @Test
    void shouldGenerateIdAndSaveProduct_WhenIdProvided() {
        UUID myUuid = UUID.fromString("dbff76c6-f59a-493b-95ee-98a4eef99a78");
        ProductDto response = given()
                .contentType(JSON)
                .body(ProductDto.builder()
                        .id(myUuid)
                        .name("Bread 1 kg")
                        .unit(PIECE)
                        .price(BigDecimal.valueOf(32))
                        .stock(BigDecimal.valueOf(60))
                        .build())
                .post("/products")
                .then()
                .statusCode(OK.value())
                .extract().as(ProductDto.class);

        assertThat(response.getId()).isNotNull().isNotEqualTo(myUuid);
        assertThat(response.getName()).isEqualTo("Bread 1 kg");
        assertThat(response.getUnit()).isSameAs(PIECE);
        assertThat(response.getPrice()).isEqualByComparingTo("32");
        assertThat(response.getStock()).isEqualByComparingTo("60");
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldUpdateProduct_WhenValidAndExists() {
        ProductDto response = given().contentType(JSON)
                .body(ProductDto.builder()
                        .name("Updated Milk 500 ml")
                        .unit(LITER)
                        .price(BigDecimal.valueOf(10))
                        .stock(BigDecimal.valueOf(40))
                        .build())
                .put("/products/{id}", MILK_500_ML_PRODUCT_ID)
                .then()
                .statusCode(OK.value())
                .extract().as(ProductDto.class);

        assertThat(response.getId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
        assertThat(response.getName()).isEqualTo("Updated Milk 500 ml");
        assertThat(response.getUnit()).isSameAs(LITER);
        assertThat(response.getPrice()).isEqualByComparingTo("10");
        assertThat(response.getStock()).isEqualByComparingTo("40");

        ProductDto updated = productService.getById(MILK_500_ML_PRODUCT_ID);
        assertThat(updated.getId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
        assertThat(updated.getName()).isEqualTo("Updated Milk 500 ml");
        assertThat(updated.getUnit()).isSameAs(LITER);
        assertThat(updated.getPrice()).isEqualByComparingTo("10");
        assertThat(updated.getStock()).isEqualByComparingTo("40");
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldDeleteProduct_WhenExists() {
        when().delete("/products/{id}", MILK_500_ML_PRODUCT_ID)
                .then()
                .statusCode(NO_CONTENT.value());

        assertThat(productService.findAll()).satisfiesExactly(
                product -> assertThat(product.getId()).isEqualTo(MILK_1_L_PRODUCT_ID),
                product -> assertThat(product.getId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID)
        );
    }
}
