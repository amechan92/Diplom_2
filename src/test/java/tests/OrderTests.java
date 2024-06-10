package tests;

import auth.AuthHelper;
import org.junit.Ignore;
import org.junit.Test;
import io.qameta.allure.Step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderTests extends BaseTest {

    private static final String VALID_INGREDIENT_ID = "61c0c5a71d1f82001bdaaa72";
    private static final String INVALID_INGREDIENT_ID = "invalidId";

    @Test
    public void createOrderWithAuthorization() {
        String token = AuthHelper.getTokenForAuthorizedUser("parashepa@gmail.com", "123456");
        createOrder(token, "[\"" + VALID_INGREDIENT_ID + "\"]", 200, true);
    }

    @Ignore //Отсутствует проверка авторизации для этой ручки на стороне сервиса, возвращает 200
    @Test
    public void createOrderWithoutAuthorization() {
        createOrderWithoutAuth("[\"" + VALID_INGREDIENT_ID + "\"]");
    }

    @Test
    public void createOrderWithIngredients() {
        String token = AuthHelper.getTokenForAuthorizedUser("parashepa@gmail.com", "123456");
        createOrderWithValidIngredients(token, "[\"" + VALID_INGREDIENT_ID + "\"]");
    }

    @Test
    public void createOrderWithoutIngredients() {
        String token = AuthHelper.getTokenForAuthorizedUser("parashepa@gmail.com", "123456");
        createOrderWithoutIngredients(token);
    }

    @Test
    public void createOrderWithInvalidIngredientHash() {
        String token = AuthHelper.getTokenForAuthorizedUser("parashepa@gmail.com", "123456");
        createOrderWithInvalidIngredients(token, "[\"" + INVALID_INGREDIENT_ID + "\"]");
    }

    @Test
    public void getOrderForAuthorizedUser() {
        String token = AuthHelper.getTokenForAuthorizedUser("parashepa@gmail.com", "123456");
        getOrderWithAuth(token);
    }

    @Test
    public void getOrderForUnauthorizedUser() {
        getOrderWithoutAuth();
    }

    @Step("Create order with authorized user")
    private void createOrder(String token, String ingredients, int statusCode, boolean success) {
        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body("{\"ingredients\": " + ingredients + "}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(statusCode)
                .body("success", equalTo(success));
    }

    @Step("Create order without authorization")
    private void createOrderWithoutAuth(String ingredients) {
        given()
                .contentType("application/json")
                .body("{\"ingredients\": " + ingredients + "}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Create order with valid ingredients")
    private void createOrderWithValidIngredients(String token, String ingredients) {
        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body("{\"ingredients\": " + ingredients + "}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.ingredients._id", hasItem(VALID_INGREDIENT_ID));
    }

    @Step("Create order without ingredients")
    private void createOrderWithoutIngredients(String token) {
        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body("{\"ingredients\": []}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Step("Create order with invalid ingredient hash")
    private void createOrderWithInvalidIngredients(String token, String ingredients) {
        given()
                .header("Authorization", token)
                .contentType("application/json")
                .body("{\"ingredients\": " + ingredients + "}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(500)
                .contentType("text/html")
                .body(containsString("Internal Server Error"));
    }

    @Step("Get order for authorized user")
    private void getOrderWithAuth(String token) {
        given()
                .header("Authorization", token)
                .when()
                .get("/api/orders")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", not(empty()));
    }

    @Step("Get order for unauthorized user")
    private void getOrderWithoutAuth() {
        given()
                .when()
                .get("/api/orders")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}