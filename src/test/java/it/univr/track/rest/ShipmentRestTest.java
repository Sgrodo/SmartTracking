package it.univr.track.rest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;

public class ShipmentRestTest extends BaseRestTest {


    @Test
    public void testCreateShipment() {
        String json = """
                {
                    "description": "Test Shipment",
                    "startDate": "2024-06-01T10:00:00Z",
                    "from": "Location A",
                    "to": "Location B",
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

    @Test
    public void testSetTrackingState(){
        // Find shipment id in list of shipments
        int id = given()
                .when()
                        .get("/api/shipments")
                .then()
                        .statusCode(200)
                        .extract()
                        .path("find { it.description == 'SAMPLE SHIPMENT' }.id");

        // Set tracking state to ACTIVE
        given()
        .when()
                .post("/api/shipment/tracking?shipmentId=" + id +"&state=1")
        .then()
                .statusCode(200);

        // Check devices state is ACTIVE
        given()
        .when()
                .get("/shipment/" + id)
        .then()
                .statusCode(200)
                .body("trackables.devices.state.flatten()", everyItem(is("ACTIVE")));

        // Set tracking state to INACTIVE
        given()
        .when()
                .post("/api/shipment/tracking?shipmentId=" + id +"&state=0")
        .then()
                .statusCode(200);

        // Check devices state is INACTIVE
        given()
        .when()
                .get("/shipment/" + id)
        .then()
                .statusCode(200)
                .body("trackables.devices.state.flatten()", everyItem(is("INACTIVE")));
    }

    @Test
    public void testAllocateAndDeallocate(){
        // extract devices length before allocation
        int devicesLengthBefore = given()
        .when()
                .get("/api/unitloads/1")
        .then()
                .statusCode(200)
                .extract()
                .path("devices.size()");

        // allocate a device to the trackable
        given()
        .when()
                .post("/api/shipment/allocate?trackableId=1&deviceId=1")
        .then()
                .statusCode(200);

        // check if the device is allocated to the trackable
        given()
        .when()
                .get("/api/unitloads/1")
        .then()
                .statusCode(200)
                .body("devices.size()", is(devicesLengthBefore + 1));

        // allocate the same device again should fail
        given()
        .when()
                .post("/api/shipment/allocate?trackableId=2&deviceId=1")
        .then()
                .statusCode(400);

        // invalid trackable ID
        given()
        .when()
                .post("/api/shipment/allocate?trackableId=999&deviceId=1")
        .then()
                .statusCode(404);

        // invalid device ID
        given()
        .when()
                .post("/api/shipment/allocate?trackableId=1&deviceId=999")
        .then()
                .statusCode(404);

        // deallocate the device from the trackable
        given()
        .when()
                .post("/api/shipment/deallocate?deviceId=1")
        .then()
                .statusCode(200);

        // check if the device is deallocated from the trackable
        given()
        .when()
                .get("/api/unitloads/1")
        .then()
                .statusCode(200)
                .body("devices.size()", is(devicesLengthBefore));

        // deallocate the same device again should fail
        given()
        .when()
                .post("/api/shipment/deallocate?deviceId=1")
        .then()
                .statusCode(400);

        // invalid device ID for deallocation
        given()
        .when()
                .post("/api/shipment/deallocate?deviceId=999")
        .then()
                .statusCode(404);

    }

}
