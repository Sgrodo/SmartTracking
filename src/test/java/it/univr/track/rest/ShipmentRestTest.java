package it.univr.track.rest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class ShipmentRestTest extends BaseRestTest {


    @Test
    public void testCreateShipment() {
        String json = """
                {
                    "description": "Test Shipment",
                    "startDate": "2024-06-01T10:00:00Z",
                    "trackableIds": [2, 3]
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(json)
        .when()
                .post("/shipment")
        .then()
                .statusCode(200);

        // Trying to create the same shipment again should fail due to duplicate trackableIds
        given()
                .header("Content-Type", "application/json")
                .body(json)
        .when()
                .post("/shipment")
        .then()
                .statusCode(500);

    }

    @Test
    public void testGetShipment() {
        given()
        .when()
                .get("/shipment/1")
        .then()
                .statusCode(200);

        // Test non-existent shipment
        given()
        .when()
                .get("/shipment/999")
        .then()
                .statusCode(404);

    }

    @Test
    public void testDeleteShipment() {
        // Check if the shipment exists before deleting
        given()
        .when()
                .get("shipment/1")
        .then()
                .statusCode(200);

        // Now delete the shipment
        given()
        .when()
                .delete("/api/shipment?shipmentId=1")
        .then()
                .statusCode(200);

        // Check if the shipment has been deleted
        given()
        .when()
                .get("/shipment/1")
        .then()
                .statusCode(404);

        // Delete non-existent shipment
        given()
        .when()
                .delete("/api/shipment?shipmentId=999")
        .then()
                .statusCode(404);


    }

    @Test
    public void testUpdateShipment() {
        String json = """
                {
                    "shipmentId": 1,
                    "description": "Updated Shipment",
                    "endDate": "2024-06-10T10:00:00Z",
                    "state": "IN_TRANSIT",
                    "trackableIds": [2, 3]
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(json)
        .when()
                .put("/api/shipment")
        .then()
                .statusCode(200);

        // Test invalid parameters
        // Non-existent shipment ID
        String invalidJson = """
                {
                    "shipmentId": 999,
                    "description": "Invalid Shipment",
                    "endDate": "2024-06-10T10:00:00Z",
                    "state": "IN_TRANSIT",
                    "trackableIds": [2, 3]
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(invalidJson)
                .log().all()
        .when()
                .put("/api/shipment")
        .then()
                .log().all()
                .statusCode(404);

        // Non-existent trackable ID
        String invalidJson2 = """
                {
                    "shipmentId": 1,
                    "description": "Invalid Shipment",
                    "endDate": "2024-06-10T10:00:00Z",
                    "state": "IN_TRANSIT",
                    "trackableIds": [999]
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(invalidJson2)
        .when()
                .put("/api/shipment")
        .then()
                .statusCode(400);

        // Empty trackable IDs
        String invalidJson3 = """
                {
                    "shipmentId": 1,
                    "description": "Invalid Shipment",
                    "endDate": "2024-06-10T10:00:00Z",
                    "state": "IN_TRANSIT",
                    "trackableIds": []
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(invalidJson3)
                .log().all()
        .when()
                .put("/api/shipment")
        .then()
                .log().all()
                .statusCode(400);

        // Null trackable IDs
        String invalidJson4 = """
                {
                    "shipmentId": 1,
                    "description": "Invalid Shipment",
                    "endDate": "2024-06-10T10:00:00Z",
                    "state": "IN_TRANSIT",
                    "trackableIds": [null]
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(invalidJson4)
                .log().all()
        .when()
                .put("/api/shipment")
        .then()
                .log().all()
                .statusCode(400);

        // Invalid state
        String invalidJson5 = """
                {
                    "shipmentId": 1,
                    "description": "Invalid Shipment",
                    "endDate": "2024-06-10T10:00:00Z",
                    "state": "INVALID_STATE",
                    "trackableIds": [2, 3]
                }
                """;

        given()
                .header("Content-Type", "application/json")
                .body(invalidJson5)
                .log().all()
        .when()
                .put("/api/shipment")
        .then()
                .log().all()
                .statusCode(400);


    }

}
