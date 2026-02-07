package it.univr.track.rest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class UnitLoadRestTest extends BaseRestTest {

    @Test
    public void createUnitLoadTest() {
        String DESCRIPTION = "Demo Unit Load";
        String START_DATE = "2024-06-01T10:00:00Z";
        Long[] LOAD_ITEM_IDS = new Long[]{ this.loadItems[0].getId() };

        String jsonBody = """
                        {
                          "description": "%s",
                          "startDate": "%s",
                          "constraints": [
                            { "type": "HUMIDITY", "percent": 50.0 },
                            { "type": "TEMPERATURE", "value": 4.0, "unit": "CELSIUS" }
                          ],
                          "loadItemIds": [%d]
                        }
                        """.formatted(DESCRIPTION, START_DATE, LOAD_ITEM_IDS[0]);


        Long createdUnitLoadId = given()
                .contentType("application/json")
                .body(jsonBody)
                .log().all()
        .when()
                .post("/api/unitloads")
        .then()
                .log().all()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        // Verify the unit load was created
        given()
        .when()
                .get("/api/unitloads/{trackableId}", createdUnitLoadId)
        .then()
                .statusCode(200);


        // Delete the unit load
        given()
                .log().all()
                .when()
                .delete("/api/unitloads/{trackableId}", createdUnitLoadId)
                .then()
                .log().all()
                .statusCode(200);

        // Verify that the unit load has been deleted
        given()
                .when()
                .get("/api/unitloads/{trackableId}", createdUnitLoadId)
                .then()
                .statusCode(404);
    }

    @Test
    public void notFoundTest() {
        // Attempt to get a non-existent unit load
        given()
                .when()
                .get("/api/unitloads/{trackableId}", 9999L)
                .then()
                .statusCode(404);

        // Attempt to delete a non-existent unit load
        given()
                .when()
                .delete("/api/unitloads/{trackableId}", 9999L)
                .then()
                .statusCode(404);
    }

    @Test
    public void invalidRequestTest() {
        // Missing loadItemIds
        String invalidJson = """
                {
                    "description": "Invalid Unit Load",
                    "startDate": "2024-06-01T10:00:00Z",
                    "constraints": [
                        { "type": "HUMIDITY", "percent": 50.0 }
                    ]
                }
                """;

        testBadRequest(invalidJson);

        // Empty loadItemIds
        String emptyLoadItemsJson = """
                {
                    "description": "Invalid Unit Load",
                    "startDate": "2024-06-01T10:00:00Z",
                    "constraints": [
                        { "type": "HUMIDITY", "percent": 50.0 }
                    ],
                    "loadItemIds": []
                }
                """;

        testBadRequest(emptyLoadItemsJson);

        // Null loadItemId
        String nullLoadItemIdJson = """
                {
                    "description": "Invalid Unit Load",
                    "startDate": "2024-06-01T10:00:00Z
                    "constraints": [
                        { "type": "HUMIDITY", "percent": 50.0 }
                    ],
                    "loadItemIds": [null]
                }
                """;

        testBadRequest(nullLoadItemIdJson);
    }

    @Test
    public void invalidConstraintsTest() {
        // Invalid constraint type
        String invalidConstraintJson = """
                {
                    "description": "Invalid Unit Load",
                    "startDate": "2024-06-01T10:00:00Z",
                    "constraints": [
                        { "type": "INVALID_TYPE", "percent": 50.0 }
                    ],
                    "loadItemIds": [1]
                }
                """;

        testBadRequest(invalidConstraintJson);

        // Missing required fields for TEMPERATURE constraint
        String missingFieldsJson = """
                {
                    "description": "Invalid Unit Load",
                    "startDate": "2024-06-01T10:00:00Z",
                    "constraints": [
                        { "type": "TEMPERATURE" }
                    ],
                    "loadItemIds": [1]
                }
                """;

        testBadRequest(missingFieldsJson);

        // Negative percent value for HUMIDITY constraint
        String negativePercentJson = """
                {
                    "description": "Invalid Unit Load",
                    "startDate": "2024-06-01T10:00:00Z",
                    "constraints": [
                        { "type": "HUMIDITY", "percent": -10.0 }
                    ],
                    "loadItemIds": [1]
                }
                """;

        testBadRequest(negativePercentJson);

        // Invalid constraint parameters (null value)
        String nullValueJson = """
                {
                    "description": "Invalid Unit Load",
                    "startDate": "2024-06-01T10:00:00Z",
                    "constraints": [
                        { "type": "TEMPERATURE", "value": null, "unit": "CELSIUS" }
                    ],
                    "loadItemIds": [1]
                }
                """;

        testBadRequest(nullValueJson);

        String nullUnitJson = """
                {
                    "description": "Invalid Unit Load",
                    "startDate": "2024-06-01T10:00:00Z",
                    "constraints": [
                        { "type": "TEMPERATURE", "value": 4.0, "unit": null }
                    ],
                    "loadItemIds": [1]
                }
                """;

        testBadRequest(nullUnitJson);
    }

    private static void testBadRequest(String jsonBody) {
        given()
                .contentType("application/json")
                .body(jsonBody)
                .log().all()
        .when()
                .post("/api/unitloads")
        .then()
                .log().all()
                .statusCode(400);
    }


}
