package tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest extends BaseTest {

    private String token;
    private String email;
    private String password = "password123";
    private String name = "Test User";

    @Before
    public void setUp() {
        createUser();
        loginUser();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    public void testUpdateUserNameWithAuthorization() {
        updateUserNameWithAuthorization("Updated Name");
    }

    @Test
    public void testUpdateUserEmailWithAuthorization() {
        String newEmail = "updated" + System.currentTimeMillis() + "@example.com";
        updateUserEmailWithAuthorization(newEmail);
    }

    @Test
    public void testUpdateUserNameWithoutAuthorization() {
        updateUserNameWithoutAuthorization("Unauthorized Name");
    }

    @Test
    public void testUpdateUserEmailWithoutAuthorization() {
        updateUserEmailWithoutAuthorization("unauthorized_email@example.com");
    }

    @Step("Create user")
    private void createUser() {
        email = "testUser" + System.currentTimeMillis() + "@example.com";
        given()
                .contentType("application/json")
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\"}")
                .when()
                .post("/api/auth/register");
    }

    @Step("Login user")
    private void loginUser() {
        Response response = given()
                .contentType("application/json")
                .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/auth/login");

        token = response.jsonPath().getString("accessToken");
    }

    @Step("Delete user")
    private void deleteUser() {
        given()
                .header("Authorization", token)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202);
    }

    @Step("Update user name with authorization")
    private void updateUserNameWithAuthorization(String updatedName) {
        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body("{\"name\": \"" + updatedName + "\"}")
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(updatedName));
    }

    @Step("Update user email with authorization")
    private void updateUserEmailWithAuthorization(String newEmail) {
        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body("{\"email\": \"" + newEmail + "\"}")
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail));
    }

    @Step("Update user name without authorization")
    private void updateUserNameWithoutAuthorization(String name) {
        given()
                .contentType("application/json")
                .body("{\"name\": \"" + name + "\"}")
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Update user email without authorization")
    private void updateUserEmailWithoutAuthorization(String email) {
        given()
                .contentType("application/json")
                .body("{\"email\": \"" + email + "\"}")
                .when()
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}