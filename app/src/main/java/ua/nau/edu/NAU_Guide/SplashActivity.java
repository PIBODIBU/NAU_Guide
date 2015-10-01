package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";
    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread background = new Thread() {
            public void run() {
                SharedPreferences settings = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
                try {
                    // Thread will sleep for 1 seconds
                    sleep(1000);

                    // After 1 seconds redirect to another intent
                    if (settings.getBoolean(FIRST_LAUNCH_KEY, true)) {
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
