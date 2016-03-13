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
         */
        public static final String GET_PAGE = "http://nauguide.esy.es/include/getLector.php";
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
