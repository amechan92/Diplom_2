package tests;

import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateUserTests extends BaseTest {

    private final Map<String, String> createdUsers = new HashMap<>();

    @Test
    public void testCreateUniqueUser() {
        String uniqueEmail = "testuser_" + UUID.randomUUID() + "@example.com";
        String password = "password";
        String token = registerUserUnique(uniqueEmail, password, "Test User");
        createdUsers.put(uniqueEmail, token);
    }

    @Test
    public void testCreateExistingUser() {
        String existingEmail = "testuser@example.com";
        registerUserExisting(existingEmail, "password", "Test User");
    }

    @Test
    public void testCreateUserWithMissingField() {
        registerUserWithMissingField();
    }

    @After
    public void cleanup() {
        createdUsers.forEach(this::deleteUser);
    }

    @Step("Register a user with a unique email")
    private String registerUserUnique(String email, String password, String name) {
        String token = given()
                .contentType("application/json")
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\"}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract().path("accessToken");

        return token;
    }

    @Step("Try to register a user with an existing email")
    private void registerUserExisting(String email, String password, String name) {
        given()
                .contentType("application/json")
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\"}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Step("Try to register a user with a missing field")
    private void registerUserWithMissingField() {
        given()
                .contentType("application/json")
                .body("{\"password\": \"password\", \"name\": \"Test User\"}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Delete a user by email and token")
    private void deleteUser(String email, String token) {
        given()
                .contentType("application/json")
                .header("Authorization", token)
                .when()
                .delete("https://stellarburgers.nomoreparties.site/api/auth/user")
                .then()
                .statusCode(202);
    }
}