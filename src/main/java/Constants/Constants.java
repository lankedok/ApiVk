package Constants;

public class Constants {


    public static class RunVerible{
        public static String server = "https://api.vk.com/method/";
        public static String user_id = "675948635";
        public static String version = "5.81";
        public static String token = "2bbc2c66fffb8bdb6140b833facc82756757b911c14bcc70289970a712c7d72b59a4808aacc9a95eef78a";
        public static String group_id = "207566036";
    }


    public static class Actions {
        public static String VK_GET_PROFILE_INFO= "account.getProfileInfo/";
        public static String VK_SAVE_PROFILE_INFO= "account.saveProfileInfo";

        public static String USER_GET = "users.get";

        public static String UPLOAD_PHOTO = "photos.getUploadServer";
        public static String OWNER_UPLOAD_PHOTO = "photos.getOwnerPhotoUploadServer";
        public static String CREATE_ALBUM = "photos.createAlbum";
        public static String DELETE_ALBUM = "photos.deleteAlbum";
        public static String SAVE_PHOTO = "photos.save";
        public static String VK_SAVE_OWNER_PHOTO = "photos.saveOwnerPhoto";
        public static String PHOTO_DELETE = "photos.delete";
        public static String MAKE_COVER = "photos.makeCover";
        public static String CREATE_COMMENT = "photos.createComment";
        public static String PUT_TAG = "photos.putTag";
        public static String PHOTO_MOVE = "photos.move";

        public static String ADD_TOPIC = "board.addTopic";
        public static String FIX_TOPIC = "board.fixTopic";
        public static String CREATE_COMMENT_TOPIC = "board.createComment";
        public static String EDIT_COMMENT_TOPIC = "board.editComment";
        public static String DELETE_COMMENT_TOPIC = "board.deleteComment";
        public static String DELETE_TOPIC = "board.deleteTopic";

        public static String CREATE_CHAT = "messages.createChat";
        public static String TEST = "messages.createChat";
    }

    public static class pathToPhoto {
        public static String PATH_TO_PHOTO = "src/main/resources/initial/photo-to-upload.jpg";
    }
}
