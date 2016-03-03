package ua.nau.edu.API;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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

    private DeleteCallbacks deleteCallbacks;

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
                deleteCallbacks.onPrepare();
            }

            @Override
            protected String doInBackground(String... params) {
                APIHTTPUtils httpUtils = new APIHTTPUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("token", token);
                postData.put("post_id", Integer.toString(postId));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);
                Log.i(TAG, BUILDER_TAG + "server response: " + response);

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
                            Log.i(TAG, BUILDER_TAG + "error == true");
                            return errorStatus;
                        }
                        if (errorStatus.equals("false")) {
                            Log.i(TAG, BUILDER_TAG + "error == false, post deleted");
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "deletePost() -> doInBackground() -> ", ex);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String errorStatus) {
                // Handling errors
                if (errorStatus != null) {
                    if (deleteCallbacks != null)
                        deleteCallbacks.onError();
                    return;
                }

                // Set new dataSet
                try {
                    dataSet.remove(deletePosition);
                    adapter.notifyItemRemoved(deletePosition);
                    adapter.notifyItemRangeChanged(deletePosition, dataSet.size());
                } catch (Exception ex) {
                    Log.e(TAG, BUILDER_TAG + "deletePost() -> onPostExecute() -> ", ex);
                }
                if (deleteCallbacks != null)
                    deleteCallbacks.onSuccess();

            }
        }.execute();
    }

    public void setDeleteCallbacks(DeleteCallbacks deleteCallbacks) {
        this.deleteCallbacks = deleteCallbacks;
    }

    public interface DeleteCallbacks {
        void onPrepare();

        void onSuccess();

        void onError();
    }
}
