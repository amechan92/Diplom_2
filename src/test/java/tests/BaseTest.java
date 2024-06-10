package tests;

import io.restassured.RestAssured;
import org.junit.BeforeClass;
import config.TestConfig;

public class BaseTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = TestConfig.BASE_URI;
    }
}