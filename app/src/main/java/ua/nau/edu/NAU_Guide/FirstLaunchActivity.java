package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.CustomView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;

public class FirstLaunchActivity extends Activity {
    /***
     * VIEWS
     ***/

    private CustomView vk_log_in;
    private CustomView gg_log_in;
    private CustomView fb_log_in;
    private CustomView login_skip;
    /*****/

    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String SIGNED_IN_KEY = EnumSharedPreferences.SIGNED_IN_KEY.toString();
    private static final String JUST_SIGNED_KEY = EnumSharedPreferences.JUST_SIGNED_KEY.toString();
    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_INFO_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
    private static final String VK_PHOTO_KEY = EnumSharedPreferencesVK.VK_PHOTO_KEY.toString();
    private static final String VK_EMAIL_KEY = EnumSharedPreferencesVK.VK_EMAIL_KEY.toString();
    private static final String VK_SIGNED_KEY = EnumSharedPreferencesVK.VK_SIGNED_KEY.toString();
    private static final String VK_ID_KEY = EnumSharedPreferencesVK.VK_ID_KEY.toString();
    private static final String PROFILE_PHOTO_LOCATION_KEY = EnumSharedPreferences.PROFILE_PHOTO_LOCATION_KEY.toString();
    private static String PROFILE_PHOTO_LOCATION;
    private String FilePath;
    private String FileName;

    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;
    private Target loadtarget;
    private int VK_APP_ID;

    private VKApiUserFull users_full = null;
    private VKRequest request_info = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200"));

    /*****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//VK initialize
        VK_APP_ID = getResources().getInteger(R.integer.VK_APP_ID);
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(getApplicationContext(), VK_APP_ID, "");

// Setting Content View
        setContentView(R.layout.activity_first);

// Get and set system services & Buttons & SharedPreferences
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);

        vk_log_in = (CustomView) findViewById(R.id.vk_sign_in);
        gg_log_in = (CustomView) findViewById(R.id.gg_sign_in);
        fb_log_in = (CustomView) findViewById(R.id.fb_sign_in);
        login_skip = (CustomView) findViewById(R.id.login_skip);

/*** BUTTONS ***/

// VK sing in button
        vk_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // VK login execute
                VKSdk.login(FirstLaunchActivity.this, VKScope.EMAIL, VKScope.PHOTOS, VKScope.WALL);
            }
        });

// Google+ sign in button
        gg_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShowLong("Google+ sign in");
            }
        });

// Facebook sign in button
        fb_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShowLong("Facebook sign in");
            }
        });

// Skip button
        login_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(FirstLaunchActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

/*****/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                request_info.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        //Do complete stuff
                        users_full = ((VKList<VKApiUserFull>) response.parsedModel).get(0);

                        FilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NAU Guide";
                        FileName = "profilePhoto_200.png";
                        PROFILE_PHOTO_LOCATION = FilePath + "/" + FileName;

/*** Shared Preferences ***/
                        settingsVK
                                .edit()
                                .putString(VK_INFO_KEY, users_full.first_name + " " + users_full.last_name)
                                .putString(VK_PHOTO_KEY, users_full.photo_200)
                                .putString(VK_EMAIL_KEY, VKSdk.getAccessToken().email)
                                .putInt(VK_ID_KEY, users_full.id)
                                .putBoolean(VK_SIGNED_KEY, true)
                                .apply();

                        settings
                                .edit()
                                .putBoolean(SIGNED_IN_KEY, true) /*** Important! Add this after each success login ***/
                                .putString(PROFILE_PHOTO_LOCATION_KEY, PROFILE_PHOTO_LOCATION)
                                .apply();

                        loadAvatar(users_full.photo_200, FilePath, FileName);

                        startActivity(new Intent(FirstLaunchActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .putExtra(JUST_SIGNED_KEY, true));

                        finish();
                        super.onComplete(response);
                    }

                    @Override
                    public void onError(VKError error) {
                        //Do error stuff
                        super.onError(error);
                    }

                    @Override
                    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                        //I don't really believe in progress
                        super.attemptFailed(request, attemptNumber, totalAttempts);
                    }
                });
            }

            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                toastShowLong(getString(R.string.VK_sign_error));
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                // VKAccessToken is invalid
                toastShowLong(getString(R.string.VK_bad_token));
            }
        }
    };

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    public void loadAvatar(String Uri, final String FilePath, final String FileName) {
        if (loadtarget == null) loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                try {
                    File dir = new File(FilePath);
                    if (!dir.exists())
                        dir.mkdirs();

                    File file = new File(dir, FileName);
                    FileOutputStream fOut = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(this).load(Uri).into(loadtarget);
    }

    private void initDialog_loading() {
        new MaterialDialog.Builder(FirstLaunchActivity.this)
                .content("Подождите")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .backgroundColor(getResources().getColor(R.color.white))
                .dividerColor(getResources().getColor(R.color.colorAppPrimary))
                .contentColor(getResources().getColor(R.color.colorAppPrimary))
                .show();
    }

}