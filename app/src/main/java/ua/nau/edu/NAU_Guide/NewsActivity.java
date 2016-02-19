package ua.nau.edu.NAU_Guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.gc.materialdesign.views.AutoHideButtonFloat;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarIndeterminate;

import java.util.ArrayList;

import ua.nau.edu.API.APIDeleteBuilder;
import ua.nau.edu.API.APILoaderBuilder;
import ua.nau.edu.API.APIRefreshBuilder;
import ua.nau.edu.API.APIStrings;
import ua.nau.edu.API.APIUpdateBuilder;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapter;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class NewsActivity extends BaseNavigationDrawerActivity {

    private static final String TAG = "NewsActivity";

    private static NewsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<NewsDataModel> data = new ArrayList<NewsDataModel>();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private APILoaderBuilder postsLoaderWithoutDialog;
    private APILoaderBuilder postsLoaderWithDialog;
    private APIRefreshBuilder apiRefreshBuilder;
    private APIDeleteBuilder apiDeleteBuilder;
    private APIUpdateBuilder apiUpdateBuilder;

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
        setUpAPI();
        setUpSwipeRefreshLayout();

        Log.i(TAG, "onCreate: Loading first " + loadNumber + " posts...");
        postsLoaderWithDialog.loadPostsAll(startLoadPosition, loadNumber, APIStrings.RequestUrl.GET_POST_ALL);
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

    private void setUpAPI() {
        setUpPostsDelete();
        setUpPostsLoaders();
        setUpPostsRefreshers();
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

        postsLoaderWithoutDialog = new APILoaderBuilder()
                .withContext(NewsActivity.this)
                .withAdapter(adapter)
                .withLoadingDialog(false)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(this)
                .withDataSet(data);
    }

    private void setUpPostsDelete() {
        apiDeleteBuilder = new APIDeleteBuilder()
                .withActivity(this)
                .withContext(this)
                .withLoadingDialog(true)
                .withTag(TAG)
                .withRecyclerView(recyclerView)
                .withDataSet(data)
                .withAdapter(adapter);
    }

    private void setUpPostsRefreshers() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayoutContainer);
        apiRefreshBuilder = new APIRefreshBuilder()
                .withContext(NewsActivity.this)
                .withAdapter(adapter)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(this)
                .withDataSet(data)
                .withSwipeRefreshLayout(mSwipeRefreshLayout)
                .withPostsLoaderBuilder(postsLoaderWithoutDialog)
                .withLinearLayoutManager(layoutManager);

        apiRefreshBuilder.setOnRefreshedAllListener(new APIRefreshBuilder.OnRefreshedAllListener() {
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
                        postsLoaderWithoutDialog.loadPostsAll(startLoadPosition, loadNumber, APIStrings.RequestUrl.GET_POST_ALL);
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
                apiRefreshBuilder.refreshItemsAll(APIStrings.RequestUrl.GET_POST_ALL, loadNumber);

            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new NewsAdapter(data, NewsActivity.this, recyclerView, sharedPrefUtils);
        recyclerView.setAdapter(adapter);

        /** OLD OnScrollListener **/
        /*recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(NewsActivity.TAG, "onLoadMore called: Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.loadPostsAll(startLoadPosition, loadNumber, APIStrings.RequestUrl.GET_POST_ALL);
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
                postsLoaderWithoutDialog.loadPostsAll(startLoadPosition, loadNumber, APIStrings.RequestUrl.GET_POST_ALL);
                startLoadPosition += loadNumber;
            }
        });

        adapter.setOnDeleteMessageAction(new NewsAdapter.OnDeleteMessageAction() {
            @Override
            public void onDeleteCalled(int postId, int deletePosition) {
                apiDeleteBuilder.deletePost(APIStrings.RequestUrl.DELETE_POST, sharedPrefUtils.getToken(), postId, deletePosition);
            }
        });

        adapter.setOnUpdateMessageAction(new NewsAdapter.OnUpdateMessageAction() {
            @Override
            public void onUpdateCalled(int postId, String message) {
                startActivity(new Intent(NewsActivity.this, UpdatePostActivity.class)
                        .putExtra("postId", postId)
                        .putExtra("message", message));
            }
        });
    }

}