package tests;

import io.qameta.allure.Step;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserLoginTests extends BaseTest {

    @Test
    public void testLoginWithExistingUser() {
        loginWithExistingUser("parashepa@gmail.com", "123456");
    }

    @Test
    public void testLoginWithWrongCredentials() {
        loginWithWrongCredentials("wrong_email@example.com", "wrong_password");
    }

    @Step("Login with existing user credentials")
    private void loginWithExistingUser(String email, String password) {
        given()
                .contentType("application/json")
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Step("Attempt to login with wrong credentials")
    private void loginWithWrongCredentials(String email, String password) {
        given()
                .contentType("application/json")
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}