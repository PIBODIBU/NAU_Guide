package ua.nau.edu.API;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.util.HashMap;

public class APICreateBuilder {
    private static final String BUILDER_TAG = "APICreateBuilder/ ";
    private String TAG;
    private Context context;
    private Activity activity;
    private CreateCallbacks createCallbacks;

    public APICreateBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public APICreateBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }

    public APICreateBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public void postMessage(final String REQUEST_URL, final String token, final String message) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (createCallbacks != null)
                    createCallbacks.onPrepare();
            }

            @Override
            protected String doInBackground(String... params) {
                APIHTTPUtils httpUtils = new APIHTTPUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("token", token);
                postData.put("message", message);

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
                            Log.e(TAG, BUILDER_TAG + "postMessage() -> error == true");
                            return errorStatus;
                        }
                        if (errorStatus.equals("false")) {
                            Log.i(TAG, BUILDER_TAG + "postMessage() -> error == false, post created");
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "postMessage() -> ", ex);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String errorStatus) {
                // Handling errors
                if (errorStatus != null) {
                    if (createCallbacks != null)
                        createCallbacks.onError();
                    return;
                }

                if (createCallbacks != null)
                    createCallbacks.onSuccess(message);
            }
        }.execute();
    }

    public void setCreateCallbacks(CreateCallbacks createCallbacks) {
        this.createCallbacks = createCallbacks;
    }

    public interface CreateCallbacks {
        void onPrepare();

        void onSuccess(String message);

        void onError();
    }
}