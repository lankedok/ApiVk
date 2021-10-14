import com.google.gson.JsonObject;
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

        requestSpecification()
                .get(VK_GET_PROFILE_INFO)
                .then()
                .spec(responseSpecification);

        Response response = requestSpecification()
                .param("home_town", "NY")
                .param("status", "test")
                .post(VK_SAVE_PROFILE_INFO)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        int changed = parseString(response.asString()).getAsJsonObject()
                .getAsJsonObject("response")
                .get("changed")
                .getAsInt();
        Assert.assertEquals(1, changed);
        waitSomeTime();
        //return the application to its original state
        requestSpecification()
                .param("home_town", "")
                .param("status", "")
                .post(VK_SAVE_PROFILE_INFO)
                .then()
                .spec(responseSpecification);
    }

    @Test
    public void savePhotoProfile() throws InterruptedException {
        Response uploadServerUrl = requestSpecification()
                .get(OWNER_UPLOAD_PHOTO)
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
                .post(uploadUrl);
        waitSomeTime();
        JsonObject uploadedResponse = parseString(uploadedPhotoInfo.asString())
                .getAsJsonObject();

        String server = uploadedResponse.get("server").getAsString();
        String hash = uploadedResponse.get("hash").getAsString();
        String photo = uploadedResponse.get("photo").getAsString();

        Response addedPhotoResponse = requestSpecification()
                .param("server", server)
                .param("hash", hash)
                .param("photo", photo)
                .post(VK_SAVE_OWNER_PHOTO)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        waitSomeTime();
        int saved = parseString(addedPhotoResponse.asString())
                .getAsJsonObject()
                .getAsJsonObject("response")
                .get("saved")
                .getAsInt();
        Assert.assertEquals(1, saved);


        //return the application to its original state
        Response userInfoResponse = requestSpecification()
                .param("fields", "photo_id")
                .get(USER_GET)
                .then()
                .spec(responseSpecification)
                .extract()
                .response();

        waitSomeTime();
        JsonObject userInfoJson = parseString(userInfoResponse.asString()).getAsJsonObject();
        String photoId = userInfoJson
                .getAsJsonArray("response")
                .get(0)
                .getAsJsonObject()
                .get("photo_id")
                .getAsString()
                .split("_")[1];
        requestSpecification()
                .param("photo_id", photoId)
                .get(PHOTO_DELETE)
                .then()
                .spec(responseSpecification);
    }
}
