import com.google.gson.JsonObject;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static Constants.Constants.Actions.*;
import static Constants.Constants.pathToPhoto.PATH_TO_PHOTO;
import static com.google.gson.JsonParser.parseString;
import static io.restassured.RestAssured.given;

public class WorkingWithProfileTest extends TestConfig {
    @Test
    public void editProfile() throws IOException, InterruptedException {


        getProfileInfo();
        waitSomeTimeOn();
        Response response = changeProfileInfo("NY", "test");

        int changed = getIntResponse(response);
        checkResponse(1, changed);
        waitSomeTimeOn();
        changeProfileInfo("", "");
    }

    @Test
    public void savePhotoProfile() throws InterruptedException {
        Response uploadServerUrl = getUploadServerUrl();
        waitSomeTimeOn();
        String uploadUrl = saveUrl(uploadServerUrl);

        File file = createFile();
        Response uploadedPhotoInfo = uploadedPhoto(file, uploadUrl);
        waitSomeTimeOn();
        JsonObject uploadedResponse = getJO(uploadedPhotoInfo);

        String server = getValue(uploadedResponse, "server");
        String hash = getValue(uploadedResponse, "hash");
        String photo = getValue(uploadedResponse, "photo");

        Response addedPhotoResponse = addedPhoto(server, hash, photo);
        waitSomeTimeOn();
        int saved = getIntResponse(addedPhotoResponse);
        checkResponse(1, saved);

        Response userInfoResponse = getUserInfo();
        waitSomeTimeOn();
        JsonObject userInfoJson = getJO(userInfoResponse);

        String photoId = getPhotoId(userInfoJson);
        deletePhoto(photoId);
    }

    @Step(value = "get profile info")
    public Response getProfileInfo() {
        return requestSpecification()
                .get(VK_GET_PROFILE_INFO)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "change profile info {0} {1}")
    public Response changeProfileInfo(String homeTown, String status) {
        return requestSpecification()
                .param("home_town", homeTown)
                .param("status", status)
                .post(VK_SAVE_PROFILE_INFO)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "get int response {0}")
    public int getIntResponse(Response response) {
        return parseString(response.asString()).getAsJsonObject()
                .getAsJsonObject("response")
                .get("changed")
                .getAsInt();
    }

    @Step(value = "get upload server url")
    public Response getUploadServerUrl() {
        return requestSpecification()
                .get(OWNER_UPLOAD_PHOTO)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "save url {0}")
    public String saveUrl(Response response) {
        return parseString(response.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("upload_url")
                .getAsString();
    }

    @Step(value = "wait")
    protected void waitSomeTimeOn() throws InterruptedException {
        Thread.sleep(2500);
    }

    @Step(value = "uploaded photo to server {0} {1}")
    public Response uploadedPhoto(File file, String uploadUrl) {
        return given()
                .multiPart("photo", file)
                .post(uploadUrl);
    }

    @Step(value = "get Json Object {0}")
    public JsonObject getJO(Response response) {
        return parseString(response.asString())
                .getAsJsonObject();
    }

    @Step(value = "get value {0} {1}")
    public String getValue(JsonObject jsonObject, String key) {
        return jsonObject.get(key).getAsString();
    }

    @Step(value = "added photo {0} {1} {2}")
    public Response addedPhoto(String server, String hash, String photo) {
        return requestSpecification()
                .param("server", server)
                .param("hash", hash)
                .param("photo", photo)
                .post(VK_SAVE_OWNER_PHOTO)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "get user info")
    public Response getUserInfo() {
        return requestSpecification()
                .param("fields", "photo_id")
                .get(USER_GET)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "get photo id {0}")
    public String getPhotoId(JsonObject jsonObject) {
        return jsonObject
                .getAsJsonArray("response")
                .get(0)
                .getAsJsonObject()
                .get("photo_id")
                .getAsString()
                .split("_")[1];
    }

    @Step(value = "delete photo")
    public Response deletePhoto(String photoId) {
        return requestSpecification()
                .param("photo_id", photoId)
                .get(PHOTO_DELETE)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "check response {0} {1}")
    public void checkResponse(int value, int valueResponse) {
        Assert.assertEquals(value, valueResponse);
    }

    @Step(value = "create file")
    public File createFile() {
        return new File(PATH_TO_PHOTO);
    }
}
