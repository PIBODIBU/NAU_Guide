package ua.nau.edu.NAU_Guide;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import ua.nau.edu.Systems.EndlessRecyclerOnScrollListener;
import ua.nau.edu.Systems.LectorsDialogs;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class NewsActivity extends BaseNavigationDrawerActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getPostAll.php";
    private static final String TAG = "LectorsListActivity";

    private static RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<NewsDataModel> data = new ArrayList<NewsDataModel>();

    private SharedPrefUtils sharedPrefUtils;
    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;

    private int startLoadPosition = 0;
    private int loadNumber = 5;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        settings = getSharedPreferences(sharedPrefUtils.APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(sharedPrefUtils.VK_PREFERENCES, LectorsListActivity.MODE_PRIVATE);
        sharedPrefUtils = new SharedPrefUtils(settings, settingsVK);

        getDrawer(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail()
        );

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayoutContainer);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAppPrimary
                /*R.color.flashy_blue,
                R.color.yellow,
                R.color.flashy_blue*/);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new NewsAdapter(data, NewsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i("NewsActivity", "Loading new data...");
                loadPosts(startLoadPosition, loadNumber);
                startLoadPosition += loadNumber;
            }
        });

        loadPosts(startLoadPosition, loadNumber);
        startLoadPosition += loadNumber;

        recyclerView.setAdapter(adapter);

    }

    private void loadPosts(final int startPosition, final int loadNumber) {
        new AsyncTask<String, Void, String>() {
            //ProgressDialog loading = new ProgressDialog(NewsActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /*loading.setMessage(getResources().getString(R.string.dialog_loading));
                loading.setIndeterminate(true);
                loading.setCancelable(false);
                loading.show();*/
            }

            @Override
            protected String doInBackground(String... params) {
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("start_post", Integer.toString(startPosition));
                postData.put("number_of_posts", Integer.toString(loadNumber));

                Log.i("NewsActivity", "StartPos: " + Integer.toString(startPosition));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "No Internet avalible");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LectorsDialogs.InternetConnectionErrorWithExit(NewsActivity.this);
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

                            Log.i("NewsActivity", "Added: [" + i + "] " + author);

                            if (!author.equals("") && !authorUniqueId.equals("") && !authorPhotoUrl.equals("") && !message.equals("") && !createTime.equals("")) {
                                data.add(new NewsDataModel(id, author, authorUniqueId, authorPhotoUrl, message, createTime));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("NewsActivity", "Can't create JSONArray");
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(final String str) {
                super.onPostExecute(str);
                //loading.dismiss();
                adapter.notifyDataSetChanged();
                //adapter.notifyItemInserted(data.size());
            }
        }.execute();
    }

    private void refreshItems() {
        new AsyncTask<String, Void, String>() {
            //ProgressDialog loading = new ProgressDialog(NewsActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /*loading.setMessage(getResources().getString(R.string.dialog_loading));
                loading.setIndeterminate(true);
                loading.setCancelable(false);
                loading.show();*/
                clearRecyclerView();
                Log.i(TAG, "Refreshing items...");
            }

            @Override
            protected String doInBackground(String... params) {
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("start_post", Integer.toString(0));
                postData.put("number_of_posts", Integer.toString(loadNumber));

                Log.i("NewsActivity", "StartPos: " + Integer.toString(0));

                final String response = httpUtils.sendPostRequestWithParams(REQUEST_URL, postData);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "No Internet avalible");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LectorsDialogs.InternetConnectionErrorWithExit(NewsActivity.this);
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

                            Log.i("NewsActivity", "Added: [" + i + "] " + author);

                            if (!author.equals("") && !authorUniqueId.equals("") && !authorPhotoUrl.equals("") && !message.equals("") && !createTime.equals("")) {
                                data.add(new NewsDataModel(id, author, authorUniqueId, authorPhotoUrl, message, createTime));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("NewsActivity", "Can't create JSONArray");
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(final String str) {
                super.onPostExecute(str);
                //loading.dismiss();
                adapter.notifyDataSetChanged();
                startLoadPosition = loadNumber;

                recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int current_page) {
                        Log.i("NewsActivity", "Loading new data...");
                        loadPosts(startLoadPosition, loadNumber);
                        startLoadPosition += loadNumber;
                    }
                });

                // Load complete
                onItemsLoadComplete();
                Log.i(TAG, "Refreshed");
            }
        }.execute();
    }

    private void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void clearRecyclerView() {
        data.clear();
        adapter.notifyDataSetChanged();
    }

}