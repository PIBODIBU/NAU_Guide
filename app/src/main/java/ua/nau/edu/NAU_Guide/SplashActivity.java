package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;

public class SplashActivity extends Activity {
private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String SIGNED_IN_KEY = EnumSharedPreferences.SIGNED_IN_KEY.toString();
    private SharedPreferences settings = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

// Get and set system services & Buttons & SharedPreferences
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 1 seconds
                    sleep(1000);

                    // After 1 seconds redirect to another intent
                    if (settings.getBoolean(SIGNED_IN_KEY, false)) {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                    } else {
                        startActivity(new Intent(getBaseContext(), FirstLaunchActivity.class));
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
