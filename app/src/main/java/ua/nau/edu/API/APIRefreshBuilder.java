package ua.nau.edu.API;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapter;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;

public class APIRefreshBuilder {
    private static final String BUILDER_TAG = "RefreshBuilder/ ";
    private Activity activity;
    private String TAG;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<NewsDataModel> data;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private APILoaderBuilder postsLoader;
    private LinearLayoutManager linearLayoutManager;
    private OnRefreshedAllListener onRefreshedAllListener;
    private OnRefreshedTargetedListener onRefreshedTargetedListener;

    public APIRefreshBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }

    public APIRefreshBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public APIRefreshBuilder withDataSet(ArrayList<NewsDataModel> data) {
        this.data = data;

        return this;
    }

    public APIRefreshBuilder withSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;

        return this;
    }

    public APIRefreshBuilder withRecycler(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        return this;
    }

    public APIRefreshBuilder withAdapter(NewsAdapter adapter) {
        this.adapter = adapter;

        return this;
    }

    public APIRefreshBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public APIRefreshBuilder withPostsLoaderBuilder(APILoaderBuilder postsLoader) {
        this.postsLoader = postsLoader;

        return this;
    }

    public APIRefreshBuilder withLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;

        return this;
    }

    public void refreshItemsAll(final String REQUEST_URL, final int loadNumber) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                adapter.setLoading();
                clearRecyclerView();
                Log.i(TAG, BUILDER_TAG + "Refreshing items...");
            }

            @Override
            protected String doInBackground(String... params) {
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("start_post", Integer.toString(0));
                postData.put("number_of_posts", Integer.toString(loadNumber));

                Log.i(TAG, BUILDER_TAG + "StartPos: " + Integer.toString(0));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, BUILDER_TAG + "No Internet avalible");
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
                    Log.e(TAG, "Server error. Response code != 200");
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
                    adapter.setLoaded();
                } else {
                    Log.e(TAG, BUILDER_TAG + "data == null");
                }

                onItemsLoadComplete();
            }
        }.execute();

        /** OLD OnScrollListener **/
        /*recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(TAG, "From PostsRefreshBuilder / Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoader.loadPostsAll(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });*/

        /*adapter.setOnLoadMoreListener(new NewsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, BUILDER_TAG + "onLoadMore called");
                data.add(null);
                adapter.notifyItemInserted(data.size() - 1);

                Log.i(TAG, "From PostsRefreshBuilder / Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoader.setProgressItemIndex(data.size() - 1);
                postsLoader.loadPostsAll(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });*/

        if (onRefreshedAllListener != null) {
            onRefreshedAllListener.onRefreshedAction();
        }
    }

    public void refreshItemsTargeted(final String REQUEST_URL, final String authorUniqueId, final int loadNumber) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                adapter.setLoading();
                clearRecyclerView();
                Log.i(TAG, BUILDER_TAG + "Refreshing items...");
            }

            @Override
            protected String doInBackground(String... params) {
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("start_post", Integer.toString(0));
                postData.put("number_of_posts", Integer.toString(loadNumber));
                postData.put("author_unique_id", authorUniqueId);

                Log.i(TAG, BUILDER_TAG + "StartPos: " + Integer.toString(0));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, BUILDER_TAG + "No Internet avalible");
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
                    Log.e(TAG, "Server error. Response code != 200");
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
                    adapter.setLoaded();
                } else {
                    Log.e(TAG, BUILDER_TAG + "data == null");
                }

                onItemsLoadComplete();
            }
        }.execute();

        if (onRefreshedTargetedListener != null) {
            onRefreshedTargetedListener.onRefreshedAction();
        }
    }

    private void onItemsLoadComplete() {
        // Update completed
        Log.i(TAG, BUILDER_TAG + "Refreshed");

        // Stop refresh animation
        swipeRefreshLayout.setRefreshing(false);
    }

    private void clearRecyclerView() {
        Log.i(TAG, BUILDER_TAG + "Deleting items...");
        data.clear();
        if (data.size() == 0)
            Log.i(TAG, BUILDER_TAG + "Deleted");
        adapter.notifyDataSetChanged();
    }

    public void setOnRefreshedAllListener(OnRefreshedAllListener onRefreshedAllListener) {
        /** Method calls after AsyncTask in PostRefreshBuilder.refreshItemsAll() **/
        this.onRefreshedAllListener = onRefreshedAllListener;
    }

    public interface OnRefreshedAllListener {
        void onRefreshedAction();
    }

    public void setOnRefreshedTargetedListener(OnRefreshedTargetedListener onRefreshedTargtedListener) {
        /** Method calls after AsyncTask in PostRefreshBuilder.refreshItemsTargeted() **/
        this.onRefreshedTargetedListener = onRefreshedTargtedListener;
    }

    public interface OnRefreshedTargetedListener {
        void onRefreshedAction();
    }
}
