package it.univr.track.rest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class PackageRestTest extends BaseRestTest{

    @Test
    public void createAndDeletePackageTest() {
        String groupCode = "PKG-001";
        String description = "Demo Package";
        Long[] unitLoadIds = new Long[]{ 1L, 2L };
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

}
