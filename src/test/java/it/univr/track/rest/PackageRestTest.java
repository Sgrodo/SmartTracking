package it.univr.track.rest;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PackageRestTest extends BaseRestTest {

    @Test
    public void createAndDeletePackageTest() {
        JsonPath jsonPath = given()
                .when()
                    .get("/api/unitloads")
                .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
        Long[] unitLoadIds = jsonPath
                    .getObject("id", Long[].class);

        assertTrue(unitLoadIds.length >= 2, "Expected at least 2 unit loads to be available for testing");

        String groupCode = "PKG-001";
        String description = "Demo Package";
        String jsonBody = """
                {
                  "groupCode": "%s",
                  "description": "%s",
                  "unitLoadIds": [%d, %d]
                }
                """.formatted(groupCode, description, unitLoadIds[0], unitLoadIds[1]);

        Long createdPackageId = given().body(jsonBody)
                .contentType("application/json")
                .log().all()
                .when()
                .post("/api/packages")
                .then()
                .log().all()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        // Verify the package was created
        given()
                .when()
                .get("/api/packages/{trackableId}", createdPackageId)
                .then()
                .statusCode(200);

        // Delete the package
        given()
                .log().all()
                .when()
                .delete("/api/packages/{trackableId}", createdPackageId)
                .then()
                .log().all()
                .statusCode(200);

        // Verify the package has been deleted
        given()
                .when()
                .get("/api/packages/{trackableId}", createdPackageId)
                .then()
                .statusCode(404);

    }

    @Test
    public void getNonExistingPackageTest() {
        Long nonExistingId = 999L;
        given()
                .when()
                .get("/api/packages/{trackableId}", nonExistingId)
                .then()
                .statusCode(404);
    }

    @Test
    public void invalidRequestTest() {
        // Empty group code
        String blackGroupCodeJson = """
                {
                  "groupCode": "",
                  "description": "Invalid Package",
                  "unitLoadIds": [1]
                }
                """;

        given().body(blackGroupCodeJson)
                .contentType("application/json")
                .log().all()
                .when()
                .post("/api/packages")
                .then()
                .log().all()
                .statusCode(400);

        // Missing unitLoadIds
        String missingUnitLoadsJson = """
                {
                  "groupCode": "PKG-002",
                  "description": "Invalid Package"
                }
                """;

        given().body(missingUnitLoadsJson)
                .contentType("application/json")
                .log().all()
                .when()
                .post("/api/packages")
                .then()
                .log().all()
                .statusCode(400);

        // Empty unitLoadIds
        String emptyUnitLoadsJson = """
                {
                    "groupCode": "PKG-003",
                    "description": "Invalid Package",
                    "unitLoadIds": []
                }
                """;

        given().body(emptyUnitLoadsJson)
                .contentType("application/json")
                .log().all()
                .when()
                .post("/api/packages")
                .then()
                .log().all()
                .statusCode(400);

        // Invalid unitLoadIds (non-numeric)
        String invalidUnitLoadsJson = """
                {
                    "groupCode": "PKG-004",
                    "description": "Invalid Package",
                    "unitLoadIds": ["invalid"]
                }
                """;

        given().body(invalidUnitLoadsJson)
                .contentType("application/json")
                .log().all()
                .when()
                .post("/api/packages")
                .then()
                .log().all()
                .statusCode(400);

        // Invalid unitLoadIds (non-existing IDs)
        String nonExistingUnitLoadsJson = """
                {
                    "groupCode": "PKG-005",
                    "description": "Invalid Package",
                    "unitLoadIds": [999, 1000]
                }
                """;

        given().body(nonExistingUnitLoadsJson)
                .contentType("application/json")
                .log().all()
                .when()
                .post("/api/packages")
                .then()
                .log().all()
                .statusCode(400);


        // Invalid unitLoadIds (null value)
        String nullUnitLoadsJson = """
                {
                    "groupCode": "PKG-006",
                    "description": "Invalid Package",
                    "unitLoadIds": null
                }
                """;

        given().body(nullUnitLoadsJson)
                .contentType("application/json")
                .log().all()
                .when()
                .post("/api/packages")
                .then()
                .log().all()
                .statusCode(400);
    }

}
