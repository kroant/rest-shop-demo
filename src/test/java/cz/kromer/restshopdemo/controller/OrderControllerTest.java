package cz.kromer.restshopdemo.controller;

import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static cz.kromer.restshopdemo.dto.OrderState.CANCELLED;
import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static cz.kromer.restshopdemo.dto.OrderState.PAID;
import static io.restassured.http.ContentType.JSON;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.UUID.fromString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.springframework.http.HttpStatus.OK;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import cz.kromer.restshopdemo.E2ETestParent;
import cz.kromer.restshopdemo.dto.OrderDto;
import cz.kromer.restshopdemo.dto.OrderItemDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.dto.ProductDto;
import cz.kromer.restshopdemo.service.ProductService;
import io.restassured.RestAssured;

class OrderControllerTest extends E2ETestParent {

    static final UUID ORDER_1_ID = fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9");

    static final UUID PRODUCT_1_ID = ProductControllerTest.PRODUCT_1_ID;
    static final UUID PRODUCT_2_ID = fromString("a3c64d30-cb49-4279-9a83-282a7d0c7669");

    @Autowired
    private ProductService productService;

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldGetAllOrders_whenExist() {
        RestAssured.given().when()
            .get("/orders").then().log().all().assertThat()
            .statusCode(OK.value())
            .body("$", hasSize(4))
            .body(containsString(ORDER_1_ID.toString()));
    }

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldGetOrderById_whenExists() {
        RestAssured.given().when()
            .get("/orders/{id}", ORDER_1_ID).then().log().all().assertThat()
            .statusCode(OK.value())
            .body("id", is(ORDER_1_ID.toString()))
            .body("state", is(NEW.name()))
            .body("createdOn", is("2022-01-05T14:23:08Z"))
            .body("items", hasSize(2))
            .body("items[0].product.id", is(PRODUCT_1_ID.toString()))
            .body("items[0].product.name", is("Milk 500 ml"))
            .body("items[0].amount", is(1f))
            .body("items[1].product.id", is(PRODUCT_2_ID.toString()))
            .body("items[1].product.name", is("Cashew Nuts"))
            .body("items[1].amount", is(500f));
    }

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldSaveOrderAndUpdateStock_WhenValid() {
        RestAssured.given().contentType(JSON).log().all()
                .body(OrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_1_ID).build())
                                        .amount(BigDecimal.valueOf(4)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(PRODUCT_2_ID).build())
                                        .amount(BigDecimal.valueOf(750)).build()))
                        .build())
                .when()
            .post("/orders").then().log().all().assertThat()
            .statusCode(OK.value())
            .body("id", notNullValue())
            .body("state", is(NEW.name()))
            .body("createdOn", greaterThan(Instant.now().minus(1, HOURS).toString()))
            .body("items", hasSize(2))
            .body("items[0].product.id", is(PRODUCT_1_ID.toString()))
            .body("items[0].product.name", is("Milk 500 ml"))
            .body("items[0].amount", is(4f))
            .body("items[1].product.id", is(PRODUCT_2_ID.toString()))
            .body("items[1].product.name", is("Cashew Nuts"))
            .body("items[1].amount", is(750f));

        ProductDto product1 = productService.getById(PRODUCT_1_ID);
        ProductDto product2 = productService.getById(PRODUCT_2_ID);
        assertThat(product1.getStock(), comparesEqualTo(BigDecimal.valueOf(26)));
        assertThat(product2.getStock(), comparesEqualTo(BigDecimal.valueOf(99250)));
    }

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldCancelOrderAndUpdateStock() {
        RestAssured.given().when()
            .put("/orders/{id}/cancel", ORDER_1_ID).then().assertThat()
            .statusCode(OK.value());

        RestAssured.given().when()
            .get("/orders/{id}", ORDER_1_ID).then().log().all().assertThat()
            .statusCode(OK.value())
            .body("id", is(ORDER_1_ID.toString()))
            .body("state", is(CANCELLED.name()));

        ProductDto product1 = productService.getById(PRODUCT_1_ID);
        ProductDto product2 = productService.getById(PRODUCT_2_ID);
        assertThat(product1.getStock(), comparesEqualTo(BigDecimal.valueOf(31)));
        assertThat(product2.getStock(), comparesEqualTo(BigDecimal.valueOf(100500)));
    }

    @Test
    @Sql({ SQL_CLEANUP, "/sql/complex-test-data.sql" })
    void shouldPayOrder() {
        RestAssured.given().when()
            .put("/orders/{id}/pay", ORDER_1_ID).then().assertThat()
            .statusCode(OK.value());

        RestAssured.given().when()
            .get("/orders/{id}", ORDER_1_ID).then().log().all().assertThat()
            .statusCode(OK.value())
            .body("id", is(ORDER_1_ID.toString()))
            .body("state", is(PAID.name()));
    }
}
