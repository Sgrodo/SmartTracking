package it.univr.track.rest;

import io.restassured.RestAssured;
import it.univr.track.BaseTest;
import org.junit.jupiter.api.BeforeAll;

public class BaseRestTest extends BaseTest {

    @BeforeAll
    public static void setBaseUri() {
        RestAssured.baseURI = "http://localhost:8080";
    }

}
