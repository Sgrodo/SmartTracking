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

        given()
        .when()
                .get("/api/unitloads/{trackableId}", createdUnitLoadId)
        .then()
                .statusCode(200);
    }

    @Test
    public void deleteUnitLoadTest() {
        Long unitLoadId = 1L;

        // Check that the unit load exists
        given()
        .when()
                .get("/api/unitloads/{trackableId}", unitLoadId)
        .then()
                .statusCode(200);

        // Delete the unit load
        given()
                .log().all()
        .when()
                .delete("/api/unitloads/{trackableId}", unitLoadId)
        .then()
                .log().all()
                .statusCode(200);

        // Verify that the unit load has been deleted
        given()
        .when()
                .get("/api/unitloads/{trackableId}", unitLoadId)
        .then()
                .statusCode(404);
    }

}
