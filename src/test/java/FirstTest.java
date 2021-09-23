import config.TestConfig;
import org.testng.annotations.Test;

import static Constants.Constants.Actions.VK_CREATE_ALBOM;
import static Constants.Constants.Actions.VK_GET_FRIENDS;
import static io.restassured.RestAssured.given;

public class FirstTest extends TestConfig {
    @Test
    public void myFirstTest() {

        given().
        when().get(VK_GET_FRIENDS).
        then().log().body().statusCode(200);
    }

}
