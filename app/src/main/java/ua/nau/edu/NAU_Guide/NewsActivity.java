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
import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapterTest;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Systems.EndlessRecyclerOnScrollListener;
import ua.nau.edu.Systems.LectorsDialogs;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class NewsActivity extends BaseNavigationDrawerActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getPostAll.php";
    private static final String TAG = "NewsActivity";

    private static NewsAdapterTest adapter;
    private LinearLayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ProgressBarIndeterminate progressBar;
    private ArrayList<NewsDataModel> data = new ArrayList<NewsDataModel>();
    private PostsLoaderBuilder postsLoaderWithoutDialog;
    private PostsLoaderBuilder postsLoaderWithDialog;
    private PostsRefreshBuilder postsRefreshBuilder;

    private SharedPrefUtils sharedPrefUtils;

    private int startLoadPosition = 0;
    private int loadNumber = 10;

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
        startLoadPosition += loadNumber;
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
                        //.withProgressBar(progressBar)
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

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new NewsAdapterTest(data, NewsActivity.this, recyclerView);
        recyclerView.setAdapter(adapter);

        /*recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(NewsActivity.TAG, "onLoadMore called: Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });*/

        /*adapter.setOnLoadMoreListener(new NewsAdapterTest.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore/ Adding null item...");

                data.add(null);

                if (data.get(data.size()) == null)
                    Log.i(TAG, "onLoadMore/ Added with position = " + Integer.toString(data.size()));
                else
                    Log.i(TAG, "onLoadMore/ item " + Integer.toString(data.size()) + " != null");

                adapter.notifyDataSetChanged();

                Log.i(NewsActivity.TAG, "onLoadMore/ calling postsLoaderWithoutDialog: Loading new data... (" + Integer.toString(loadNumber) + ") posts");

                postsLoaderWithoutDialog.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });*/

        adapter.setOnLoadMoreListener(new NewsAdapterTest.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore called");
                data.add(null);
                adapter.notifyItemInserted(data.size() - 1);

                Log.i(NewsActivity.TAG, "onLoadMore/ Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.setProgressItemIndex(data.size() - 1);
                postsLoaderWithoutDialog.loadPosts(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });
    }

}