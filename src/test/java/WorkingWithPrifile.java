import config.TestConfig;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static Constants.Constants.Actions.*;
import static Constants.Constants.ValuesForPhoto.token_user_photo;
import static Constants.Constants.RunVerible.*;
import static io.restassured.RestAssured.given;

public class WorkingWithPrifile extends TestConfig {
    @Test
    public void changePrifile() throws InterruptedException {
        Response response = given().queryParam("user_id",user_id ).queryParam("access_token", token_user_photo).
                queryParam("v", version).log().all().
                when().get(VK_GET_PROFILE_INFO).
                then().extract().response();
        String responseProfile = response.getBody().asString();
        System.out.println(responseProfile);
        Thread.sleep(4000);
        given().queryParam("user_id",user_id ).queryParam("access_token", token_user_photo).
                queryParam("v", version).queryParam("home_town", "Ижевск").queryParam("status", "test").
        when().post(VK_SAVE_PROFILE_INFO).
        then().log().all();
    }
}
