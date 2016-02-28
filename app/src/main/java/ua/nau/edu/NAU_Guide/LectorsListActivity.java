package ua.nau.edu.NAU_Guide;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.RecyclerViews.LectorsActivity.LectorsAdapter;
import ua.nau.edu.RecyclerViews.LectorsActivity.LectorsDataModel;
import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.Systems.SearchViewUtils;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;


public class LectorsListActivity extends BaseNavigationDrawerActivity implements SearchView.OnQueryTextListener {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getLectors.php";
    private static final String TAG = "LectorsListActivity";

    private static LectorsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private ArrayList<LectorsDataModel> data = new ArrayList<LectorsDataModel>();

    private SharedPrefUtils sharedPrefUtils;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectorslist);

        sharedPrefUtils = new SharedPrefUtils(this);

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
                APIHTTPUtils httpUtils = new APIHTTPUtils();
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

    @Override
    protected void onDestroy() {
        for (int i = 0; i < data.size(); i++) {
            Picasso.with(this).invalidate(Uri.parse(data.get(i).getPhotoUrl()));
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lector_list, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setQueryHint(getResources().getString(R.string.lectorslist_search_hint));
        searchView.setOnQueryTextListener(this);
        SearchViewUtils.setHintColor(this, searchView, R.color.searchView_hint_color);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.onActionViewCollapsed();

                adapter.setDataSet(data);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<LectorsDataModel> filteredDataSet = new ArrayList<>();

        for (LectorsDataModel dataItem : data) {
            if (dataItem.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredDataSet.add(dataItem);
            }
        }

        adapter.setDataSet(filteredDataSet);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}
