package cz.kromer.restshopdemo;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;

import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.filter.log.LogDetail.ALL;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public abstract class SpringTest {

    @MockitoBean
    protected Clock clock;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        RestAssured.config = RestAssured.config()
                .logConfig(
                        logConfig()
                                .enableLoggingOfRequestAndResponseIfValidationFails(ALL)
                                .enablePrettyPrinting(true)
                );
        RestAssured.replaceFiltersWith(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}
