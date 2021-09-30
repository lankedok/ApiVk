import config.TestConfig;
import io.restassured.response.Response;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.util.Map;

import static Constants.Constants.Actions.CREATE_CHAT;
import static io.restassured.RestAssured.given;

public class WorkingWithMessages extends TestConfig {
    @Test
    public void Massage() {
        //create conversation
        Map<String, String> additionalParams = createParams(
                Pair.of("user_ids", "565020460,675948635"),
                Pair.of("title", "Куда пропадают какашки из лотка"));
        Response addTopicResponse = given()
                .params(additionalParams)
                .get(CREATE_CHAT);
        System.out.println(addTopicResponse.asString());
    }
}
