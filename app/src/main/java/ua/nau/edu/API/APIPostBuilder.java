package ua.nau.edu.API;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.util.HashMap;

import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;

public class APIPostBuilder {
    private static final String BUILDER_TAG = "PostBuilder/ ";
    private String TAG;
    private Context context;
    private Activity activity;
    private boolean withDialog = false;

    public APIPostBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public APIPostBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }

    public APIPostBuilder withLoadingDialog(boolean withDialog) {
        this.withDialog = withDialog;

        return this;
    }

    public APIPostBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public void postMessage(final String REQUEST_URL, final String token, final String message) {
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
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("token", token);
                postData.put("message", message);

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
                    APIDialogs.AlertDialogs.errorWhilePostingMessage(context);
                } else {
                    Toast.makeText(context, "Отправлено", Toast.LENGTH_LONG).show();
                    activity.finish();
                }

            }
        }.execute();
    }
}