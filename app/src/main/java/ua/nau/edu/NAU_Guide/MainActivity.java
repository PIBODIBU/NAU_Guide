package ua.nau.edu.NAU_Guide;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.API.APIUrl;
import ua.nau.edu.Dialogs.NewAppVersionDialog;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.RecyclerViews.MainActivity.MainActivityAdapter;
import ua.nau.edu.RecyclerViews.MainActivity.MainActivityDataModel;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.University.NAU;

public class MainActivity extends BaseNavigationDrawerActivity {

    public MainActivity() {
    }

    private final String TAG = getClass().getSimpleName();

    private static final String JUST_SIGNED_KEY = EnumSharedPreferences.JUST_SIGNED_KEY.toString();
    private static final String EXIT_KEY = EnumSharedPreferences.EXIT.toString();

    private SharedPrefUtils sharedPrefUtils;

    private VKRequest request_share;

    private RelativeLayout rootView;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainActivityDataModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sharedPrefUtils = new SharedPrefUtils(this);

        if (getIntent().getBooleanExtra(EXIT_KEY, false)) {
            finish();
        }

        if (!sharedPrefUtils.getToken().equals(""))
            checkToken(sharedPrefUtils.getToken());

        if (getIntent().getBooleanExtra(JUST_SIGNED_KEY, false))
            showShareDialog();

        rootView = (RelativeLayout) findViewById(R.id.main_activity);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        try {
            recyclerView.setHasFixedSize(true);
        } catch (NullPointerException ex) {
            Log.e(TAG, "onCreate() -> ", ex);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        NAU UniData = new NAU(this);
        UniData.init();

        data = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            data.add(new MainActivityDataModel(
                    UniData.getCorpsInfoNameShort().get(i),
                    UniData.getCorpsInfoNameFull().get(i),
                    i,
                    UniData.getCorpsGerb().get(i)
            ));
        }
        adapter = new MainActivityAdapter(data, this, this);
        recyclerView.setAdapter(adapter);

        getDrawer(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail()
        );

        checkVersion(getString(R.string.app_version));

        /******************************************************
         * GOOGLE SERVICE, IF YOU KNOW WHAT I'M TALKING ABOUT *
         ******************************************************/
        /*if (isServiceRunning(StagerService.class)) {
            Log.e(TAG, "Service is already running");
        } else {
            Log.d(TAG, "Starting StagerService...");
            startStagerService();
        }*/
        /******************************************************
         ******************************************************/
    }

    /******************************************************
     * GOOGLE SERVICE, IF YOU KNOW WHAT I'M TALKING ABOUT *
     ******************************************************/
    private void startStagerService() {
        startService(new Intent(MainActivity.this, StagerService.class));
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /******************************************************
     ******************************************************/

    public View getRootView() {
        return rootView;
    }

    private void showShareDialog() {
        request_share = VKApi.wall().post(VKParameters.from(
                VKApiConst.OWNER_ID,
                Integer.toString(sharedPrefUtils.getVKId()),
                VKApiConst.MESSAGE,
                getString(R.string.VK_share_text)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(getString(R.string.VK_share_dialog_title))
                .setMessage(getString(R.string.VK_share_dialog_message))
                .setCancelable(false)
                .setPositiveButton("Рассказать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final MaterialDialog sendDialog = APIDialogs.ProgressDialogs.loading(MainActivity.this);
                        sendDialog.show();

                        request_share.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                sendDialog.dismiss();
                                Snackbar.make(rootView, getString(R.string.VK_sent_success), Snackbar.LENGTH_LONG).show();

                                super.onComplete(response);
                            }

                            @Override
                            public void onError(VKError error) {
                                sendDialog.dismiss();
                                Snackbar.make(rootView, getString(R.string.VK_sent_error), Snackbar.LENGTH_LONG).show();

                                super.onError(error);
                            }

                            @Override
                            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                                sendDialog.dismiss();
                                Snackbar.make(rootView, getString(R.string.VK_sent_error), Snackbar.LENGTH_LONG).show();

                                super.attemptFailed(request, attemptNumber, totalAttempts);
                            }
                        });
                    }
                })
                .setNegativeButton("Позже", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAppPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        getCurrentSelection();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem menuItemLogOut = menu.findItem(R.id.logOut);
        if (!sharedPrefUtils.getSignedState()) {
            menuItemLogOut.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut: {
                if (sharedPrefUtils.getSignedState()) {
                    sharedPrefUtils.performLogOut();

                    startActivity(new Intent(MainActivity.this, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkToken(final String token) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                APIHTTPUtils apiUtils = new APIHTTPUtils();
                HashMap<String, String> data = new HashMap<>();
                data.put("token", params[0]);

                String response = apiUtils.sendPostRequestWithParams(APIUrl.RequestUrl.CHECK_TOKEN, data);

                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "Can't check token: No Internet avalible");
                } else if (response.equalsIgnoreCase("error_server")) {
                    Log.e(TAG, "Can't check token: Server error. Response code != 200");
                    return null;
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                            Log.e("MainActivity", "Bad token: " + token);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(rootView, "Bad token. Exiting...", Snackbar.LENGTH_LONG).show();
                                    sharedPrefUtils.performLogOut();

                                    startActivity(new Intent(MainActivity.this, LoginActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                }
                            });
                        } else if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                            Log.i(TAG, "Check Token: token " + token + " accepted");
                        }
                    } catch (Exception e) {
                        Log.e("MainActivity", "Can't create JSONObject");
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void arg) {
                super.onPostExecute(arg);
            }
        }.execute(token);
    }

    private void checkVersion(final String version) {
        new AsyncTask<Void, Void, String>() {

            private final APIHTTPUtils httpUtils = new APIHTTPUtils();
            private final String currentVersion = version;

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> requestGetVersionParams = new HashMap<>();
                requestGetVersionParams.put("action", "getVersion");

                return httpUtils.sendPostRequestWithParams(APIUrl.RequestUrl.API, requestGetVersionParams);
            }

            @Override
            protected void onPostExecute(String response) {
                try {
                    Log.d(TAG, "CheckUtils -> Server response:\n" + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        String newVersion = jsonObject.getString("version");

                        Log.d(TAG, "Available version: " + newVersion);
                        Log.d(TAG, "Current version: " + currentVersion);

                        if (!newVersion.trim().equalsIgnoreCase(currentVersion)) {
                            NewAppVersionDialog dialog = new NewAppVersionDialog();
                            dialog.init(MainActivity.this, MainActivity.this, newVersion);
                            dialog.show(getSupportFragmentManager(), "NewAppVersionDialog");
                        }
                    } else {
                        Log.e(TAG, "Error while checking version. Error message: " + jsonObject.getString("error_msg"));
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "onCreate() -> onPostExecute() -> ", ex);
                }
            }
        }.execute();
    }
}