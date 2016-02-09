package ua.nau.edu.Systems.SharedPrefUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.NAU_Guide.MainActivity;

/**
 * Created by Roman on 11.12.2015.
 */
public class SharedPrefUtils {
    public SharedPrefUtils(SharedPreferences settings, SharedPreferences settingsVK) {
        sharedPrefs = settings;
        sharedPrefsVK = settingsVK;
    }

    private SharedPreferences sharedPrefs;
    private SharedPreferences sharedPrefsVK;

    public static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    public static final String SIGNED_IN_KEY = EnumSharedPreferences.SIGNED_IN_KEY.toString();
    public static final String JUST_SIGNED_KEY = EnumSharedPreferences.JUST_SIGNED_KEY.toString();
    public static final String FIRST_LAUNCH = EnumSharedPreferences.FIRST_LAUNCH.toString();
    public static final String TOKEN_KEY = EnumSharedPreferences.TOKEN_KEY.toString();
    public static final String UNIQUE_ID = EnumSharedPreferences.UNIQUE_ID.toString();
    public static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    public static final String VK_INFO_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
    public static final String VK_PHOTO_KEY = EnumSharedPreferencesVK.VK_PHOTO_KEY.toString();
    public static final String VK_NAME_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
    public static final String VK_EMAIL_KEY = EnumSharedPreferencesVK.VK_EMAIL_KEY.toString();
    public static final String VK_SIGNED_KEY = EnumSharedPreferencesVK.VK_SIGNED_KEY.toString();
    public static final String VK_ID_KEY = EnumSharedPreferencesVK.VK_ID_KEY.toString();
    public static final String PROFILE_PHOTO_LOCATION_KEY = EnumSharedPreferences.PROFILE_PHOTO_LOCATION_KEY.toString();
    public static final String EXIT_KEY = EnumSharedPreferences.EXIT.toString();


    public String getName() {
        return sharedPrefsVK.getString(VK_INFO_KEY, "");
    }

    public void setName(String username) {
        sharedPrefsVK.edit().putString(VK_INFO_KEY, username).apply();
    }

    public String getEmail() {
        return sharedPrefsVK.getString(VK_EMAIL_KEY, "");
    }

    public void setEmail(String email) {
        sharedPrefsVK.edit().putString(VK_EMAIL_KEY, email).apply();
    }

    public String getToken() {
        return sharedPrefs.getString(TOKEN_KEY, "");
    }

    public void setToken(String token) {
        sharedPrefs.edit().putString(TOKEN_KEY, token).apply();
    }

    public String getUniqueId() {
        return sharedPrefs.getString(UNIQUE_ID, "");
    }

    public void setUniqueId(String uniqueId) {
        sharedPrefs.edit().putString(UNIQUE_ID, uniqueId).apply();
    }

}