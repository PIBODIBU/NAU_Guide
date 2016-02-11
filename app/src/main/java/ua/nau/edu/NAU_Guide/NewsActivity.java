package ua.nau.edu.NAU_Guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarIndeterminate;

import java.util.ArrayList;

import ua.nau.edu.API.APILoaderBuilder;
import ua.nau.edu.API.APIRefreshBuilder;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapter;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class NewsActivity extends BaseNavigationDrawerActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getPostAll.php";
    private static final String TAG = "NewsActivity";

    private static NewsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ProgressBarIndeterminate progressBar;
    private ArrayList<NewsDataModel> data = new ArrayList<NewsDataModel>();
    private APILoaderBuilder postsLoaderWithoutDialog;
    private APILoaderBuilder postsLoaderWithDialog;
    private APIRefreshBuilder APIRefreshBuilder;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SharedPrefUtils sharedPrefUtils;

    private int startLoadPosition = 0;
    private int loadNumber = 10;

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

        setUpViews();
        setUpRecyclerView();
        setUpPostsLoaders();
        setUpPostsRefreshers();
        setUpSwipeRefreshLayout();

        Log.i(TAG, "onCreate: Loading first " + loadNumber + " posts...");
        postsLoaderWithDialog.loadPostsAll(startLoadPosition, loadNumber, REQUEST_URL);
        startLoadPosition += loadNumber;
    }

    private boolean isLoggedAsLector() {
        if (!sharedPrefUtils.getToken().equals(""))
            return true;
        else
            return false;
    }

    private void setUpViews() {
        if (isLoggedAsLector()) {
            Log.i(TAG, "setUpViews/ is logged in as lector");
            ButtonFloat FABCreatePost = (ButtonFloat) findViewById(R.id.fab_create_post);
            FABCreatePost.setVisibility(View.VISIBLE);
            FABCreatePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(NewsActivity.this, CreatePostActivity.class));
                }
            });
        } else {
            Log.i(TAG, "setUpViews/ is NOT logged in as lector");
        }
    }

    private void setUpPostsLoaders() {
        postsLoaderWithDialog = new APILoaderBuilder()
                .withContext(NewsActivity.this)
                .withAdapter(adapter)
                .withLoadingDialog(true)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(this)
                .withDataSet(data);

        progressBar = (ProgressBarIndeterminate) findViewById(R.id.progressBar);
        postsLoaderWithoutDialog = new APILoaderBuilder()
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
        APIRefreshBuilder = new APIRefreshBuilder()
                .withContext(NewsActivity.this)
                .withAdapter(adapter)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(this)
                .withDataSet(data)
                .withSwipeRefreshLayout(mSwipeRefreshLayout)
                .withPostsLoaderBuilder(postsLoaderWithoutDialog)
                .withLinearLayoutManager(layoutManager);

        APIRefreshBuilder.setOnRefreshedAllListener(new APIRefreshBuilder.OnRefreshedAllListener() {
            @Override
            public void onRefreshedAction() {
                startLoadPosition = loadNumber;
                Log.i(TAG, "From onRefreshedAction/ startLoadPosition = " + Integer.toString(startLoadPosition));

                adapter.setOnLoadMoreListener(new NewsAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        Log.i(TAG, "From onRefreshedAction/ onLoadMore called");
                        data.add(null);
                        adapter.notifyItemInserted(data.size() - 1);

                        Log.i(TAG, "From onRefreshedAction / Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                        postsLoaderWithoutDialog.setProgressItemIndex(data.size() - 1);
                        postsLoaderWithoutDialog.loadPostsAll(startLoadPosition, loadNumber, REQUEST_URL);
                        startLoadPosition += loadNumber;
                    }
                });
            }
        });
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
                APIRefreshBuilder.refreshItemsAll(REQUEST_URL, loadNumber);

            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new NewsAdapter(data, NewsActivity.this, recyclerView);
        recyclerView.setAdapter(adapter);

        /** OLD OnScrollListener **/
        /*recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(NewsActivity.TAG, "onLoadMore called: Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.loadPostsAll(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });*/

        adapter.setOnLoadMoreListener(new NewsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore called");
                data.add(null);
                adapter.notifyItemInserted(data.size() - 1);

                Log.i(NewsActivity.TAG, "onLoadMore/ Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.setProgressItemIndex(data.size() - 1);
                postsLoaderWithoutDialog.loadPostsAll(startLoadPosition, loadNumber, REQUEST_URL);
                startLoadPosition += loadNumber;
            }
        });
    }

}