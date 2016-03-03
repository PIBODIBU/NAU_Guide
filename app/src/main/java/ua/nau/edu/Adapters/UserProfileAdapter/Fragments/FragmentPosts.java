package ua.nau.edu.Adapters.UserProfileAdapter.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ua.nau.edu.API.APIDeleteBuilder;
import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.API.APILoaderBuilder;
import ua.nau.edu.API.APIRefreshBuilder;
import ua.nau.edu.API.APIStrings;
import ua.nau.edu.API.APIValues;
import ua.nau.edu.NAU_Guide.CreatePostActivity;
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
    private APILoaderBuilder apiLoaderBuilder;
    private APIRefreshBuilder apiRefreshBuilder;
    private APIDeleteBuilder apiDeleteBuilder;
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
        setUpSwipeRefreshLayout();
        setUpPostsLoaders();
        setUpPostsDelete();
        setUpPostsRefreshers();

        Log.i(TAG, "onCreateView: Loading first " + loadNumber + " posts...");
        Log.i(TAG, "onCreateView: Loading first unique id: " + getActivity().getIntent().getExtras().getString("uniqueId"));
        apiLoaderBuilder.loadPostsTargeted(APIStrings.RequestUrl.GET_POST_TARGETED,
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
        final MaterialDialog loadingDialog = APIDialogs.ProgressDialogs.loading(supportActivity);

        apiLoaderBuilder = new APILoaderBuilder()
                .withContext(supportActivity)
                .withAdapter(adapter)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(supportActivity)
                .withDataSet(data);

        apiLoaderBuilder.setLoaderCallbacks(new APILoaderBuilder.LoaderCallbacks() {
            @Override
            public void onPrepare() {
                loadingDialog.show();
            }

            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                loadingDialog.dismiss();
                if (error.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                    APIDialogs.AlertDialogs.internetConnectionError(supportActivity);
                } else if (error.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    APIDialogs.AlertDialogs.serverConnectionError(supportActivity);
                } else if (error.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION_TIMED_OUT)) {
                    APIDialogs.AlertDialogs.serverConnectionError(supportActivity);
                }
            }
        });

        postsLoaderWithoutDialog = new APILoaderBuilder()
                .withContext(supportActivity)
                .withAdapter(adapter)
                .withLoadingDialog(false)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(supportActivity)
                .withDataSet(data);
        postsLoaderWithoutDialog.setLoaderCallbacks(new APILoaderBuilder.LoaderCallbacks() {
            @Override
            public void onPrepare() {
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(String error) {
                if (error.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                    APIDialogs.AlertDialogs.internetConnectionError(supportActivity);
                } else if (error.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    APIDialogs.AlertDialogs.serverConnectionError(supportActivity);
                } else if (error.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION_TIMED_OUT)) {
                    APIDialogs.AlertDialogs.serverConnectionError(supportActivity);
                }
            }
        });
    }

    private void setUpPostsDelete() {
        final MaterialDialog loadingDialog = APIDialogs.ProgressDialogs.loading(supportActivity);

        apiDeleteBuilder = new APIDeleteBuilder()
                .withActivity(supportActivity)
                .withContext(supportActivity)
                .withLoadingDialog(true)
                .withTag(TAG)
                .withRecyclerView(recyclerView)
                .withDataSet(data)
                .withAdapter(adapter);

        apiDeleteBuilder.setDeleteCallbacks(new APIDeleteBuilder.DeleteCallbacks() {
            @Override
            public void onPrepare() {
                loadingDialog.show();
            }

            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
                Snackbar.make(supportActivity.rootView, "Удалено", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError() {
                loadingDialog.dismiss();
                APIDialogs.AlertDialogs.errorWhileDeletingPost(supportActivity);
            }
        });
    }

    private void setUpPostsRefreshers() {
        apiRefreshBuilder = new APIRefreshBuilder()
                .withContext(supportActivity)
                .withAdapter(adapter)
                .withTag(TAG)
                .withRecycler(recyclerView)
                .withActivity(supportActivity)
                .withDataSet(data)
                .withSwipeRefreshLayout(mSwipeRefreshLayout)
                .withPostsLoaderBuilder(postsLoaderWithoutDialog)
                .withLinearLayoutManager(layoutManager);

        apiRefreshBuilder.setOnRefreshedTargetedListener(new APIRefreshBuilder.OnRefreshedTargetedListener() {
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

        apiRefreshBuilder.setRefresherallbacks(new APIRefreshBuilder.RefresherCallbacks() {
            @Override
            public void onPrepare() {

            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                APIDialogs.AlertDialogs.errorWhileUpdatingMessage(supportActivity);
            }
        });
    }

    private void setUpSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) FragmentView.findViewById(R.id.swipeLayoutContainer);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAppPrimary
                /*R.color.flashy_blue,
                R.color.yellow,
                R.color.flashy_blue*/);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refreshing items
                apiRefreshBuilder.refreshItemsTargeted(APIStrings.RequestUrl.GET_POST_TARGETED, authorUniqueId, loadNumber);

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

        adapter.setOnDeleteMessageAction(new NewsAdapter.OnDeleteMessageAction() {
            @Override
            public void onDeleteCalled(final int postId, final int deletePosition) {
                final AlertDialog deleteDialog = new AlertDialog.Builder(supportActivity)
                        .setTitle("Удалить?")
                        .setMessage("Это действия нельзя отменить. Вы точно хотите удалить сообщение?")
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                apiDeleteBuilder.deletePost(APIStrings.RequestUrl.DELETE_POST, sharedPrefUtils.getToken(), postId, deletePosition);
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();

                deleteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(supportActivity, R.color.colorAppPrimary));
                        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(supportActivity, R.color.black));
                    }
                });

                deleteDialog.show();
            }
        });

        adapter.setOnUpdateMessageAction(new NewsAdapter.OnUpdateMessageAction() {
            @Override
            public void onUpdateCalled(int postId, String message) {
                /*startActivity(new Intent(supportActivity, UpdatePostActivity.class)
                        .putExtra("postId", postId)
                        .putExtra("message", message));*/

                startActivityForResult(new Intent(supportActivity, UpdatePostActivity.class)
                                .putExtra("postId", postId)
                                .putExtra("message", message),
                        UpdatePostActivity.REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CreatePostActivity.REQUEST_CODE: {
                if (resultCode == APIValues.RESULT_OK) {
                    Snackbar.make(supportActivity.rootView, "Отправлено", Snackbar.LENGTH_LONG).show();
                    apiRefreshBuilder.refreshItemsTargeted(APIStrings.RequestUrl.GET_POST_TARGETED, authorUniqueId, loadNumber);
                } else if (resultCode == APIValues.RESULT_ERROR) {
                    APIDialogs.AlertDialogs.errorWhilePostingMessage(supportActivity);
                }
                break;
            }

            case UpdatePostActivity.REQUEST_CODE: {
                if (resultCode == APIValues.RESULT_OK) {
                    Snackbar.make(supportActivity.rootView, "Обновлено", Snackbar.LENGTH_LONG).show();
                    apiRefreshBuilder.refreshItemsTargeted(APIStrings.RequestUrl.GET_POST_TARGETED, authorUniqueId, loadNumber);
                } else if (resultCode == APIValues.RESULT_ERROR) {
                    APIDialogs.AlertDialogs.errorWhileUpdatingMessage(supportActivity);
                }
                break;
            }

            default: {

            }
        }
    }
}
