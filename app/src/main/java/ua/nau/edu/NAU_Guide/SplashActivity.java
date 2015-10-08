package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {
/*** VKONTAKTE SDK VARIABLES ***/
    private static final String VK_PREFERENCES = "VK_PREFERENCES";
    private static final String VK_INFO_KEY = "VK_INFO_KEY";
    private static final String VK_PHOTO_KEY = "VK_PHOTO_KEY";
    private static final String VK_EMAIL_KEY = "VK_EMAIL_KEY";
    private static final String VK_SIGNED_KEY = "VK_SIGNED_KEY";
    private static final String VK_ID_KEY = "VK_ID_KEY";/*****/
/*****/

/*** GLOBAL VARIABLES ***/
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";
    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";

    SharedPreferences settings_global = null;
    SharedPreferences settings_vk = null;
    SharedPreferences.Editor editor_global;
    SharedPreferences.Editor editor_vk;
/*****/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

// Get and set system services & Buttons & SharedPreferences
        settings_global = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
        settings_vk = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        editor_global = settings_global.edit();
        editor_vk = settings_vk.edit();
//
        if (settings_vk.getBoolean(VK_SIGNED_KEY, false));

        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 1 seconds
                    sleep(1000);

                    // After 1 seconds redirect to another intent
                    if (settings_global.getBoolean(FIRST_LAUNCH_KEY, true)) {
                        startActivity(new Intent(getBaseContext(), FirstLaunchActivity.class));
                    } else {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    }

                    //Remove activity
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // start thread
        background.start();
    }
}
