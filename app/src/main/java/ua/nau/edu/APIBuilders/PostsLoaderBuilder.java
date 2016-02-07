package ua.nau.edu.APIBuilders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.gc.materialdesign.views.ProgressBarIndeterminate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Systems.LectorsDialogs;

public class PostsLoaderBuilder {
    private static final String BUILDER_TAG = "PostsLoaderBuilder/ ";
    private String TAG;
    private Context context;
    private Activity activity;
    private boolean withDialog = false;
    private ArrayList<NewsDataModel> data;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressBarIndeterminate progressBar;

    public PostsLoaderBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public PostsLoaderBuilder withDataSet(ArrayList<NewsDataModel> data) {
        this.data = data;

        return this;
    }

    public PostsLoaderBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }

    public PostsLoaderBuilder withLoadingDialog(boolean withDialog) {
        this.withDialog = withDialog;

        return this;
    }

    public PostsLoaderBuilder withRecycler(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        return this;
    }

    public PostsLoaderBuilder withAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;

        return this;
    }

    public PostsLoaderBuilder withProgressBar(ProgressBarIndeterminate progressBar) {
        this.progressBar = progressBar;

        return this;
    }

    public PostsLoaderBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public void loadPosts(final int startLoadPosition, final int loadNumber, final String REQUEST_URL) {
        new AsyncTask<String, Void, String>() {
            ProgressDialog loadingDialog = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (withDialog) {
                    loadingDialog = new ProgressDialog(context);
                    loadingDialog.setMessage(context.getResources().getString(R.string.dialog_loading));
                    loadingDialog.setIndeterminate(true);
                    loadingDialog.setCancelable(false);
                    loadingDialog.show();
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("start_post", Integer.toString(startLoadPosition));
                postData.put("number_of_posts", Integer.toString(loadNumber));

                Log.i(TAG, BUILDER_TAG + "StartPos: " + Integer.toString(startLoadPosition));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "No Internet avalible");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LectorsDialogs.internetConnectionErrorWithExit(context);
                        }
                    });
                } else if (response.equalsIgnoreCase("error_server")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LectorsDialogs.serverConnectionErrorWithExit(context);
                        }
                    });
                    Log.e(TAG, BUILDER_TAG + "Server error. Response code != 200");
                    return null;
                } else {
                    try {
                        int id;
                        String author;
                        String authorUniqueId;
                        String authorPhotoUrl;
                        String message;
                        String createTime;

                        JSONObject response_object = new JSONObject(response);
                        final JSONArray array = response_object.getJSONArray("post");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);

                            id = jsonObject.getInt("id");
                            author = jsonObject.getString("author");
                            authorUniqueId = jsonObject.getString("author_unique_id");
                            authorPhotoUrl = jsonObject.getString("author_photo_url");
                            message = jsonObject.getString("message");
                            createTime = jsonObject.getString("created_at");

                            if (!author.equals("") && !authorUniqueId.equals("") && !authorPhotoUrl.equals("") && !message.equals("") && !createTime.equals("")) {
                                data.add(new NewsDataModel(id, author, authorUniqueId, authorPhotoUrl, message, createTime));
                                Log.i(TAG, BUILDER_TAG + "Added: [" + id + "] " + author + "   " + createTime);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, BUILDER_TAG + "Can't create JSONArray");
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String str) {
                super.onPostExecute(str);
                if (data != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    Log.i(TAG, BUILDER_TAG + "onPostExecute: dataSet == null");
                    //activity.finish();
                }

                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    public int incStartPosition(int startLoadPosition, int loadNumber) {
        return startLoadPosition += loadNumber;
    }
}