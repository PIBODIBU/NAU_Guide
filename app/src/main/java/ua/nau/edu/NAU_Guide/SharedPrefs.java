package ua.nau.edu.NAU_Guide;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public abstract class SharedPrefs extends AppCompatActivity {
    private String PREFERENCES_NAME;

    private SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, this.MODE_PRIVATE);
    private SharedPreferences.Editor editor = settings.edit();

    public SharedPrefs() {
    }

    public SharedPrefs(String PREFERENCES_NAME) {
        this.PREFERENCES_NAME = PREFERENCES_NAME;
    }

    public void putStringVKPreferences(String key, String prefs) {
        editor.putString(key, prefs);

        editor.apply();
    }

    public void getStringVKPreferences(String KEY) {
        settings.getString(KEY, "");
    }

}
