package it.univr.track.web;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;


public class CreateUnitLoadWebTest extends WebBaseTest {


    @Test
    public void testNoTemperatureUnit() {
        given()
                .formParam("description", "test unitload")
                .formParam("temperatureValue", 50)
                //.formParam("temperatureUnit", "Celsius")
                .formParam("loadItemIds", 1, 2)
                .formParam("deviceIds", 1)
                .when()
                .post(this.baseUrl + "/unitloads/new")
                .then()
                .log().all()
                .body(containsString("Temperature unit is required"));
    }

    @Test
    public void testValidTemperatureConstraint() {
        given()
                .formParam("description", "test unitload")
                .formParam("temperatureValue", 50)
                .formParam("temperatureUnit", "CELSIUS")
                .formParam("loadItemIds", 1, 2)
                .formParam("deviceIds", 1)
                .when()
                .post(this.baseUrl + "/unitloads/new")
                .then()
                .log().all()
                .body(not(containsString("p id=\"error\"")));
    }

    @Test
    public void testEmptyLoadItems() {
        given()
                .formParam("description", "test unitload")
                .formParam("temperatureValue", 50)
                .formParam("temperatureUnit", "CELSIUS")
                .formParam("loadItemIds", "")
                .formParam("deviceIds", 1)
                .when()
                .post(this.baseUrl + "/unitloads/new")
                .then()
                .log().all()
                .body(containsString("loadItemIds is required"));
    }

    @Test
    public void testEmptyAndNullDevices() {
        given()
                .formParam("description", "test unitload")
                .formParam("temperatureValue", 50)
                .formParam("temperatureUnit", "CELSIUS")
                .formParam("loadItemIds", 1, 2)
                .formParam("deviceIds", "")
                .when()
                .post(this.baseUrl + "/unitloads/new")
                .then()
                .log().all()
                .body(containsString("deviceIds is required"));

        given()
                .formParam("description", "test unitload")
                .formParam("temperatureValue", 50)
                .formParam("temperatureUnit", "CELSIUS")
                .formParam("loadItemIds", 1, 2)
                .when()
                .post(this.baseUrl + "/unitloads/new")
                .then()
                .log().all()
                .body(containsString("deviceIds is required"));
    }

}
