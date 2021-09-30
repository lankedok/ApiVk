import com.google.gson.JsonObject;
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
import static Constants.Constants.pathToPhoto.PATH_TO_PHOTO;
import static Constants.Constants.pathToResp.RESP_EDIT_INFO_PROFILE;
import static Constants.Constants.pathToResp.RESP_GET_INFO_PROFILE;
import static com.google.gson.JsonParser.parseString;
import static io.restassured.RestAssured.given;

public class WorkingWithProfileTest extends TestConfig {
    @Test
    public void getProfile() throws IOException {
        //given
        File file = new File(RESP_GET_INFO_PROFILE);
        String expectedResponse = new String(Files.readAllBytes(file.toPath()));
        String expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();

        //act
        Response response = given()
                .params(createParams())
                .get(VK_GET_PROFILE_INFO);

        //assertions
        int statusCode = response.getStatusCode();
        String actualJsonResponse = response.asString();

        print(expectedJsonResponse, "expectedJsonResponse");
        print(actualJsonResponse, "actualJsonResponse");

        Assert.assertEquals(200, statusCode);
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);
    }

    @Test
    public void editProfile() throws IOException, InterruptedException {
        //given
        File file = new File(RESP_EDIT_INFO_PROFILE);
        String expectedResponse = new String(Files.readAllBytes(file.toPath()));
        String expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();

        //act
        Map<String, String> additionalParams = createParams(
                Pair.of("home_town", "NY"),
                Pair.of("status", "test"));
        Response response = given()
                .params(additionalParams)
                .post(VK_SAVE_PROFILE_INFO);

        //assertions
        int statusCode = response.getStatusCode();
        String actualJsonResponse = response.asString();

        print(expectedJsonResponse, "expectedJsonResponse");
        print(actualJsonResponse, "actualJsonResponse");

        Assert.assertEquals(200, statusCode);
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);

        Thread.sleep(2500);
        additionalParams = createParams(
                Pair.of("home_town", ""),
                Pair.of("status", ""));
        given()
                .params(additionalParams)
                .post(VK_SAVE_PROFILE_INFO)
                .then()
                .statusCode(200);
    }

    @Test
    public void savePhotoProfile() throws InterruptedException {
        //act
        Response uploadServerUrl = given()
                .params(createParams())
                .get(OWNER_UPLOAD_PHOTO);
        Thread.sleep(2500);
        String uploadUrl = parseString(uploadServerUrl.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("upload_url")
                .getAsString();

        File file = new File(PATH_TO_PHOTO);
        Response uploadedPhotoInfo = given()
                .multiPart("photo", file)
                .post(uploadUrl);
        Thread.sleep(2500);
        JsonObject uploadedResponse = parseString(uploadedPhotoInfo.asString())
                .getAsJsonObject();

        String server = uploadedResponse.get("server").getAsString();
        String hash = uploadedResponse.get("hash").getAsString();
        String photo = uploadedResponse.get("photo").getAsString();

        Map<String, String> additionalParams = createParams(
                Pair.of("server", server),
                Pair.of("hash", hash),
                Pair.of("photo", photo));

        Response addedPhotoResponse = given()
                .params(additionalParams)
                .post(VK_SAVE_OWNER_PHOTO);
        Thread.sleep(2500);

        //assertions
        int statusCode = addedPhotoResponse.getStatusCode();
        int saved = parseString(addedPhotoResponse.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("saved")
                .getAsInt();

        Assert.assertEquals(200, statusCode);
        Assert.assertEquals(1, saved);

        //return the application to its original state
        additionalParams = createParams(
                Pair.of("fields", "photo_id"));
        Response userInfoResponse = given()
                .params(additionalParams)
                .get(USER_GET);

        Thread.sleep(2500);
        JsonObject userInfoJson = parseString(userInfoResponse.asString()).getAsJsonObject();
        String photoId = userInfoJson
                .getAsJsonArray("response")
                .get(0)
                .getAsJsonObject()
                .get("photo_id")
                .getAsString()
                .split("_")[1];
        additionalParams = createParams(
                Pair.of("photo_id", photoId));
        given()
                .params(additionalParams)
                .get(PHOTO_DELETE)
                .then().statusCode(200);
    }
}
