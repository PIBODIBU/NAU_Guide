package ua.nau.edu.API;

public class APIValues {
    // Max string length for posting message on wall
    public static final int maxMessageLength = 2000;

    // Max lines count before show button "Expand text..."
    public static int maxLinesBeforeExpand = 4;

    /**
     * Activity result codes
     */

    // Activity finished without errors & with job done (e.g. user successfully created message)
    public static int RESULT_OK = 1;

    // Activity finished without errors & without job done (e.g. user closed activity)
    public static int RESULT_CANCELED = 0;

    // Activity finished with errors & without job done (e.g. server/connection error)
    public static int RESULT_ERROR = -1;

}
