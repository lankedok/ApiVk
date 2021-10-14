import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static Constants.Constants.Actions.*;
import static com.google.gson.JsonParser.parseString;

public class WorkingWithGroupTest extends TestConfig {

    @Test
    public void GroupTest() throws InterruptedException, IOException {
        Response addTopicResponse = createTopic();
        waitSomeTime();
        String topicId = parseString(addTopicResponse.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsString();

        Response fixTopicResponse = requestSpecificationGroup()
                .param("topic_id", topicId)
                .get(FIX_TOPIC)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        Assert.assertEquals(1, getIntResponseCode(fixTopicResponse));

        Response fistCommentResponse = requestSpecificationGroup()
                .param("topic_id", topicId)
                .param("message", "иногда пропадают сразу")
                .get(CREATE_COMMENT_TOPIC)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        String firstCommentId = parseString(fistCommentResponse.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsString();
        requestSpecificationGroup()
                .param("topic_id", topicId)
                .param("message", "а иногда лежат часами")
                .get(CREATE_COMMENT_TOPIC)
                .then()
                .spec(responseSpecification);
        waitSomeTime();
        Response thirdCommentResponse = requestSpecificationGroup()
                .param("topic_id", topicId)
                .param("message", "что делать? куда писать?")
                .get(CREATE_COMMENT_TOPIC)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        String thirdCommentId = parseString(thirdCommentResponse.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsString();
        Response editCommentResponse = requestSpecificationGroup()
                .param("topic_id", topicId)
                .param("comment_id", thirdCommentId)
                .param("message", "что делать? куда писАть?")
                .get(EDIT_COMMENT_TOPIC)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        Assert.assertEquals(1, getIntResponseCode(editCommentResponse));
        Response deleteCommentResponse = requestSpecificationGroup()
                .param("topic_id", topicId)
                .param("comment_id", firstCommentId)
                .get(DELETE_COMMENT_TOPIC)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        Assert.assertEquals(1, getIntResponseCode(deleteCommentResponse));
        //return the application to its original state
        Response deleteTopicResponse = requestSpecificationGroup()
                .param("topic_id", topicId)
                .get(DELETE_TOPIC)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        Assert.assertEquals(1, getIntResponseCode(deleteTopicResponse));
    }

    @Step(value = "Create topic")
    public Response createTopic() {
        return requestSpecificationGroup()
                .param("title", "Куда пропадают какашки из лотка")
                .param("text", "помогите!")
                .get(ADD_TOPIC)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();

    }
}
