import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static Constants.Constants.Actions.*;
import static com.google.gson.JsonParser.parseString;

public class WorkingWithGroupTest extends TestConfig {

    @Description("Group Test test")
    @Test
    public void GroupTest() throws InterruptedException, IOException {
        Response addTopicResponse = createTopic();
        waitSomeTimeOn();

        String topicId = getTopicId(addTopicResponse);
        Response fixTopicResponse = fixTopicResponse(topicId);
        waitSomeTimeOn();

        checkResponse(1, fixTopicResponse);
        Response fistCommentResponse = createComment(topicId, "иногда пропадают сразу");
        waitSomeTimeOn();

        String firstCommentId = getCommentId(fistCommentResponse);
        createComment(topicId, "а иногда лежат часами");
        waitSomeTimeOn();

        Response thirdCommentResponse = createComment(topicId, "что делать? куда писать?");
        waitSomeTimeOn();

        String thirdCommentId = getCommentId(thirdCommentResponse);
        Response editCommentResponse = editComment(topicId, thirdCommentId);
        waitSomeTimeOn();
        checkResponse(1, editCommentResponse);


        Response deleteCommentResponse = deleteComment(topicId, firstCommentId);
        waitSomeTimeOn();
        checkResponse(1, deleteCommentResponse);


        Response deleteTopicResponse = deleteTopic(topicId);
        checkResponse(1, deleteTopicResponse);

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

    @Step(value = "Get topic id")
    public String getTopicId(Response resp) {
        return parseString(resp.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsString();
    }

    @Step(value = "fix Topic Response {0}")
    public Response fixTopicResponse(String topicId) {
        return requestSpecificationGroup()
                .param("topic_id", topicId)
                .get(FIX_TOPIC)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "create comment {0} {1}")
    public Response createComment(String topicId, String message) {
        return requestSpecificationGroup()
                .param("topic_id", topicId)
                .param("message", message)
                .get(CREATE_COMMENT_TOPIC)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "get comment id {0}")
    public String getCommentId(Response response) {
        return parseString(response.asString())
                .getAsJsonObject()
                .getAsJsonPrimitive("response")
                .getAsString();
    }

    @Step(value = "edit comment {0} {1}")
    public Response editComment(String topicId, String commentId) {
        return requestSpecificationGroup()
                .param("topic_id", topicId)
                .param("comment_id", commentId)
                .param("message", "что делать? куда писАть?")
                .get(EDIT_COMMENT_TOPIC)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "delete comment {0} {1}")
    public Response deleteComment(String topicId, String commentId) {
        return requestSpecificationGroup()
                .param("topic_id", topicId)
                .param("comment_id", commentId)
                .get(DELETE_COMMENT_TOPIC)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "delete topic {0}")
    public Response deleteTopic(String topicId) {
        return requestSpecificationGroup()
                .param("topic_id", topicId)
                .get(DELETE_TOPIC)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "check response {0} {1}")
    public void checkResponse(int value, Response response) {
        Assert.assertEquals(value, getIntResponseCode(response));
    }

    @Step(value = "wait")
    protected void waitSomeTimeOn() throws InterruptedException {
        Thread.sleep(2500);
    }
}
