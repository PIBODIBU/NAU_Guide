package ua.nau.edu.API;

public class APIStrings {
    public static class RequestUrl {
        /**
         * Required params:
         *
         * token - user access token
         * post_id - id of post to delete
         *
         */
        public static final String DELETE_POST = "http://nauguide.esy.es/include/deletePost.php";

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
    }

}
