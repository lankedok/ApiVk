import com.google.gson.JsonObject;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static Constants.Constants.Actions.*;
import static Constants.Constants.RunVerible.user_id;
import static Constants.Constants.pathToPhoto.PATH_TO_PHOTO;
import static com.google.gson.JsonParser.parseString;
import static io.restassured.RestAssured.given;

public class WorkingWithPhotosTest extends TestConfig {
    @Test
    public void WorkingWithPhoto() throws IOException, InterruptedException {
        Response createPhotoAlbum = createPhotoAlbum("Test Album", "3");
        waitSomeTimeOn();

        String albumId = getAlbumId(createPhotoAlbum);
        Response uploadServerUrl = getServerUrl(albumId);
        waitSomeTimeOn();
        String uploadUrl = saveUploadURL(uploadServerUrl);

        File file = createFile();
        Response uploadedPhotoInfo = uploadedPhoto(file, uploadUrl);
        waitSomeTimeOn();
        JsonObject uploadedResponse = getJO(uploadedPhotoInfo);

        String server = getValue(uploadedResponse, "server");
        String hash = getValue(uploadedResponse, "hash");
        String photosList = getValue(uploadedResponse, "photos_list");
        Response addedPhotoResponse = addedPhoto(server, hash, albumId, photosList);

        waitSomeTimeOn();
        String photoId = getPhotoId(addedPhotoResponse);
        Response makeCoverResponse = makeCover(albumId, photoId);
        waitSomeTimeOn();

        checkResponse(1, makeCoverResponse);
        createComment(photoId, "test comment");
        waitSomeTimeOn();
        makeTag(photoId);
        waitSomeTimeOn();

        createPhotoAlbum = createPhotoAlbum("Public Test Album", "0");
        waitSomeTimeOn();

        String publicAlbumId = getAlbumId(createPhotoAlbum);
        movePhoto(photoId, publicAlbumId);
        waitSomeTimeOn();

        Response deleteAlbumResponse = deleteAlbum(albumId);
        waitSomeTimeOn();
        checkResponse(1, deleteAlbumResponse);

        deleteAlbumResponse = deleteAlbum(publicAlbumId);
        checkResponse(1, deleteAlbumResponse);
    }

    @Step(value = "create photo album {0}")
    public Response createPhotoAlbum(String title, String privacy) {
        return requestSpecification()
                .param("title", title)
                .param("privacy_view", privacy)
                .get(CREATE_ALBUM)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "get album id {0}")
    public String getAlbumId(Response response) {
        return parseString(response.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("id")
                .getAsString();
    }

    @Step(value = "get server url {0}")
    public Response getServerUrl(String albumId) {
        return requestSpecification()
                .param("album_id", albumId)
                .get(UPLOAD_PHOTO)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "save upload url {0}")
    public String saveUploadURL(Response response) {
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

    @Step(value = "create file")
    public File createFile() {
        return new File(PATH_TO_PHOTO);
    }

    @Step(value = "uploaded photo to server {0} {1}")
    public Response uploadedPhoto(File file, String uploadUrl) {
        return given()
                .multiPart("photo", file)
                .post(uploadUrl)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
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

    @Step(value = "added photo {0} {1} {2} {3}")
    public Response addedPhoto(String server, String hash, String albumId, String photosList) {
        return requestSpecification()
                .param("server", server)
                .param("hash", hash)
                .param("album_id", albumId)
                .param("photos_list", photosList)
                .post(SAVE_PHOTO)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "get photo id {0}")
    public String getPhotoId(Response response) {
        return parseString(response.asString())
                .getAsJsonObject()
                .getAsJsonArray("response")
                .get(0)
                .getAsJsonObject()
                .get("id")
                .getAsString();
    }

    @Step(value = "make cover {0} {1}")
    public Response makeCover(String albumId, String photoId) {
        return requestSpecification()
                .param("album_id", albumId)
                .param("photo_id", photoId)
                .get(MAKE_COVER)
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

    @Step(value = "create comment {0} {1}")
    public Response createComment(String photoId, String message) {
        return requestSpecification()
                .param("photo_id", photoId)
                .param("message", message)
                .get(CREATE_COMMENT)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "make tag {0}")
    public Response makeTag(String photoId) {
        return requestSpecification()
                .param("owner_id", user_id)
                .param("photo_id", photoId)
                .param("x", "25")
                .param("x2", "75")
                .param("y", "25")
                .param("y2", "75")
                .get(PUT_TAG)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "move photo {0} {1}")
    public Response movePhoto(String photoId, String albumId) {
        return requestSpecification()
                .param("photo_id", photoId)
                .param("target_album_id", albumId)
                .get(PHOTO_MOVE)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }

    @Step(value = "delete album {0}")
    public Response deleteAlbum(String albumId) {
        return requestSpecification()
                .param("album_id", albumId)
                .get(DELETE_ALBUM)
                .then()
                .log()
                .body()
                .spec(responseSpecification)
                .extract()
                .response();
    }
}
