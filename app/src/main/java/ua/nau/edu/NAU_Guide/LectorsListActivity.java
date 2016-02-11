package ua.nau.edu.NAU_Guide;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.RecyclerViews.LectorsActivity.LectorsAdapter;
import ua.nau.edu.RecyclerViews.LectorsActivity.LectorsDataModel;
import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;


public class LectorsListActivity extends BaseNavigationDrawerActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getLectors.php";
    private static final String TAG = "LectorsListActivity";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<LectorsDataModel> data = new ArrayList<LectorsDataModel>();

    private SharedPrefUtils sharedPrefUtils;
    private SharedPreferences settings;
    private SharedPreferences settingsVK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectorslist);

        settings = getSharedPreferences(sharedPrefUtils.APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(sharedPrefUtils.VK_PREFERENCES, LectorsListActivity.MODE_PRIVATE);
        sharedPrefUtils = new SharedPrefUtils(settings, settingsVK);

        getDrawer(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail()
        );

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_user_data);
        recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new LectorsAdapter(data, LectorsListActivity.this);

        new AsyncTask<Void, Void, Void>() {
            MaterialDialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = APIDialogs.ProgressDialogs.loading(LectorsListActivity.this);
                loadingDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                LoginLectorUtils httpUtils = new LoginLectorUtils();
                String response = httpUtils.sendPostRequest(REQUEST_URL);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "No Internet avalible");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIDialogs.AlertDialogs.internetConnectionErrorWithExit(LectorsListActivity.this);
                        }
                    });
                } else if (response.equalsIgnoreCase("error_server")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIDialogs.AlertDialogs.serverConnectionErrorWithExit(LectorsListActivity.this);
                        }
                    });
                    Log.e(TAG, "Server error. Response code != 200");
                    return null;
                } else {
                    try {
                        String name, uniqueId, photoUrl, institute;

                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);

                            name = jsonObject.getString("name");
                            uniqueId = jsonObject.getString("unique_id");
                            photoUrl = jsonObject.getString("photo_url");
                            institute = jsonObject.getString("institute") + ", " + jsonObject.getString("department");

                            if (!name.equals("") && !uniqueId.equals("") && !photoUrl.equals("")) {
                                data.add(new LectorsDataModel(name, uniqueId, photoUrl, institute));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("LectorsListActivity", "Can't create JSONArray");
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(final Void str) {
                super.onPostExecute(str);
                loadingDialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        }.execute();

        recyclerView.setAdapter(adapter);
    }
}
