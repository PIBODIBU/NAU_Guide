package ua.nau.edu.NAU_Guide;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.RecyclerViews.LectorsActivity.LectorsAdapter;
import ua.nau.edu.RecyclerViews.LectorsActivity.LectorsDataModel;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsAdapter;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Systems.LectorsDialogs;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class NewsActivity extends BaseNavigationDrawerActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getPostAll.php";
    private static final String TAG = "LectorsListActivity";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<NewsDataModel> data = new ArrayList<NewsDataModel>();

    private SharedPrefUtils sharedPrefUtils;
    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;

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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new NewsAdapter(data, NewsActivity.this);

        new AsyncTask<String, Void, String>() {
            ProgressDialog loading = new ProgressDialog(NewsActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading.setMessage(getResources().getString(R.string.dialog_loading));
                loading.setIndeterminate(true);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected String doInBackground(String... params) {
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("limit_from", "0");
                postData.put("limit_to", "30");

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

                            Log.e("NewsActivity", "Added:" + author);

                            //if (!author.equals("") && !authorUniqueId.equals("") && !authorPhotoUrl.equals("") && !message.equals("") && !createTime.equals("")) {
                                data.add(new NewsDataModel(id, author, authorUniqueId, authorPhotoUrl, message, createTime));
                            //}
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
                loading.dismiss();
                adapter.notifyDataSetChanged();
            }
        }.execute();

        recyclerView.setAdapter(adapter);
    }

}
