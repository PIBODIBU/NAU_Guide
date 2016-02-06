package ua.nau.edu.NAU_Guide;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.views.CustomView;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorUtils;
import ua.nau.edu.RecyclerViews.MainActivity.MainActivityAdapter;
import ua.nau.edu.RecyclerViews.MainActivity.MainActivityDataModel;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.University.NAU;

public class MainActivity extends BaseNavigationDrawerActivity implements
        View.OnClickListener {

    public MainActivity() {
    }

    private static final String TAG = "MainActivity";

    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String SIGNED_IN_KEY = EnumSharedPreferences.SIGNED_IN_KEY.toString();
    private static final String JUST_SIGNED_KEY = EnumSharedPreferences.JUST_SIGNED_KEY.toString();
    private static final String FIRST_LAUNCH = EnumSharedPreferences.FIRST_LAUNCH.toString();
    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_INFO_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
    private static final String VK_PHOTO_KEY = EnumSharedPreferencesVK.VK_PHOTO_KEY.toString();
    private static final String VK_EMAIL_KEY = EnumSharedPreferencesVK.VK_EMAIL_KEY.toString();
    private static final String VK_SIGNED_KEY = EnumSharedPreferencesVK.VK_SIGNED_KEY.toString();
    private static final String VK_ID_KEY = EnumSharedPreferencesVK.VK_ID_KEY.toString();
    private static final String PROFILE_PHOTO_LOCATION_KEY = EnumSharedPreferences.PROFILE_PHOTO_LOCATION_KEY.toString();
    private static final String EXIT_KEY = EnumSharedPreferences.EXIT.toString();

    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;
    private SharedPrefUtils sharedPrefUtils;

    private VKRequest request_share;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MainActivityDataModel> data;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(sharedPrefUtils.APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(sharedPrefUtils.VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        sharedPrefUtils = new SharedPrefUtils(settings, settingsVK);
        //vk_sign_out = (CustomView) findViewById(R.id.vk_sign_out);        // VK SignOut Button

        if (getIntent().getBooleanExtra(EXIT_KEY, false)) {
            finish();
        }

        getDrawer(
                settingsVK.getString(VK_INFO_KEY, ""),
                settingsVK.getString(VK_EMAIL_KEY, "")
        );

        if (!sharedPrefUtils.getToken().equals(""))
            checkToken(sharedPrefUtils.getToken());

        if (getIntent().getBooleanExtra(JUST_SIGNED_KEY, false))
            showShareDialog();

        if (settings.getBoolean(FIRST_LAUNCH, true))
            settings.edit().putBoolean(FIRST_LAUNCH, false).apply();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        NAU UniData = new NAU(this);
        UniData.init();

        data = new ArrayList<MainActivityDataModel>();
        for (int i = 1; i <= 12; i++) {
            data.add(new MainActivityDataModel(
                    UniData.getCorpsInfoNameShort().get(i),
                    UniData.getCorpsInfoNameFull().get(i),
                    i,
                    UniData.getCorpsGerb().get(i)
            ));
        }

        adapter = new MainActivityAdapter(data, this);
        recyclerView.setAdapter(adapter);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void showShareDialog() {
        request_share = VKApi.wall().post(VKParameters.from(
                VKApiConst.OWNER_ID,
                Integer.toString(settingsVK.getInt(VK_ID_KEY, -1)),
                VKApiConst.MESSAGE,
                getString(R.string.VK_share_text)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Вы вошли!")
                .setMessage("Вы успешно авторизовались! Спасибо, что используете наше приложение. Расскажите о нем своим друзьям!")
                .setPositiveButton("Рассказать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request_share.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                toastShowLong(getString(R.string.VK_sent_success));
                                super.onComplete(response);
                            }

                            @Override
                            public void onError(VKError error) {
                                toastShowLong(getString(R.string.VK_sent_error));
                                super.onError(error);
                            }

                            @Override
                            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                                super.attemptFailed(request, attemptNumber, totalAttempts);
                            }
                        });

                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Позже", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAppPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAppPrimary));
            }
        });

        dialog.show();
    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        getCurrentSelection();
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.vk_sign_out: { // VK SignOut Button
                settings
                        .edit()
                        .putBoolean(SIGNED_IN_KEY, false)
                        .putString(PROFILE_PHOTO_LOCATION_KEY, "")
                        .apply();
                settingsVK
                        .edit()
                        .putString(VK_PHOTO_KEY, "")
                        .putString(VK_EMAIL_KEY, "")
                        .putString(VK_INFO_KEY, "")
                        .putBoolean(VK_SIGNED_KEY, false)
                        .apply();

                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();

                break;
            }*/
            default:
                break;
        }
    }

    public Context getContext() {
        return MainActivity.this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logOut: {

                if (settings.getBoolean(SIGNED_IN_KEY, false)) {
                    settings
                            .edit()
                            .putBoolean(SIGNED_IN_KEY, false)
                            .putString(PROFILE_PHOTO_LOCATION_KEY, "")
                            .apply();
                    settingsVK
                            .edit()
                            .putString(VK_PHOTO_KEY, "")
                            .putString(VK_EMAIL_KEY, "")
                            .putString(VK_INFO_KEY, "")
                            .putBoolean(VK_SIGNED_KEY, false)
                            .apply();

                    sharedPrefUtils.setToken("");

                    startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
                LoginLectorUtils apiUtils = new LoginLectorUtils();
                HashMap<String, String> data = new HashMap<>();
                data.put("token", params[0]);

                String response = apiUtils.sendPostRequestWithParams("http://nauguide.esy.es/include/checkToken.php", data);

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
                                    Toast.makeText(MainActivity.this, "Bad token. Exiting...", Toast.LENGTH_LONG).show();

                                    settings
                                            .edit()
                                            .putBoolean(SIGNED_IN_KEY, false)
                                            .putString(PROFILE_PHOTO_LOCATION_KEY, "")
                                            .apply();
                                    settingsVK
                                            .edit()
                                            .putString(VK_PHOTO_KEY, "")
                                            .putString(VK_EMAIL_KEY, "")
                                            .putString(VK_INFO_KEY, "")
                                            .putBoolean(VK_SIGNED_KEY, false)
                                            .apply();
                                    sharedPrefUtils.setToken("");

                                    startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

}