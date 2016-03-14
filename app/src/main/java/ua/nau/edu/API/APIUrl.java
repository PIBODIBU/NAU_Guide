package ua.nau.edu.API;

public class APIUrl {
    public static class RequestUrl {

        /**
         * Required params:
         * <p/>
         * action - action to perform
         */
        public static final String API = "http://nauguide.esy.es/include/api";

        /**
         * Required params:
         * <p/>
         * username - username of user for login
         * password - user's password for login
         */
        public static final String LOGIN_URL = "http://nauguide.esy.es/include/login.php";

        /**
         * Required params:
         * <p/>
         * token - user access token
         * post_id - id of post to delete
         */
        public static final String DELETE_POST = "http://nauguide.esy.es/include/deletePost.php";

        /**
         * Required params:
         * <p/>
         * token - user access token
         * post_id - id of post to update
         * new_message - new post text
         */
        public static final String UPDATE_POST = "http://nauguide.esy.es/include/updatePost.php";

        /**
         * Required params:
         */
        public static final String MAKE_POST = "http://nauguide.esy.es/include/makePost.php";

        /**
         * Required params:
         * <p/>
         * No params is required
         */
        public static final String GET_USER_ALL = "http://nauguide.esy.es/include/getUsers.php";

        /**
         * Required params:
         */
        public static final String GET_POST_ALL = "http://nauguide.esy.es/include/getPostAll.php";

        /**
         * Required params:
         */
        public static final String GET_POST_TARGETED = "http://nauguide.esy.es/include/getPostTargeted.php";

        /**
         * Required params:
         */
        public static final String GET_MYPAGE = "http://nauguide.esy.es/include/getMyPage.php";

        /**
         * Required params:
         * <p/>
         * unique_id - unique id of user
         */
        public static final String GET_USER_PAGE = "http://nauguide.esy.es/include/getUser.php";

        /**
         * Required params:
         * <p/>
         * token - token for check
         */
        public static String CHECK_TOKEN = "http://nauguide.esy.es/include/checkToken.php";

        /**
         * Required params:
         * <p/>
         * token - user's token
         * lat - user's latitude
         * lng - user's longitude
         */
        public static String REGISTER_LOCATION = "http://nauguide.esy.es/include/registerLocation";

        /**
         * Required params:
         * <p/>
         * token - user's token
         */
        public static String DISCONNECT_LOCATION = "http://nauguide.esy.es/include/disconnectLocation";

        /**
         * Required params:
         * <p/>
         */
        public static String GET_LOCATIONS = "http://nauguide.esy.es/include/getLocations";
    }

    public static class ImageUrl {
        public static final String DEFAULT_AVATAR = "http://nauguide.esy.es/images/avatar_default.png";
    }

    public static class ResponseKeys {
        public static class PageLoading {
            public static final String NAME = "name";
            public static final String BIOGRAPHY = "bio";
            public static final String PHOTO_URL = "photo_url";
            public static final String PHONE_URL = "phone";
        }
    }

}
