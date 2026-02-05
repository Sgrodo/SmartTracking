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


        given()
                .contentType("application/json")
                .body(jsonBody)
                .log().all()
        .when()
                .post("/api/unitload")
        .then()
                .log().all()
                .statusCode(201);

    }

    @Test
    public void invalidRequestTest() {
        String jsonBody = """
                        {
                          "description": "",
                          "startDate": "invalid-date",
                          "constraints": [],
                          "loadItemIds": []
                        }
                        """;

        given()
                .contentType("application/json")
                .body(jsonBody)
                .log().all()
        .when()
                .post("/api/unitload")
        .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    public void invalidLoadItemTest() {
        String jsonBody = """
                        {
                          "description": "Test Unit Load",
                          "startDate": "2024-06-01T10:00:00Z",
                          "constraints": [],
                          "loadItemIds": [9999]
                        }
                        """;

        given()
                .contentType("application/json")
                .body(jsonBody)
                .log().all()
        .when()
                .post("/api/unitload")
        .then()
                .log().all()
                .statusCode(404);
    }

}
