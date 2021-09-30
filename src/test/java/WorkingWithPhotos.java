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
import static Constants.Constants.RunVerible.user_id;
import static Constants.Constants.pathToPhoto.PATH_TO_PHOTO;
import static Constants.Constants.pathToResp.RESP_DELETE_ALBUM;
import static Constants.Constants.pathToResp.RESP_MAKE_COVER;
import static com.google.gson.JsonParser.parseString;
import static io.restassured.RestAssured.given;

public class WorkingWithPhotos extends TestConfig {
    @Test
    public void WorkingWithPhoto() throws InterruptedException, IOException {
        //create privet Album
        Map<String, String> additionalParams = createParams(
                Pair.of("title", "Test Album"),
                Pair.of("privacy_view", "3"));
        Response createPhotoAlbum = given()
                .params(additionalParams)
                .get(CREATE_ALBUM);
        Thread.sleep(2500);

        String albumId = parseString(createPhotoAlbum.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("id")
                .getAsString();

        //add photo to album
        additionalParams = createParams(
                Pair.of("album_id", albumId));
        Response uploadServerUrl = given()
                .params(additionalParams)
                .get(UPLOAD_PHOTO);
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
        String photosList = uploadedResponse.get("photos_list").getAsString();

        additionalParams = createParams(
                Pair.of("server", server),
                Pair.of("hash", hash),
                Pair.of("album_id", albumId),
                Pair.of("photos_list", photosList));

        Response addedPhotoResponse = given()
                .params(additionalParams)
                .post(SAVE_PHOTO);

        Thread.sleep(2500);
        String photoId = parseString(addedPhotoResponse.asString())
                .getAsJsonObject()
                .getAsJsonArray("response")
                .get(0)
                .getAsJsonObject()
                .get("id")
                .getAsString();
        //make cover
        additionalParams = createParams(
                Pair.of("album_id", albumId),
                Pair.of("photo_id", photoId)
                );
        Response makeCoverResponse = given()
                .params(additionalParams)
                .get(MAKE_COVER);
        Thread.sleep(2500);
        file = new File(RESP_MAKE_COVER);
        String expectedResponse = new String(Files.readAllBytes(file.toPath()));
        String expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();
        String actualJsonResponse = makeCoverResponse.asString();
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);
        //comment photo
        additionalParams = createParams(
                Pair.of("photo_id", photoId),
                Pair.of("message", "test comment"));
        Response createComment = given()
                .params(additionalParams)
                .get(CREATE_COMMENT);
        Thread.sleep(2500);
        //put tag
        additionalParams = createParams(
                Pair.of("owner_id", user_id),
                Pair.of("photo_id", photoId),
                Pair.of("x", "25"),
                Pair.of("x2", "75"),
                Pair.of("y","25"),
                Pair.of("y2","75"));
        given()
                .params(additionalParams)
                .get(PUT_TAG)
                .then()
                .statusCode(200);
        Thread.sleep(2500);

        //create public album
        additionalParams = createParams(
                Pair.of("title", "Public Test Album"));
        createPhotoAlbum = given()
                .params(additionalParams)
                .get(CREATE_ALBUM);
        Thread.sleep(2500);

        String publicAlbumId = parseString(createPhotoAlbum.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("id")
                .getAsString();
        Thread.sleep(2500);
        //move photo
        additionalParams = createParams(
                Pair.of("photo_id", photoId),
                Pair.of("target_album_id", publicAlbumId));
        given()
                .params(additionalParams)
                .get(PHOTO_MOVE)
                .then()
                .statusCode(200);
        Thread.sleep(2500);

        //delete album
        additionalParams = createParams(
                Pair.of("album_id", albumId));
        Response deleteAlbumResponse = given()
                .params(additionalParams)
                .get(DELETE_ALBUM);
        Thread.sleep(2500);
        file = new File(RESP_DELETE_ALBUM);
        expectedResponse = new String(Files.readAllBytes(file.toPath()));
        expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();
        actualJsonResponse = deleteAlbumResponse.asString();
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);

        //return the application to its original state
        additionalParams = createParams(
                Pair.of("album_id", publicAlbumId));
        deleteAlbumResponse = given()
                .params(additionalParams)
                .get(DELETE_ALBUM);
        Thread.sleep(2500);
        file = new File(RESP_DELETE_ALBUM);
        expectedResponse = new String(Files.readAllBytes(file.toPath()));
        expectedJsonResponse = parseString(expectedResponse).getAsJsonObject().toString();
        actualJsonResponse = deleteAlbumResponse.asString();
        Assert.assertEquals(expectedJsonResponse, actualJsonResponse);
    }
}
