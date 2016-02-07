package ua.nau.edu.NAU_Guide;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gc.materialdesign.views.ProgressBarIndeterminate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.APIBuilders.PostsLoaderBuilder;
import ua.nau.edu.APIBuilders.PostsRefreshBuilder;
import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapter;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Systems.EndlessRecyclerOnScrollListener;
import ua.nau.edu.Systems.LectorsDialogs;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class NewsActivity extends BaseNavigationDrawerActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getPostAll.php";
    private static final String TAG = "NewsActivity";

    private static RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ProgressBarIndeterminate progressBar;
    private ArrayList<NewsDataModel> data = new ArrayList<NewsDataModel>();
    private PostsLoaderBuilder postsLoaderWithoutDialog;
    private PostsLoaderBuilder postsLoaderWithDialog;
    private PostsRefreshBuilder postsRefreshBuilder;

    private SharedPrefUtils sharedPrefUtils;

    private int startLoadPosition = 0;
    private int loadNumber = 5;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        sharedPrefUtils = new SharedPrefUtils(
                getSharedPreferences(sharedPrefUtils.APP_PREFERENCES, MODE_PRIVATE),
                getSharedPreferences(sharedPrefUtils.VK_PREFERENCES, LectorsListActivity.MODE_PRIVATE));

        getDrawer(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail()
        );

        setUpRecyclerView();
        setUpPostsLoaders();
        setUpPostsRefreshers();
        setUpSwipeRefreshLayout();

        Log.i(TAG, "onCreate: Loading first " + loadNumber + " posts...");

        postsLoaderWithDialog.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
        startLoadPosition = postsLoaderWithDialog.incStartPosition(startLoadPosition, loadNumber);
    }

    private void setUpPostsLoaders() {
        postsLoaderWithDialog = new PostsLoaderBuilder()
                .withContext(NewsActivity.this)
                .withAdapter(adapter)
                .withLoadingDialog(true)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(this)
                .withDataSet(data);

        progressBar = (ProgressBarIndeterminate) findViewById(R.id.progressBar);
        postsLoaderWithoutDialog = new PostsLoaderBuilder()
                .withContext(NewsActivity.this)
                .withAdapter(adapter)
                .withLoadingDialog(false)
                .withProgressBar(progressBar)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(this)
                .withDataSet(data);
    }

    private void setUpPostsRefreshers() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayoutContainer);
        postsRefreshBuilder = new PostsRefreshBuilder()
                .withContext(NewsActivity.this)
                .withAdapter(adapter)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(this)
                .withDataSet(data)
                .withSwipeRefreshLayout(mSwipeRefreshLayout)
                .withStartLoadPosition(startLoadPosition)
                .withPostsLoaderBuilder(postsLoaderWithoutDialog)
                .withLinearLayoutManager(layoutManager);

        /*recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(TAG, "From PostsRefreshBuilder / Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition = postsLoaderWithoutDialog.incStartPosition(startLoadPosition, loadNumber);
            }
        });*/

    }

    private void setUpSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAppPrimary
                /*R.color.flashy_blue,
                R.color.yellow,
                R.color.flashy_blue*/);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refreshing items
                postsRefreshBuilder.refreshItems(REQUEST_URL, loadNumber);

            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);
        layoutManager = new LinearLayoutManager(this);
        adapter = new NewsAdapter(data, NewsActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(NewsActivity.TAG, "onLoadMore called: Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition = postsLoaderWithoutDialog.incStartPosition(startLoadPosition, loadNumber);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void refreshItems() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                clearRecyclerView();
                Log.i(TAG, "Refreshing items...");
            }

            @Override
            protected String doInBackground(String... params) {
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("start_post", Integer.toString(0));
                postData.put("number_of_posts", Integer.toString(loadNumber));

                Log.i(TAG, "StartPos: " + Integer.toString(0));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "No Internet avalible");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LectorsDialogs.internetConnectionErrorWithExit(NewsActivity.this);
                        }
                    });
                } else if (response.equalsIgnoreCase("error_server")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LectorsDialogs.serverConnectionErrorWithExit(NewsActivity.this);
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

                            Log.i(TAG, "Added: [" + i + "] " + createTime);

                            if (!author.equals("") && !authorUniqueId.equals("") && !authorPhotoUrl.equals("") && !message.equals("") && !createTime.equals("")) {
                                data.add(new NewsDataModel(id, author, authorUniqueId, authorPhotoUrl, message, createTime));
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Can't create JSONArray");
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(final String str) {
                super.onPostExecute(str);
                adapter.notifyDataSetChanged();
                startLoadPosition = loadNumber;

                recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int current_page) {
                        Log.i(TAG, "Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                        postsLoaderWithoutDialog.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
                        startLoadPosition = postsLoaderWithoutDialog.incStartPosition(startLoadPosition, loadNumber);
                    }
                });

                // Load complete
                onItemsLoadComplete();
            }
        }.execute();
    }

    private void onItemsLoadComplete() {
        // Update completed
        Log.i(TAG, "Refreshed");

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void clearRecyclerView() {
        data.clear();
        adapter.notifyDataSetChanged();
    }

}