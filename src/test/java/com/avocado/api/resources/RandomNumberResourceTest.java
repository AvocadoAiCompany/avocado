package com.avocado.api.resources;

import io.dropwizard.testing.junit5.DropwizardTestSupport;
import io.dropwizard.testing.junit5.TestingServerExtension;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RandomNumberResourceTest {
    private final TestingServerExtension server = new DropwizardTestSupport<>(
            RandomNumberConfiguration.class,
            "src/test/resources/test-config.yaml"
    ).getServer();

    @Test
    public void returnsRandomNumber() {
        given()
            .when()
            .get("/random")
            .then()
            .statusCode(200)
            .body("number", allOf(
                greaterThanOrEqualTo(0),
                lessThanOrEqualTo(100),
                notNullValue()
            ));
    }
}
