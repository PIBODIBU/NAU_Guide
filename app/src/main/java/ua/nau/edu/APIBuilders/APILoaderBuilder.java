package ua.nau.edu.APIBuilders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ProgressBarIndeterminate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapter;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Systems.APIAlertDialogs;

public class APILoaderBuilder {
    private static final String BUILDER_TAG = "LoaderBuilder/ ";
    private String TAG;
    private Context context;
    private Activity activity;
    private boolean withDialog = false;
    private ArrayList<NewsDataModel> data;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ProgressBarIndeterminate progressBar;
    private int progressItemIndex = -1;

    public APILoaderBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public APILoaderBuilder withDataSet(ArrayList<NewsDataModel> data) {
        this.data = data;

        return this;
    }

    public APILoaderBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }

    public APILoaderBuilder withLoadingDialog(boolean withDialog) {
        this.withDialog = withDialog;

        return this;
    }

    public APILoaderBuilder withRecycler(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        return this;
    }

    public APILoaderBuilder withAdapter(NewsAdapter adapter) {
        this.adapter = adapter;

        return this;
    }

    public APILoaderBuilder withProgressBar(ProgressBarIndeterminate progressBar) {
        this.progressBar = progressBar;

        return this;
    }

    public APILoaderBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public void setProgressItemIndex(int progressItemIndex) {
        this.progressItemIndex = progressItemIndex;
    }

    public void loadPostsAll(final int startLoadPosition, final int loadNumber, final String REQUEST_URL) {
        new AsyncTask<String, Void, String>() {
            //ProgressDialog loadingDialog = null;
            MaterialDialog loadingDialog;
            int addedItems = 0;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (withDialog) {
                    /*loadingDialog = new ProgressDialog(context);
                    loadingDialog.setMessage(context.getResources().getString(R.string.dialog_loading));
                    loadingDialog.setIndeterminate(true);
                    loadingDialog.setCancelable(false);
                    loadingDialog.show();*/

                    loadingDialog = new MaterialDialog.Builder(context)
                            .content(context.getResources().getString(R.string.dialog_loading))
                            .progress(true, 0)
                            .cancelable(false)
                            .widgetColor(ContextCompat.getColor(context, R.color.colorAppPrimary))
                            .contentColor(ContextCompat.getColor(context, R.color.black))
                            .backgroundColor(ContextCompat.getColor(context, R.color.white))
                            .build();
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
                            APIAlertDialogs.internetConnectionErrorWithExit(context);
                        }
                    });
                } else if (response.equalsIgnoreCase("error_server")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIAlertDialogs.serverConnectionErrorWithExit(context);
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
                                addedItems++;
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
                if (progressItemIndex != -1) {
                    data.remove(progressItemIndex);
                    adapter.notifyItemRemoved(progressItemIndex + 1);
                }
                if (data != null && addedItems != 0) {
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
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

    public void loadPostsTargeted(final String REQUEST_URL, final String authorUniqueId, final int startLoadPosition, final int loadNumber) {
        new AsyncTask<String, Void, String>() {
            //ProgressDialog loadingDialog = null;
            MaterialDialog loadingDialog;
            int addedItems = 0;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (withDialog) {
                    /*loadingDialog = new ProgressDialog(context);
                    loadingDialog.setMessage(context.getResources().getString(R.string.dialog_loading));
                    loadingDialog.setIndeterminate(true);
                    loadingDialog.setCancelable(false);
                    loadingDialog.show();*/

                    loadingDialog = new MaterialDialog.Builder(context)
                            .content(context.getResources().getString(R.string.dialog_loading))
                            .progress(true, 0)
                            .cancelable(false)
                            .widgetColor(ContextCompat.getColor(context, R.color.colorAppPrimary))
                            .contentColor(ContextCompat.getColor(context, R.color.black))
                            .backgroundColor(ContextCompat.getColor(context, R.color.white))
                            .build();
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
                postData.put("author_unique_id", authorUniqueId);

                Log.i(TAG, BUILDER_TAG + "StartPos: " + Integer.toString(startLoadPosition));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "No Internet avalible");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIAlertDialogs.internetConnectionErrorWithExit(context);
                        }
                    });
                } else if (response.equalsIgnoreCase("error_server")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIAlertDialogs.serverConnectionErrorWithExit(context);
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
                                addedItems++;
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
                if (progressItemIndex != -1) {
                    data.remove(progressItemIndex);
                    adapter.notifyItemRemoved(progressItemIndex + 1);
                    Log.i(TAG, BUILDER_TAG + "Progress Item removed");
                }
                if (data != null && addedItems != 0) {
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
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
}