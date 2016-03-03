package ua.nau.edu.API;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.util.HashMap;

public class APIUpdateBuilder {
    private static final String BUILDER_TAG = "APIUpdateBuilder/ ";
    private String TAG;
    private Context context;
    private Activity activity;
    private UpdateCallbacks updateCallbacks;

    public APIUpdateBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public APIUpdateBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }


    public APIUpdateBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public void updateMessage(final String REQUEST_URL, final String token, final String message, final int postId) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                if (updateCallbacks != null)
                    updateCallbacks.onPrepare();
            }

            @Override
            protected String doInBackground(String... params) {
                APIHTTPUtils httpUtils = new APIHTTPUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("token", token);
                postData.put("new_message", message);
                postData.put("post_id", Integer.toString(postId));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                    return APIHTTPUtils.ERROR_CONNECTION;
                } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    return APIHTTPUtils.ERROR_SERVER;
                } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    return APIHTTPUtils.ERROR_CONNECTION_TIMED_OUT;
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String errorStatus = jsonObject.getString("error");

                        if (errorStatus.equals("true")) {
                            Log.e(TAG, BUILDER_TAG + "updateMessage() -> error == true");
                            return errorStatus;
                        } else if (errorStatus.equals("false")) {
                            Log.i(TAG, BUILDER_TAG + "updateMessage() -> error == false, post created");
                        }

                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "updateMessage() -> ", ex);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String errorStatus) {
                // Handling errors
                if (errorStatus != null) {
                    if (updateCallbacks != null)
                        updateCallbacks.onError();
                    return;
                }

                if (updateCallbacks != null)
                    updateCallbacks.onSuccess(message);
            }
        }.execute();
    }

    public void setUpdateCallbacks(UpdateCallbacks updateCallbacks) {
        this.updateCallbacks = updateCallbacks;
    }

    public interface UpdateCallbacks {
        void onPrepare();

        void onSuccess(String message);

        void onError();
    }
}