package ua.nau.edu.NAU_Guide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.CustomView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.Systems.CircleTransform;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class LoginActivity extends BaseToolbarActivity {

    private static final String LOGIN_URL = "http://nauguide.esy.es/include/log.php";
    private static final String TAG = "LoginActivity";

    /***
     * VIEWS
     ***/
    private CustomView vk_log_in;
    private CustomView login_skip;
    private CustomView login_lector;
    private TextInputLayout editTextUserName;
    private TextInputLayout editTextPassword;
    /*****/

    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String SIGNED_IN_KEY = EnumSharedPreferences.SIGNED_IN_KEY.toString();
    private static final String JUST_SIGNED_KEY = EnumSharedPreferences.JUST_SIGNED_KEY.toString();
    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_NAME_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
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
    private SharedPrefUtils sharedPrefUtils;
    private Target loadtarget;
    private int VK_APP_ID;

    private VKApiUserFull users_full = null;
    private VKRequest request_info = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200"));

    /*****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VK_APP_ID = getResources().getInteger(R.integer.VK_APP_ID);
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(getApplicationContext(), VK_APP_ID, "");

        setContentView(R.layout.activity_login);

        getToolbar();

        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(VK_PREFERENCES, LoginActivity.MODE_PRIVATE);
        sharedPrefUtils = new SharedPrefUtils(settings, settingsVK);

        /*vk_log_in = (CustomView) findViewById(R.id.login_vk);
        login_skip = (CustomView) findViewById(R.id.login_skip);
        login_lector = (CustomView) findViewById(R.id.login_lector);*/

        editTextUserName = (TextInputLayout) findViewById(R.id.username);
        editTextPassword = (TextInputLayout) findViewById(R.id.password);

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

                        settingsVK
                                .edit()
                                        //.putString(VK_NAME_KEY, users_full.first_name + " " + users_full.last_name)
                                .putString(VK_PHOTO_KEY, users_full.photo_200)
                                .putString(VK_EMAIL_KEY, VKSdk.getAccessToken().email)
                                .putInt(VK_ID_KEY, users_full.id)
                                .putBoolean(VK_SIGNED_KEY, true)
                                .apply();

                        sharedPrefUtils.setName(users_full.first_name + " " + users_full.last_name);

                        settings
                                .edit()
                                .putBoolean(SIGNED_IN_KEY, true) /*** Important! Add this after each success login ***/
                                .putString(PROFILE_PHOTO_LOCATION_KEY, PROFILE_PHOTO_LOCATION)
                                .apply();


                        loadAvatar(users_full.photo_200, FilePath, FileName);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_skip: {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                break;
            }
            case R.id.login_vk: {
                VKSdk.login(LoginActivity.this, VKScope.EMAIL, VKScope.PHOTOS, VKScope.WALL);
                break;
            }
            case R.id.login_lector: {
                hideKeyboard();
                login();
                break;
            }
        }
    }

    private void login() {
        String username = editTextUserName.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();

        Log.d(TAG, "username == " + username + " password == " + password);

        if (isUsernameValid(username) && isPasswordValid(password)) {
            userLogin(username, password);
        }
    }

    private boolean isUsernameValid(String username) {
        if (username.equals("")) {
            editTextUserName.setError("Введите логин");
            return false;
        } else {
            editTextUserName.setError("");
            return true;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.equals("")) {
            editTextPassword.setError("Введите пароль");
            return false;
        } else {
            editTextPassword.setError("");
            return true;
        }
    }

    private void userLogin(final String username, final String password) {
        new AsyncTask<String, Void, String>() {

            MaterialDialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = new MaterialDialog.Builder(LoginActivity.this)
                        .content(LoginActivity.this.getResources().getString(R.string.dialog_loading))
                        .progress(true, 0)
                        .cancelable(false)
                        .widgetColor(ContextCompat.getColor(LoginActivity.this, R.color.colorAppPrimary))
                        .contentColor(ContextCompat.getColor(LoginActivity.this, R.color.black))
                        .backgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.white))
                        .build();
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<>();

                data.put("username", params[0]);
                data.put("password", params[1]);

                APIHTTPUtils httpUtils = new APIHTTPUtils();
                return httpUtils.sendPostRequestWithParams(LOGIN_URL, data);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                if (!response.equalsIgnoreCase("error")) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("error").equals("true")) {
                            APIDialogs.AlertDialogs.badLoginOrUsername(LoginActivity.this);
                        } else if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                            doLoginStuff(
                                    jsonObject.getString("name"),
                                    jsonObject.getString("unique_id"),
                                    jsonObject.getString("email"),
                                    jsonObject.getString("photo_url"),
                                    jsonObject.getString("token"));
                        }

                    } catch (Throwable t) {
                        Log.e("LoginLectorActivty", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Connection error", Toast.LENGTH_LONG).show();
                }

                loadingDialog.dismiss();
            }
        }.execute(username, password);
    }

    private void doLoginStuff(String name, String uniqueId, String email, String photoUrl, String token) {
        FilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NAU Guide";
        FileName = "profilePhoto_200.png";
        PROFILE_PHOTO_LOCATION = FilePath + "/" + FileName;

        settings
                .edit()
                .putBoolean(SIGNED_IN_KEY, true) /*** Important! Add this after each success login ***/
                .putString(PROFILE_PHOTO_LOCATION_KEY, PROFILE_PHOTO_LOCATION)
                .putString(sharedPrefUtils.TOKEN_KEY, token)
                .apply();

        loadAvatar(photoUrl, FilePath, FileName, new CircleTransform());

        sharedPrefUtils.setName(name);
        sharedPrefUtils.setEmail(email);
        sharedPrefUtils.setUniqueId(uniqueId);

        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
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

    public void loadAvatar(String Uri, final String FilePath, final String FileName, Transformation transformation) {
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

        Picasso.with(this).load(Uri).transform(transformation).into(loadtarget);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}