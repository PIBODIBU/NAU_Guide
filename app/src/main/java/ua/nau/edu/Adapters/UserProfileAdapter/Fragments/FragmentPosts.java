package ua.nau.edu.Adapters.UserProfileAdapter.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ua.nau.edu.API.APILoaderBuilder;
import ua.nau.edu.API.APIRefreshBuilder;
import ua.nau.edu.API.APIStrings;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.NAU_Guide.UpdatePostActivity;
import ua.nau.edu.NAU_Guide.UserProfileActivity;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapter;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class FragmentPosts extends Fragment {

    private static final String TAG = "UserFragmentPosts";
    private String authorUniqueId;

    private View FragmentView;

    private static NewsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<NewsDataModel> data = new ArrayList<NewsDataModel>();
    private APILoaderBuilder postsLoaderWithoutDialog;
    private APILoaderBuilder postsLoaderWithDialog;
    private APIRefreshBuilder APIRefreshBuilder;
    private UserProfileActivity supportActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SharedPrefUtils sharedPrefUtils;

    private int startLoadPosition = 0;
    private int loadNumber = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        supportActivity = (UserProfileActivity) getActivity();
        FragmentView = inflater.inflate(R.layout.fragment_user_posts, container, false);
        authorUniqueId = getActivity().getIntent().getExtras().getString("uniqueId");

        sharedPrefUtils = new SharedPrefUtils(supportActivity);

        setUpRecyclerView();
        setUpPostsLoaders();
        setUpPostsRefreshers();
        setUpSwipeRefreshLayout();

        Log.i(TAG, "onCreateView: Loading first " + loadNumber + " posts...");
        Log.i(TAG, "onCreateView: Loading first unique id: " + getActivity().getIntent().getExtras().getString("uniqueId"));
        postsLoaderWithDialog.loadPostsTargeted(APIStrings.RequestUrl.GET_POST_TARGETED,
                authorUniqueId,
                startLoadPosition,
                loadNumber);
        startLoadPosition += loadNumber;

        return FragmentView;
    }

    @Override
    public void onDestroy() {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) != null)
                Picasso.with(supportActivity).invalidate(Uri.parse(data.get(i).getAuthorPhotoUrl()));
        }

        super.onDestroy();
    }

    private void setUpPostsLoaders() {
        postsLoaderWithDialog = new APILoaderBuilder()
                .withContext(supportActivity)
                .withAdapter(adapter)
                .withLoadingDialog(true)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(supportActivity)
                .withDataSet(data);

        postsLoaderWithoutDialog = new APILoaderBuilder()
                .withContext(supportActivity)
                .withAdapter(adapter)
                .withLoadingDialog(false)
                        //.withProgressBar(progressBar)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(supportActivity)
                .withDataSet(data);
    }

    private void setUpPostsRefreshers() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) FragmentView.findViewById(R.id.swipeLayoutContainer);
        APIRefreshBuilder = new APIRefreshBuilder()
                .withContext(supportActivity)
                .withAdapter(adapter)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(supportActivity)
                .withDataSet(data)
                .withSwipeRefreshLayout(mSwipeRefreshLayout)
                .withPostsLoaderBuilder(postsLoaderWithoutDialog)
                .withLinearLayoutManager(layoutManager);

        APIRefreshBuilder.setOnRefreshedTargetedListener(new APIRefreshBuilder.OnRefreshedTargetedListener() {
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
                        postsLoaderWithoutDialog.loadPostsTargeted(APIStrings.RequestUrl.GET_POST_TARGETED,
                                authorUniqueId,
                                startLoadPosition,
                                loadNumber);
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
                APIRefreshBuilder.refreshItemsTargeted(APIStrings.RequestUrl.GET_POST_TARGETED, authorUniqueId, loadNumber);

            }
        });
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) FragmentView.findViewById(R.id.recyclerview_fragment_user_posts);
        layoutManager = new LinearLayoutManager(supportActivity);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new NewsAdapter(data, supportActivity, recyclerView, sharedPrefUtils);
        recyclerView.setAdapter(adapter);

        /**
         * OLD OnScrollListener
         */
        /*recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(NewsActivity.TAG, "onLoadMore called: Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.loadPostsAll(startLoadPosition, loadNumber, APIStrings.RequestUrl.GET_POST_TARGETED);
                startLoadPosition += loadNumber;
            }
        });*/

        adapter.setOnLoadMoreListener(new NewsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore called");
                data.add(null);
                adapter.notifyItemInserted(data.size() - 1);

                Log.i(TAG, "onLoadMore/ Loading new data... (" + Integer.toString(loadNumber) + ") posts");
                postsLoaderWithoutDialog.setProgressItemIndex(data.size() - 1);
                postsLoaderWithoutDialog.loadPostsTargeted(APIStrings.RequestUrl.GET_POST_TARGETED,
                        authorUniqueId,
                        startLoadPosition,
                        loadNumber);
                startLoadPosition += loadNumber;
            }
        });

        adapter.setOnUpdateMessageAction(new NewsAdapter.OnUpdateMessageAction() {
            @Override
            public void onUpdateCalled(int postId, String message) {
                startActivity(new Intent(supportActivity, UpdatePostActivity.class)
                        .putExtra("postId", postId)
                        .putExtra("message", message));
            }
        });
    }
}
