package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public class SharedPrefs extends AppCompatActivity {
    public static final String VK_PREFERENCES = "VK_PREFERENCES";
    public static final String VK_INFO_KEY = "VK_INFO_KEY";
    public static final String VK_PHOTO_KEY = "VK_PHOTO_KEY";
    public static final String VK_EMAIL_KEY = "VK_EMAIL_KEY";
    private static final String VK_SIGNED_KEY = "VK_SIGNED_KEY";

    private boolean SIGNED_IN;

    int MODE;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor = settings.edit();

    public SharedPrefs() {
    }

    public SharedPrefs(int MODE) {
        this.MODE = MODE;

        settings = getSharedPreferences(VK_PREFERENCES, MODE);
    }

    public void putStringVKPreferences(String key, String prefs) {
        editor.putString(key, prefs);

        editor.apply();
    }

    public void getStringVKPreferences(String KEY) {
        settings.getString(KEY, "");
    }

}
