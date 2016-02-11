package ua.nau.edu.API;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;

public class APIDeleteBuilder {
    private static final String BUILDER_TAG = "DeleteBuilder/ ";
    private String TAG;
    private Context context;
    private Activity activity;
    private boolean withDialog = false;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<NewsDataModel> dataSet;

    public APIDeleteBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public APIDeleteBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }

    public APIDeleteBuilder withLoadingDialog(boolean withDialog) {
        this.withDialog = withDialog;

        return this;
    }

    public APIDeleteBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public APIDeleteBuilder withRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        return this;
    }

    public APIDeleteBuilder withAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;

        return this;
    }

    public APIDeleteBuilder withDataSet(ArrayList<NewsDataModel> dataSet) {
        this.dataSet = dataSet;

        return this;
    }

    public void deletePost(final String REQUEST_URL, final String token, final int postId, final int deletePosition) {
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
                postData.put("post_id", Integer.toString(postId));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);
                Log.i(TAG, "server response: " + response);

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
                            Log.i(TAG, BUILDER_TAG + "error == false, post deleted");
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
                    APIDialogs.AlertDialogs.errorWhileDeletingPost(context);
                } else {
                    dataSet.remove(deletePosition);
                    adapter.notifyItemRemoved(deletePosition);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Удалено", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
