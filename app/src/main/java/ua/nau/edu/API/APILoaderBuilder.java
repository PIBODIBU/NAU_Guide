package ua.nau.edu.API;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ProgressBarIndeterminate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapter;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;

public class APILoaderBuilder {
    private static final String BUILDER_TAG = "LoaderBuilder/ ";
    private String TAG;
    private Context context;
    private Activity activity;
    private boolean withDialog = false;
    private ArrayList<NewsDataModel> data;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private int progressItemIndex = -1;
    private LoaderCallbacks loaderCallbacks = null;

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

    public APILoaderBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public APILoaderBuilder withLoaderCallbacks(LoaderCallbacks loaderCallbacks) {
        this.loaderCallbacks = loaderCallbacks;

        return this;
    }

    public void setProgressItemIndex(int progressItemIndex) {
        this.progressItemIndex = progressItemIndex;
    }

    public void loadPostsAll(final int startLoadPosition, final int loadNumber, final String REQUEST_URL) {
        new AsyncTask<String, Void, String>() {
            int addedItems = 0;
            int oldSize;

            @Override
            protected void onPreExecute() {
                oldSize = adapter.getItemCount();
                if (loaderCallbacks != null) {
                    loaderCallbacks.onPrepare();
                }
            }

            /**
             * @return null if loading was successful, or String error
             */
            @Override
            protected String doInBackground(String... params) {
                APIHTTPUtils httpUtils = new APIHTTPUtils();
                HashMap<String, String> postData = new HashMap<>();
                postData.put("start_post", Integer.toString(startLoadPosition));
                postData.put("number_of_posts", Integer.toString(loadNumber));

                Log.i(TAG, BUILDER_TAG + "StartPos: " + Integer.toString(startLoadPosition));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                    return APIHTTPUtils.ERROR_CONNECTION;
                } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    return APIHTTPUtils.ERROR_SERVER;
                } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    return APIHTTPUtils.ERROR_CONNECTION_TIMED_OUT;
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
                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "loadPostsAll() -> ", ex);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String response) {
                // Handling errors
                if (response != null) {
                    if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                        if (loaderCallbacks != null) {
                            loaderCallbacks.onError(APIHTTPUtils.ERROR_SERVER);
                            return;
                        }
                    } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                        if (loaderCallbacks != null) {
                            loaderCallbacks.onError(APIHTTPUtils.ERROR_CONNECTION);
                            return;
                        }
                    } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                        if (loaderCallbacks != null) {
                            loaderCallbacks.onError(APIHTTPUtils.ERROR_CONNECTION_TIMED_OUT);
                            return;
                        }
                    }
                }

                // Remove progress item
                if (progressItemIndex != -1) {
                    try {
                        data.remove(progressItemIndex);
                        adapter.notifyItemRemoved(progressItemIndex + 1);
                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "onPostExecute() -> ", ex);
                    }
                }

                // Set new dataSet
                if (data != null && addedItems != 0) {
                    try {
                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemRangeInserted(oldSize, addedItems);
                        adapter.setLoaded();
                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "onPostExecute() -> ", ex);
                    }
                } else {
                    Log.e(TAG, BUILDER_TAG + "onPostExecute: data == null");
                }

                if (loaderCallbacks != null) {
                    loaderCallbacks.onSuccess();
                }
            }
        }.execute();
    }

    public void loadPostsTargeted(final String REQUEST_URL, final String authorUniqueId, final int startLoadPosition, final int loadNumber) {
        new AsyncTask<String, Void, String>() {
            int addedItems = 0;
            int oldSize;

            @Override
            protected void onPreExecute() {
                oldSize = adapter.getItemCount();
                if (loaderCallbacks != null) {
                    loaderCallbacks.onPrepare();
                }
            }

            /**
             * @return null if loading was successful, or String error
             */

            @Override
            protected String doInBackground(String... params) {
                APIHTTPUtils httpUtils = new APIHTTPUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("start_post", Integer.toString(startLoadPosition));
                postData.put("number_of_posts", Integer.toString(loadNumber));
                postData.put("author_unique_id", authorUniqueId);

                Log.i(TAG, BUILDER_TAG + "StartPos: " + Integer.toString(startLoadPosition));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                    return APIHTTPUtils.ERROR_CONNECTION;
                } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    return APIHTTPUtils.ERROR_SERVER;
                } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    return APIHTTPUtils.ERROR_CONNECTION_TIMED_OUT;
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
                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "loadPostsAll() -> ", ex);
                    }
                }
                return null;
            }

            private int [] kkk(){
                int[] k =  new int[100];
                return k;
            }

            @Override
            protected void onPostExecute(final String response) {
                // Handling errors
                if (response != null) {
                    if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                        if (loaderCallbacks != null) {
                            loaderCallbacks.onError(APIHTTPUtils.ERROR_SERVER);
                            return;
                        }
                    } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                        if (loaderCallbacks != null) {
                            loaderCallbacks.onError(APIHTTPUtils.ERROR_CONNECTION);
                            return;
                        }
                    } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                        if (loaderCallbacks != null) {
                            loaderCallbacks.onError(APIHTTPUtils.ERROR_CONNECTION_TIMED_OUT);
                            return;
                        }
                    }
                }

                // Remove progress item
                if (progressItemIndex != -1) {
                    try {
                        data.remove(progressItemIndex);
                        adapter.notifyItemRemoved(progressItemIndex + 1);
                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "onPostExecute() -> ", ex);
                    }
                }

                // Set new dataSet
                if (data != null && addedItems != 0) {
                    try {
                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemRangeInserted(oldSize, addedItems);
                        adapter.setLoaded();
                    } catch (Exception ex) {
                        Log.e(TAG, BUILDER_TAG + "onPostExecute() -> ", ex);
                    }
                } else {
                    Log.e(TAG, BUILDER_TAG + "onPostExecute: data == null");
                }

                if (loaderCallbacks != null) {
                    loaderCallbacks.onSuccess();
                }
            }
        }.execute();
    }

    public void setLoaderCallbacks(LoaderCallbacks loaderCallbacks) {
        this.loaderCallbacks = loaderCallbacks;
    }

    public interface LoaderCallbacks {
        void onPrepare();

        void onSuccess();

        void onError(String error);
    }
}