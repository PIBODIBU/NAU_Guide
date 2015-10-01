package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by root on 9/30/15.
 */
public class FirstLaunchActivity extends Activity {
    // VKONTAKTE SDK VARIABLES
    private int appId = 5084652;
    public static final String VK_PREFERENCES = "VK_PREFERENCES";
    public static final String VK_INFO_KEY = "VK_INFO_KEY";
    public static final String VK_PHOTO_KEY = "VK_PHOTO_KEY";
    public static final String VK_EMAIL_KEY = "VK_EMAIL_KEY";
    private static final String VK_SIGNED_KEY = "VK_SIGNED_KEY";
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";
    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private boolean SIGNED_IN;
    VKRequest request_info = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_50, photo_100, photo_200"));
//

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
                        VKApiUserFull users = ((VKList<VKApiUserFull>) response.parsedModel).get(0);
                        SIGNED_IN = true;

/** Shared Preferences **/
                        SharedPreferences settings_vk = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
                        SharedPreferences.Editor editor_vk = settings_vk.edit();

                        editor_vk.putString(VK_INFO_KEY, users.first_name + " " + users.last_name);
                        editor_vk.putString(VK_PHOTO_KEY, users.photo_200);
                        editor_vk.putString(VK_EMAIL_KEY, VKSdk.getAccessToken().email);

                        editor_vk.putBoolean(VK_SIGNED_KEY, SIGNED_IN);

                        editor_vk.apply();
/*****/

/** Important! Add this after each success login **/
                        SharedPreferences settings_global = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
                        settings_global.edit().putBoolean(FIRST_LAUNCH_KEY, false).apply();
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
                toastShowLong("Ошибка авторизации");
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
                toastShowLong("Invalid access token");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//VK initialize
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(getApplicationContext(), appId, "");
//

        setContentView(R.layout.activity_first);

// VK sing in button
        Button vk_log_in = (Button) findViewById(R.id.vk_sign_in);
        vk_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // VK login execute
                VKSdk.login(FirstLaunchActivity.this, VKScope.EMAIL, VKScope.PHOTOS);
            }
        });
//

// Google+ sign in button
        Button gg_log_in = (Button) findViewById(R.id.gg_sign_in);
        gg_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShowLong("Google+ sign in");
            }
        });
//

// Facebook sign in button
        Button fb_log_in = (Button) findViewById(R.id.fb_sign_in);
        fb_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShowLong("Facebook sign in");
            }
        });
//

// Skip button
        Button login_skip = (Button) findViewById(R.id.login_skip);
        login_skip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
/** Important! Add this after each success login **/
                SharedPreferences settings_global = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
                settings_global.edit().putBoolean(FIRST_LAUNCH_KEY, false).apply();
/*****/

                finish();
                startActivity(new Intent(FirstLaunchActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
//

    }

    @Override
    protected void onStop() {


        super.onStop();
    }
}
