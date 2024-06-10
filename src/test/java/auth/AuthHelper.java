package auth;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthHelper {

    public static String getTokenForAuthorizedUser(String email, String password) {
        Response response = given()
                .contentType("application/json")
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .extract()
                .response();

        return response.path("accessToken");
    }
}