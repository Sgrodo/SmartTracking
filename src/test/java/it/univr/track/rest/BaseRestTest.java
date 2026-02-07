package it.univr.track.rest;

import io.restassured.RestAssured;
import it.univr.track.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BaseRestTest extends BaseTest {

    @BeforeAll
    public static void setBaseUri() {
        RestAssured.baseURI = "http://localhost:8080";
    }

}
