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
    private boolean withDialog = false;

    public APIUpdateBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public APIUpdateBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }

    public APIUpdateBuilder withLoadingDialog(boolean withDialog) {
        this.withDialog = withDialog;

        return this;
    }

    public APIUpdateBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public void updateMessage(final String REQUEST_URL, final String token, final String message, final int postId) {
        new AsyncTask<String, Void, String>() {
            MaterialDialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (withDialog) {
                    loadingDialog = APIDialogs.ProgressDialogs.loading(context);
                    loadingDialog.show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                APIHTTPUtils httpUtils = new APIHTTPUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("token", token);
                postData.put("new_message", message);
                postData.put("post_id", Integer.toString(postId));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "No Internet avalible");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIDialogs.AlertDialogs.internetConnectionErrorWithExit(context);
                        }
                    });
                } else if (response.equalsIgnoreCase("error_server")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIDialogs.AlertDialogs.serverConnectionErrorWithExit(context);
                        }
                    });
                    Log.e(TAG, BUILDER_TAG + "Server error. Response code != 200");
                    return null;
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String errorStatus = jsonObject.getString("error");

                        if (errorStatus.equals("true")) {
                            Log.i(TAG, BUILDER_TAG + "error == true");
                            return errorStatus;
                        } else if (errorStatus.equals("false")) {
                            Log.i(TAG, BUILDER_TAG + "error == false, post created");
                            return errorStatus;
                        }

                    } catch (Exception e) {
                        Log.e(TAG, BUILDER_TAG + "Can't create JSONArray");
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String errorStatus) {
                super.onPostExecute(errorStatus);
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }

                if (errorStatus.equals("true")) {
                    APIDialogs.AlertDialogs.errorWhileUpdatingMessage(context);
                } else {
                    Toast.makeText(context, "Обновлено", Toast.LENGTH_LONG).show();
                    activity.finish();
                }

            }
        }.execute();
    }
}