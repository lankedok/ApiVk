import config.TestConfig;
import io.restassured.response.Response;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static Constants.Constants.Actions.*;
import static Constants.Constants.pathToResp.*;
import static com.google.gson.JsonParser.parseString;
import static io.restassured.RestAssured.given;

public class WorkingWithGroupTest extends TestConfig {
    @Test
    public void GroupTest() throws InterruptedException, IOException {
        //add topic
        Map<String, String> additionalParams = createParamsGroup(
                Pair.of("title", "Куда пропадают какашки из лотка"),
                Pair.of("text", "помогите!"));
        Response addTopicResponse = given()
                .params(additionalParams)
                .get(ADD_TOPIC);
        Thread.sleep(2500);
        String topicId = parseString(addTopicResponse.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsString();

        //fix topic
        additionalParams = createParamsGroup(
                Pair.of("topic_id", topicId));
        Response fixTopicResponse = given()
                .params(additionalParams)
                .get(FIX_TOPIC);
        Thread.sleep(2500);
        File file = new File(RESP_FIX_TOPIC);
        String expectedResponse = new String(Files.readAllBytes(file.toPath()));
        String expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();
        String actualJsonResponse = fixTopicResponse.asString();
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);

        //create first comment
        additionalParams = createParamsGroup(
                Pair.of("topic_id", topicId),
                Pair.of("message", "иногда пропадают сразу"));
        Response fistCommentResponse = given()
                .params(additionalParams)
                .get(CREATE_COMMENT_TOPIC);
        Thread.sleep(2500);
        String firstCommentId = parseString(fistCommentResponse.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsString();
        //create second comment
        additionalParams = createParamsGroup(
                Pair.of("topic_id", topicId),
                Pair.of("message", "а иногда лежат часами"));
        given()
                .params(additionalParams)
                .get(CREATE_COMMENT_TOPIC)
                .then()
                .statusCode(200);
        Thread.sleep(2500);
        //create third comment
        additionalParams = createParamsGroup(
                Pair.of("topic_id", topicId),
                Pair.of("message", "что делать? куда писать?"));
        Response thirdCommentResponse = given()
                .params(additionalParams)
                .get(CREATE_COMMENT_TOPIC);
        Thread.sleep(2500);
        String thirdCommentId = parseString(thirdCommentResponse.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsString();
        //edit third comment
        additionalParams = createParamsGroup(
                Pair.of("topic_id", topicId),
                Pair.of("comment_id", thirdCommentId),
                Pair.of("message", "что делать? куда писАть?"));
        Response editCommentResponse = given()
                .params(additionalParams)
                .get(EDIT_COMMENT_TOPIC);
        Thread.sleep(2500);
        file = new File(RESP_EDIT_COMMENT_TOPIC);
        expectedResponse = new String(Files.readAllBytes(file.toPath()));
        expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();
        actualJsonResponse = editCommentResponse.asString();
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);
        //delete first comment
        additionalParams = createParamsGroup(
                Pair.of("topic_id", topicId),
                Pair.of("comment_id", firstCommentId));
        Response deleteCommentResponse = given()
                .params(additionalParams)
                .get(DELETE_COMMENT_TOPIC);
        Thread.sleep(2500);
        file = new File(RESP_DELETE_COMMENT_TOPIC);
        expectedResponse = new String(Files.readAllBytes(file.toPath()));
        expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();
        actualJsonResponse = deleteCommentResponse.asString();
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);
        //return the application to its original state
        additionalParams = createParamsGroup(
                Pair.of("topic_id", topicId));
        Response deleteTopicResponse = given()
                .params(additionalParams)
                .get(DELETE_TOPIC);
        file = new File(RESP_DELETE_TOPIC);
        expectedResponse = new String(Files.readAllBytes(file.toPath()));
        expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();
        actualJsonResponse = deleteTopicResponse.asString();
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);
    }
}
