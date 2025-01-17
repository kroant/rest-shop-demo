package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.SpringTest;
import cz.kromer.restshopdemo.dto.CreateOrderDto;
import cz.kromer.restshopdemo.dto.OrderItemDto;
import cz.kromer.restshopdemo.dto.OrderProductDto;
import cz.kromer.restshopdemo.dto.OrderResponseDto;
import cz.kromer.restshopdemo.service.OrderService;
import cz.kromer.restshopdemo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static cz.kromer.restshopdemo.TestConstants.CASHEW_NUTS_PRODUCT_ID;
import static cz.kromer.restshopdemo.TestConstants.MILK_1_L_PRODUCT_ID;
import static cz.kromer.restshopdemo.TestConstants.MILK_500_ML_PRODUCT_ID;
import static cz.kromer.restshopdemo.TestConstants.SQL_CLEANUP;
import static cz.kromer.restshopdemo.TestConstants.SQL_COMPLEX_TEST_DATA;
import static cz.kromer.restshopdemo.dto.OrderState.CANCELLED;
import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static cz.kromer.restshopdemo.dto.OrderState.PAID;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

class OrderControllerTest extends SpringTest {

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldGetAllOrders_WhenExist() {
        List<OrderResponseDto> response = when()
                .get("/orders")
                .then()
                .statusCode(OK.value())
                .extract().jsonPath().getList(".", OrderResponseDto.class);

        assertThat(response).satisfiesExactly(
                order -> {
                    assertThat(order.getId()).isEqualTo(UUID.fromString("b3a48eee-65a4-431b-a11a-e770a7f0ba8b"));
                    assertThat(order.getState()).isSameAs(NEW);
                    assertThat(order.getPrice()).isEqualByComparingTo("787");
                    assertThat(order.getCreatedOn()).isEqualTo("2022-01-05T16:58:46Z");
                    assertThat(order.getItems()).satisfiesExactly(
                            item -> {
                                assertThat(item.getProduct().getId()).isEqualTo(MILK_1_L_PRODUCT_ID);
                                assertThat(item.getProduct().getName()).isEqualTo("Milk 1 l");
                                assertThat(item.getAmount()).isEqualByComparingTo("2");
                            }, item -> {
                                assertThat(item.getProduct().getId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID);
                                assertThat(item.getProduct().getName()).isEqualTo("Cashew Nuts");
                                assertThat(item.getAmount()).isEqualByComparingTo("2500");
                            }
                    );
                }, order -> {
                    assertThat(order.getId()).isEqualTo(UUID.fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9"));
                    assertThat(order.getState()).isSameAs(NEW);
                    assertThat(order.getPrice()).isEqualByComparingTo("162");
                    assertThat(order.getCreatedOn()).isEqualTo("2022-01-05T14:23:08Z");
                    assertThat(order.getItems()).satisfiesExactly(
                            item -> {
                                assertThat(item.getProduct().getId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
                                assertThat(item.getProduct().getName()).isEqualTo("Milk 500 ml");
                                assertThat(item.getAmount()).isEqualByComparingTo("1");
                            }, item -> {
                                assertThat(item.getProduct().getId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID);
                                assertThat(item.getProduct().getName()).isEqualTo("Cashew Nuts");
                                assertThat(item.getAmount()).isEqualByComparingTo("500");
                            }
                    );
                }, order -> {
                    assertThat(order.getId()).isEqualTo(UUID.fromString("e2a878e6-72c6-49f5-b391-cb60fbca944e"));
                    assertThat(order.getState()).isSameAs(PAID);
                    assertThat(order.getPrice()).isEqualByComparingTo("120.5");
                    assertThat(order.getCreatedOn()).isEqualTo("2022-01-10T09:43:00Z");
                    assertThat(order.getItems()).satisfiesExactly(
                            item -> {
                                assertThat(item.getProduct().getId()).isEqualTo(MILK_1_L_PRODUCT_ID);
                                assertThat(item.getProduct().getName()).isEqualTo("Milk 1 l");
                                assertThat(item.getAmount()).isEqualByComparingTo("1");
                            }, item -> {
                                assertThat(item.getProduct().getId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
                                assertThat(item.getProduct().getName()).isEqualTo("Milk 500 ml");
                                assertThat(item.getAmount()).isEqualByComparingTo("1");
                            }, item -> {
                                assertThat(item.getProduct().getId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID);
                                assertThat(item.getProduct().getName()).isEqualTo("Cashew Nuts");
                                assertThat(item.getAmount()).isEqualByComparingTo("300");
                            }
                    );
                }, order -> {
                    assertThat(order.getId()).isEqualTo(UUID.fromString("27408323-1031-4658-8995-7ecff8f2b26f"));
                    assertThat(order.getState()).isSameAs(CANCELLED);
                    assertThat(order.getPrice()).isEqualByComparingTo("0");
                    assertThat(order.getCreatedOn()).isEqualTo("2022-01-15T06:29:59Z");
                    assertThat(order.getItems()).isEmpty();
                }
        );
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldGetOrderById_WhenExists() {
        UUID orderId = UUID.fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9");

        OrderResponseDto response = when()
                .get("/orders/{id}", orderId)
                .then()
                .statusCode(OK.value())
                .extract().as(OrderResponseDto.class);

        assertThat(response.getId()).isEqualTo(orderId);
        assertThat(response.getState()).isSameAs(NEW);
        assertThat(response.getPrice()).isEqualByComparingTo("162");
        assertThat(response.getCreatedOn()).isEqualTo("2022-01-05T14:23:08Z");
        assertThat(response.getItems()).satisfiesExactly(
                item -> {
                    assertThat(item.getProduct().getId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
                    assertThat(item.getProduct().getName()).isEqualTo("Milk 500 ml");
                    assertThat(item.getAmount()).isEqualByComparingTo("1");
                }, item -> {
                    assertThat(item.getProduct().getId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID);
                    assertThat(item.getProduct().getName()).isEqualTo("Cashew Nuts");
                    assertThat(item.getAmount()).isEqualByComparingTo("500");
                }
        );
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldCreateOrderAndUpdateStock_WhenValid() {
        Instant startTime = now().truncatedTo(SECONDS);

        OrderResponseDto response = given()
                .contentType(JSON)
                .body(CreateOrderDto.builder()
                        .items(List.of(
                                OrderItemDto.builder().product(OrderProductDto.builder().id(MILK_500_ML_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(4)).build(),
                                OrderItemDto.builder().product(OrderProductDto.builder().id(CASHEW_NUTS_PRODUCT_ID).build())
                                        .amount(BigDecimal.valueOf(750)).build()))
                        .build())
                .post("/orders")
                .then()
                .statusCode(OK.value())
                .extract().as(OrderResponseDto.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getState()).isSameAs(NEW);
        assertThat(response.getPrice()).isEqualByComparingTo("273");
        assertThat(response.getCreatedOn()).isAfterOrEqualTo(startTime);
        assertThat(response.getItems()).satisfiesExactly(
                item -> {
                    assertThat(item.getProduct().getId()).isEqualTo(MILK_500_ML_PRODUCT_ID);
                    assertThat(item.getProduct().getName()).isEqualTo("Milk 500 ml");
                    assertThat(item.getAmount()).isEqualByComparingTo("4");
                }, item -> {
                    assertThat(item.getProduct().getId()).isEqualTo(CASHEW_NUTS_PRODUCT_ID);
                    assertThat(item.getProduct().getName()).isEqualTo("Cashew Nuts");
                    assertThat(item.getAmount()).isEqualByComparingTo("750");
                }
        );
        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).getStock()).isEqualByComparingTo("26");
        assertThat(productService.getById(CASHEW_NUTS_PRODUCT_ID).getStock()).isEqualByComparingTo("99250");
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldCancelOrderAndReturnToStock() {
        UUID orderId = UUID.fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9");

        when().put("/orders/{id}/cancel", orderId)
                .then()
                .statusCode(NO_CONTENT.value());

        OrderResponseDto order = orderService.getById(orderId);
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getState()).isSameAs(CANCELLED);
        assertThat(order.getPrice()).isEqualByComparingTo("162");

        assertThat(productService.getById(MILK_500_ML_PRODUCT_ID).getStock()).isEqualByComparingTo("31");
        assertThat(productService.getById(CASHEW_NUTS_PRODUCT_ID).getStock()).isEqualByComparingTo("100500");
    }

    @Test
    @Sql({SQL_CLEANUP, SQL_COMPLEX_TEST_DATA})
    void shouldPayOrder() {
        UUID orderId = UUID.fromString("fa254654-bdbc-431b-8b9e-f6bf34540ee9");

        when().put("/orders/{id}/pay", orderId)
                .then()
                .statusCode(NO_CONTENT.value());

        OrderResponseDto order = orderService.getById(orderId);
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getState()).isSameAs(PAID);
        assertThat(order.getPrice()).isEqualByComparingTo("162");
    }
}
