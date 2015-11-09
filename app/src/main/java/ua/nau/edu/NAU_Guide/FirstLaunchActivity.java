package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by root on 9/30/15.
 */
public class FirstLaunchActivity extends Activity {
    /***
     * VIEWS
     ***/

    CustomView vk_log_in;
    CustomView gg_log_in;
    CustomView fb_log_in;
    CustomView login_skip;

    /*****/

    /***
     * VKONTAKTE SDK VARIABLES
     ***/

    private int appId = 5084652;

    private static final String VK_PREFERENCES = "VK_PREFERENCES";
    private static final String VK_INFO_KEY = "VK_INFO_KEY";
    private static final String VK_PHOTO_KEY = "VK_PHOTO_KEY";
    private static final String VK_EMAIL_KEY = "VK_EMAIL_KEY";
    private static final String VK_SIGNED_KEY = "VK_SIGNED_KEY";
    private static final String VK_ID_KEY = "VK_ID_KEY";

    VKApiUserFull users_full = null;
    VKRequest request_info = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_50, photo_100, photo_200"));

    private String profilePhotoLocation;

    /*****/

    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";

    SharedPreferences settings_global = null;
    SharedPreferences settings_vk = null;
    SharedPreferences.Editor editor_global;
    SharedPreferences.Editor editor_vk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//VK initialize
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(getApplicationContext(), appId, "");
//

// Setting Content View
        setContentView(R.layout.activity_first);

// Get and set system services & Buttons & SharedPreferences
        settings_global = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
        settings_vk = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        editor_global = settings_global.edit();
        editor_vk = settings_vk.edit();

        vk_log_in = (CustomView) findViewById(R.id.vk_sign_in);
        gg_log_in = (CustomView) findViewById(R.id.gg_sign_in);
        fb_log_in = (CustomView) findViewById(R.id.fb_sign_in);
        login_skip = (CustomView) findViewById(R.id.login_skip);
//

/*** BUTTONS ***/

// VK sing in button
        vk_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // VK login execute
                VKSdk.login(FirstLaunchActivity.this, VKScope.EMAIL, VKScope.PHOTOS, VKScope.WALL);
            }
        });
//

// Google+ sign in button
        gg_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShowLong("Google+ sign in");
            }
        });
//

// Facebook sign in button
        fb_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShowLong("Facebook sign in");
            }
        });
//

// Skip button
        login_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(FirstLaunchActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
//

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

                        loadAvatar(users_full.photo_200);

/*** Shared Preferences ***/
                        editor_vk.putString(VK_INFO_KEY, users_full.first_name + " " + users_full.last_name);
                        editor_vk.putString(VK_PHOTO_KEY, users_full.photo_200);
                        editor_vk.putString(VK_EMAIL_KEY, VKSdk.getAccessToken().email);
                        editor_vk.putInt(VK_ID_KEY, users_full.id);

                        editor_vk.putBoolean(VK_SIGNED_KEY, true);

                        editor_vk.apply();
/*****/

/*** Important! Add this after each success login ***/
                        editor_global.putBoolean(FIRST_LAUNCH_KEY, false).apply();
/*****/

                        finish();
                        startActivity(new Intent(FirstLaunchActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

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

    private void loadAvatar(String Uri) {
        profilePhotoLocation = getFilesDir().getPath() + "/profilePhoto_200.jpg";

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(profilePhotoLocation);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                            ostream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {
                }
            }
        };

        Picasso.with(getApplicationContext())
                .load(Uri)
                .into(target);
    }

}
