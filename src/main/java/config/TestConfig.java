package config;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import static Constants.Constants.RunVerible.*;
import static com.google.gson.JsonParser.parseString;
import static io.restassured.RestAssured.given;

public class TestConfig {

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = server;

    }

    protected static RequestSpecification requestSpecification() {
        return given()
                .param("v", version)
                .param("access_token", token)
                .param("user_id", user_id);
    }

    protected static RequestSpecification requestSpecificationGroup() {
        return given()
                .param("v", version)
                .param("access_token", token)
                .param("user_id", user_id)
                .param("group_id", group_id);
    }

    protected ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .build();

    protected void waitSomeTime() throws InterruptedException {
        Thread.sleep(2500);
    }

    protected int getIntResponseCode(Response response) {
        return parseString(response.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsInt();
    }
}
