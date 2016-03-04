package ua.nau.edu.Support.SharedPrefUtils;

import android.content.Context;
import android.content.SharedPreferences;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;

public class SharedPrefUtils {

    public SharedPrefUtils(Context context) {
        sharedPrefs = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        this.context = context;
    }

    private Context context;
    private SharedPreferences sharedPrefs;

    public static final String APP_PREFERENCES = "ua.nau.edu.NAU_Guide.app_preferences";
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
    public static final String ACCOUNTHEADER_BG_IMAGE = EnumSharedPreferences.ACCOUNTHEADER_BG_IMAGE.toString();
    public static final String MAP_LAYER_KEY = EnumSharedPreferences.MAP_LAYER_KEY.toString();

    public void performLogin(String name, String email, String uniqueId, String token, String photoLocation) {
        setSignedState(true);
        setToken(token);
        setName(name);
        setEmail(email);
        setUniqueId(uniqueId);
        setProfilePhotoLocation(photoLocation);
    }


    public void performLoginVK(String name, String email, String photoLocation, int id) {
        setSignedState(true);
        setName(name);
        setEmail(email);
        setProfilePhotoLocation(photoLocation);
        setVKId(id);
    }


    public void performLogOut() {
        setSignedState(false);
        setProfilePhotoLocation("");
        setEmail("");
        setName("");
        setToken("");
        setUniqueId("");
        setVKId(-1);
    }


    public void setMapLayer(int layer) {
        sharedPrefs.edit().putInt(MAP_LAYER_KEY, layer).apply();
    }

    public int getMapLayer() {
        return sharedPrefs.getInt(MAP_LAYER_KEY, -1);
    }


    public String getName() {
        return sharedPrefs.getString(VK_INFO_KEY, "");
    }

    public void setName(String username) {
        sharedPrefs.edit().putString(VK_INFO_KEY, username).apply();
    }


    public String getEmail() {
        return sharedPrefs.getString(VK_EMAIL_KEY, "");
    }

    public void setEmail(String email) {
        sharedPrefs.edit().putString(VK_EMAIL_KEY, email).apply();
    }


    public String getToken() {
        return sharedPrefs.getString(TOKEN_KEY, "");
    }

    public void setToken(String token) {
        sharedPrefs.edit().putString(TOKEN_KEY, token).apply();
    }


    public int getVKId() {
        return sharedPrefs.getInt(VK_ID_KEY, -1);
    }

    public void setVKId(int id) {
        sharedPrefs.edit().putInt(VK_ID_KEY, id).apply();
    }


    public String getUniqueId() {
        return sharedPrefs.getString(UNIQUE_ID, "");
    }

    public void setUniqueId(String uniqueId) {
        sharedPrefs.edit().putString(UNIQUE_ID, uniqueId).apply();
    }


    public int getAccountheaderBgImage() {
        return sharedPrefs.getInt(ACCOUNTHEADER_BG_IMAGE, -1);
    }

    public void setAccountheaderBgImage(int resourceId) {
        sharedPrefs.edit().putInt(ACCOUNTHEADER_BG_IMAGE, resourceId).apply();
    }


    public boolean getSignedState() {
        return sharedPrefs.getBoolean(SIGNED_IN_KEY, false);
    }

    public void setSignedState(boolean state) {
        sharedPrefs.edit().putBoolean(SIGNED_IN_KEY, state).apply();
    }

    public String getProfilePhotoLocation() {
        return sharedPrefs.getString(PROFILE_PHOTO_LOCATION_KEY, "");
    }

    public void setProfilePhotoLocation(String location) {
        sharedPrefs.edit().putString(PROFILE_PHOTO_LOCATION_KEY, location).apply();
    }

}