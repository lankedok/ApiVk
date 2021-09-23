package config;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import static Constants.Constants.RunVerible.path;
import static Constants.Constants.RunVerible.server;

public class TestConfig {


    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = server;
        RestAssured.basePath = path;
    }
}
