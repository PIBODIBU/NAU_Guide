package ua.nau.edu.NAU_Guide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.CustomView;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.RecyclerViews.MainActivityAdapter;
import ua.nau.edu.RecyclerViews.MainActivityDataModel;
import ua.nau.edu.University.NAU;

public class MainActivity extends BaseNavigationDrawerActivity implements
        View.OnClickListener {

    public MainActivity() {
    }

    /***
     * VIEWS
     ***/

    private CustomView vk_sign_out;

    /*****/

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

        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        vk_sign_out = (CustomView) findViewById(R.id.vk_sign_out);

        if (getIntent().getBooleanExtra(EXIT_KEY, false)) {
            finish();
        }

        getDrawer(
                settingsVK.getString(VK_INFO_KEY, ""),
                settingsVK.getString(VK_EMAIL_KEY, "")
        );

        if (getIntent().getBooleanExtra(JUST_SIGNED_KEY, false))
            initDialog_share();

        if (!settingsVK.getBoolean(VK_SIGNED_KEY, false)) {
            vk_sign_out.setEnabled(false);
        }

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

        removedItems = new ArrayList<Integer>();

        adapter = new MainActivityAdapter(data, this);
        recyclerView.setAdapter(adapter);


    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void initDialog_share() {
        request_share = VKApi.wall().post(VKParameters.from(
                VKApiConst.OWNER_ID,
                Integer.toString(settingsVK.getInt(VK_ID_KEY, -1)),
                VKApiConst.MESSAGE,
                getString(R.string.VK_share_text)));

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Вы вошли!")
                .content("Вы успешно авторизовались! Спасибо, что используете наше приложение. Расскажите о нем своим друзьям!")
                .positiveText("Рассказать")
                .negativeText("Отмена")

                .cancelable(false)

                .backgroundColor(getResources().getColor(R.color.white))
                .dividerColor(getResources().getColor(R.color.colorAppPrimary))
                .positiveColor(getResources().getColor(R.color.colorAppPrimary))
                .negativeColor(getResources().getColor(R.color.black))
                .contentColor(getResources().getColor(R.color.black))
                .titleColor(getResources().getColor(R.color.colorAppPrimary))

                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    }
                })
                .show();
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
            case R.id.vk_sign_out: {
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
            }
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
}