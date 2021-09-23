import config.TestConfig;
import io.restassured.response.Response;
import org.testng.annotations.Test;


import static Constants.Constants.Actions.*;
import static Constants.Constants.RunVerible.user_id;
import static Constants.Constants.RunVerible.version;
import static Constants.Constants.ValuesForPhoto.titleAlbom;
import static Constants.Constants.ValuesForPhoto.token_user_photo;
import static io.restassured.RestAssured.given;
public class WorkingWithPhotos extends TestConfig{
    @Test
    public void WorkingWithPhoto() throws InterruptedException {
//        Response response = given().queryParam("user_id",user_id ).queryParam("access_token", token_user_photo).
//                queryParam("v", version).queryParam("title", titleAlbom).queryParam("privacy_view", 3).log().uri().
//                when().post(VK_CREATE_ALBOM).
//                then().extract().response();
//        String privetAlbomId = response.getBody().asString().substring(18, 27);
////           response.getBody().prettyPrint();
//
//        System.out.println(privetAlbomId);
//        System.out.println(response.getBody().asString());

//        Response responseUploadPhoto = given().queryParam("album_id",280882086).queryParam("user_id",user_id ).queryParam("access_token", token_user_photo).
//               queryParam("v", version).
//                when().post(VK_UPLOAD_PHOTO).
//                then().extract().response();
//        String uploadURL = responseUploadPhoto.getBody().asString();
//        uploadURL.indexOf()
//        Thread.sleep(4000);
        String urlPhoto = "https://pu.vk.com/c234231/ss2013/upload.php?act=do_add&mid=675948635&aid=280882086&gid=0&hash=9c71cd4e5e22cd7af94f8e848d3d5564&rhash=ff97f1cd121338e1036e3c11fa7ce3f5&swfupload=1&api=1";
        given().queryParam("album_id",280882086).queryParam("user_id",user_id ).queryParam("access_token", token_user_photo).
               queryParam("v", version).queryParam("album_id",280882086).queryParam("server", urlPhoto).
        when().post(VK_SAVE_PHOTO).
                then().log().all();
    }
}
