package ua.nau.edu.APIBuilders;


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
import ua.nau.edu.Systems.LectorsDialogs;

public class PostsRefreshBuilder {
    private static final String BUILDER_TAG = "RefreshBuilder/ ";
    private Activity activity;
    private String TAG;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<NewsDataModel> data;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private int startLoadPosition;
    private PostsLoaderBuilder postsLoader;
    private LinearLayoutManager linearLayoutManager;

    public PostsRefreshBuilder withActivity(Activity activity) {
        this.activity = activity;

        return this;
    }

    public PostsRefreshBuilder withContext(Context context) {
        this.context = context;

        return this;
    }

    public PostsRefreshBuilder withDataSet(ArrayList<NewsDataModel> data) {
        this.data = data;

        return this;
    }

    public PostsRefreshBuilder withSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;

        return this;
    }

    public PostsRefreshBuilder withRecycler(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        return this;
    }

    public PostsRefreshBuilder withAdapter(NewsAdapter adapter) {
        this.adapter = adapter;

        return this;
    }

    public PostsRefreshBuilder withTag(String TAG) {
        this.TAG = TAG;

        return this;
    }

    public PostsRefreshBuilder withStartLoadPosition(int startLoadPosition) {
        this.startLoadPosition = startLoadPosition;

        return this;
    }

    public PostsRefreshBuilder withPostsLoaderBuilder(PostsLoaderBuilder postsLoader) {
        this.postsLoader = postsLoader;

        return this;
    }

    public PostsRefreshBuilder withLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;

        return this;
    }

    public void refreshItems(final String REQUEST_URL, final int loadNumber) {
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

                startLoadPosition = loadNumber;
                Log.i(TAG, BUILDER_TAG + "startLoadPosition = " + Integer.toString(startLoadPosition));

                onItemsLoadComplete();
            }
        }.execute();

        /*recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(TAG, "From PostsRefreshBuilder / Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoader.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });*/

        adapter.setOnLoadMoreListener(new NewsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, BUILDER_TAG + "onLoadMore called");
                data.add(null);
                adapter.notifyItemInserted(data.size() - 1);

                Log.i(TAG, "From PostsRefreshBuilder / Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoader.setProgressItemIndex(data.size() - 1);
                postsLoader.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });
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
}
