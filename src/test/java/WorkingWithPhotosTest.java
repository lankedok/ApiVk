import com.google.gson.JsonObject;
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
        Response createPhotoAlbum = requestSpecification()
                .param("title", "Test Album")
                .param("privacy_view", "3")
                .get(CREATE_ALBUM)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();

        String albumId = parseString(createPhotoAlbum.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("id")
                .getAsString();

        Response uploadServerUrl = requestSpecification()
                .param("album_id", albumId)
                .get(UPLOAD_PHOTO)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        String uploadUrl = parseString(uploadServerUrl.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("upload_url")
                .getAsString();

        File file = new File(PATH_TO_PHOTO);
        Response uploadedPhotoInfo = given()
                .multiPart("photo", file)
                .post(uploadUrl)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        JsonObject uploadedResponse = parseString(uploadedPhotoInfo.asString())
                .getAsJsonObject();

        String server = uploadedResponse.get("server").getAsString();
        String hash = uploadedResponse.get("hash").getAsString();
        String photosList = uploadedResponse.get("photos_list").getAsString();


        Response addedPhotoResponse = requestSpecification()
                .param("server", server)
                .param("hash", hash)
                .param("album_id", albumId)
                .param("photos_list", photosList)
                .post(SAVE_PHOTO)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();

        waitSomeTime();
        String photoId = parseString(addedPhotoResponse.asString())
                .getAsJsonObject()
                .getAsJsonArray("response")
                .get(0)
                .getAsJsonObject()
                .get("id")
                .getAsString();
        Response makeCoverResponse = requestSpecification()
                .param("album_id", albumId)
                .param("photo_id", photoId)
                .get(MAKE_COVER)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();

        Assert.assertEquals(1, getIntResponseCode(makeCoverResponse));
        requestSpecification()
                .param("photo_id", photoId)
                .param("message", "test comment")
                .get(CREATE_COMMENT)
                .then()
                .spec(responseSpecification);
        waitSomeTime();
        requestSpecification()
                .param("owner_id", user_id)
                .param("photo_id", photoId)
                .param("x", "25")
                .param("x2", "75")
                .param("y", "25")
                .param("y2", "75")
                .get(PUT_TAG)
                .then()
                .spec(responseSpecification);
        waitSomeTime();

        createPhotoAlbum = requestSpecification()
                .param("title", "Public Test Album")
                .get(CREATE_ALBUM)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();

        String publicAlbumId = parseString(createPhotoAlbum.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("id")
                .getAsString();
        waitSomeTime();
        requestSpecification()
                .param("photo_id", photoId)
                .param("target_album_id", publicAlbumId)
                .get(PHOTO_MOVE)
                .then()
                .spec(responseSpecification);
        waitSomeTime();

        Response deleteAlbumResponse = requestSpecification()
                .param("album_id", albumId)
                .get(DELETE_ALBUM)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        Assert.assertEquals(1, getIntResponseCode(deleteAlbumResponse));

        //return the application to its original state
        deleteAlbumResponse = requestSpecification()
                .param("album_id", publicAlbumId)
                .get(DELETE_ALBUM)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        Assert.assertEquals(1, getIntResponseCode(deleteAlbumResponse));
    }
}
