package it.univr.track.web;

import io.restassured.RestAssured;
import it.univr.track.rest.BaseRestTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.annotation.DirtiesContext;

public class WebBaseTest extends BaseRestTest {

    protected final String baseUrl = "http://localhost:8080/web";

}
