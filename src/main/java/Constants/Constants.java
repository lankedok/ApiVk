package Constants;

import static Constants.Constants.Server.VK_URL;

public class Constants {


    public static class RunVerible{
        public static String server = VK_URL;
        public static String path = Path.VK_PATH;
        public static String user_id = "675948635";
        public static String version = "5.81";
    }
    public static class ValuesForPhoto {
        public static String token_user_photo = "";
        public static String token_app = "";
        public static String titleAlbom = "TestAlbom";
    }
    public static class ValuesForApp {
        public static String token_app = "";
    }
    public static class Server {
        public static String VK_URL = "https://api.vk.com/";
    }
    public static class Path {
        public static String VK_PATH ="method/";
    }
    public static class Actions {
        public static String VK_GET_FRIENDS = "friends.get/";
        public static String VK_CREATE_ALBOM = "photos.createAlbum/";
        public static String VK_UPLOAD_PHOTO = "photos.getUploadServer/";
        public static String VK_SAVE_PHOTO = "photos.save/";
        public static String VK_GET_PROFILE_INFO= "account.getProfileInfo/";
        public static String VK_SAVE_PROFILE_INFO= "account.saveProfileInfo";

    }
}
