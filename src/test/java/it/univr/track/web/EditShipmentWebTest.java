package it.univr.track.web;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class EditShipmentWebTest extends WebBaseTest{

    @Test
    public void testNoFromLocation() {
        given()
                .formParam("toLocation", "Warehouse B")
        .when()
                .post(this.baseUrl + "/shipments/1/edit")
        .then()
                .log().all()
                .body(containsString("Invalid fromLocation, it cannot be Empty."));

        given()
                .formParam("fromLocation", "")
                .formParam("toLocation", "Warehouse B")
        .when()
                .post(this.baseUrl + "/shipments/1/edit")
        .then()
                .log().all()
                .body(containsString("Invalid fromLocation, it cannot be Empty."));
    }

    @Test
    public void testNoToLocation() {
        given()
                .formParam("fromLocation", "Warehouse A")
        .when()
                .post(this.baseUrl + "/shipments/1/edit")
        .then()
                .log().all()
                .body(containsString("Invalid toLocation, it cannot be Empty."));


        given()
                .formParam("fromLocation", "Warehouse A")
                .formParam("toLocation", "")
        .when()
                .post(this.baseUrl + "/shipments/1/edit")
        .then()
                .log().all()
                .body(containsString("Invalid toLocation, it cannot be Empty."));
    }

    @Test
    public void testEmptyNewTrackables() {
        given()
                .formParam("fromLocation", "Warehouse A")
                .formParam("toLocation", "Warehouse B")
                .formParam("newTrackableIds", "")
        .when()
                .post(this.baseUrl + "/shipments/1/edit")
        .then()
                .log().all()
                .body(containsString("Trackables need to be present"));
    }
}
