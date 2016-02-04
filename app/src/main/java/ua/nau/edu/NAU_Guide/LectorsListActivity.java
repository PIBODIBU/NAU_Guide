package ua.nau.edu.NAU_Guide;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.nau.edu.RecyclerViews.LectorsActivity.LectorsAdapter;
import ua.nau.edu.RecyclerViews.LectorsActivity.LectorsDataModel;
import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

/**
 * Created by root on 2/3/16.
 */
public class LectorsListActivity extends BaseNavigationDrawerActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getLectors.php";

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<LectorsDataModel> data;

    private SharedPrefUtils sharedPrefUtils;
    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectors);

        settings = getSharedPreferences(sharedPrefUtils.APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(sharedPrefUtils.VK_PREFERENCES, LectorsListActivity.MODE_PRIVATE);
        sharedPrefUtils = new SharedPrefUtils(settings, settingsVK);

        getDrawer(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail()
        );

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_user_data);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new AsyncTask<Void, Void, Void>() {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new ProgressDialog(LectorsListActivity.this);
                loading.setMessage("Please Wait");
                loading.setIndeterminate(true);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    LoginLectorUtils httpUtils = new LoginLectorUtils();
                    String name, uniqueId, photoUrl, institute;
                    JSONArray array = new JSONArray(httpUtils.sendPostRequest(REQUEST_URL));
                    data = new ArrayList<LectorsDataModel>();

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

                return null;
            }

            @Override
            protected void onPostExecute(final Void str) {
                super.onPostExecute(str);
                loading.dismiss();
                adapter = new LectorsAdapter(data, LectorsListActivity.this);
                recyclerView.setAdapter(adapter);
            }
        }.execute();


    }
}
