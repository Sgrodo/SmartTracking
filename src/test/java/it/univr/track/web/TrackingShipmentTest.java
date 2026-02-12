package it.univr.track.web;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class TrackingShipmentTest extends WebBaseTest{

    @Test
    public void testActivateTracking(){
        // Create shipment with empty device
        given()
                .formParam("fromLocation", "Warehouse A")
                .formParam("toLocation", "Warehouse B")
                .formParam("unitLoadIds", 2)
        .when()
                .post(this.baseUrl + "/newShipment")
        .then()
                .log().all()
                .statusCode(302);

        // Unbind device 9 from trackable 2
        given()
                .formParam("deviceId", 9)
                .formParam("shipmentId", 2)
        .when()
                .post(this.baseUrl + "/trackables/2/devices/unbind")
        .then()
                .log().all()
                .statusCode(302);

        // Activate tracking for shipment 2
        given()
        .when()
                .post(this.baseUrl + "/shipments/2/tracking/activate")
        .then()
                .log().all()
                .body(containsString("Nessun Device è assegnato ai Trackable"));
    }

    @Test
    public void testDeactivateTracking(){
        // Create shipment with empty device
        given()
                .formParam("fromLocation", "Warehouse A")
                .formParam("toLocation", "Warehouse B")
                .formParam("unitLoadIds", 2)
        .when()
                .post(this.baseUrl + "/newShipment")
        .then()
                .log().all()
                .statusCode(302);

        // Unbind device 9 from trackable 2
        given()
                .formParam("deviceId", 9)
                .formParam("shipmentId", 2)
        .when()
                .post(this.baseUrl + "/trackables/2/devices/unbind")
        .then()
                .log().all()
                .statusCode(302);

        // Activate tracking for shipment 2
        given()
        .when()
                .post(this.baseUrl + "/shipments/2/tracking/deactivate")
        .then()
                .log().all()
                .body(containsString("Nessun Device è assegnato ai Trackable"));
    }

}
