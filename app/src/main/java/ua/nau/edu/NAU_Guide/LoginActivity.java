package ua.nau.edu.NAU_Guide;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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

import ua.nau.edu.API.APIUrl;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.Support.Picasso.CircleTransform;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class LoginActivity extends BaseToolbarActivity {

    private static final String TAG = "LoginActivity";

    /***
     * VIEWS
     ***/
    private RelativeLayout rootView;
    private TextInputLayout editTextUserName;
    private TextInputLayout editTextPassword;
    /*****/

    private static final String JUST_SIGNED_KEY = EnumSharedPreferences.JUST_SIGNED_KEY.toString();
    private static String PROFILE_PHOTO_LOCATION;
    private String FilePath;
    private String FileName;

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

        sharedPrefUtils = new SharedPrefUtils(this);

        rootView = (RelativeLayout) findViewById(R.id.root_view);

        editTextUserName = (TextInputLayout) findViewById(R.id.username);
        editTextPassword = (TextInputLayout) findViewById(R.id.password);

        if (editTextUserName.getEditText() != null) {
            editTextUserName.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (!isUsernameValid(editTextUserName.getEditText().getText().toString())) {
                            editTextUserName.setError("Введите логин");
                        }
                    }
                }
            });
        }

        if (editTextPassword.getEditText() != null) {
            editTextPassword.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (!isPasswordValid(editTextPassword.getEditText().getText().toString())) {
                            editTextPassword.setError("Введите пароль");
                        }
                    }
                }
            });
        }

        if (savedInstanceState != null) {
            if (editTextUserName.getEditText() != null)
                editTextUserName.getEditText().setText(savedInstanceState.getString("username"));
            if (editTextPassword.getEditText() != null)
                editTextPassword.getEditText().setText(savedInstanceState.getString("password"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            outState.putString("username", editTextUserName.getEditText().getText().toString());
            outState.putString("password", editTextPassword.getEditText().getText().toString());
        } catch (Exception ex) {
            Log.e(TAG, "onSaveInstanceState() -> ", ex);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                /**
                 * Success login
                 */
                request_info.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        final MaterialDialog loadingDialog = APIDialogs.ProgressDialogs.loading(LoginActivity.this);
                        loadingDialog.show();

                        users_full = ((VKList<VKApiUserFull>) response.parsedModel).get(0);

                        FilePath = getApplicationInfo().dataDir + "/images";
                        FileName = "user_avatar.png";
                        PROFILE_PHOTO_LOCATION = FilePath + "/" + FileName;


                        sharedPrefUtils.performLoginVK(
                                users_full.first_name + " " + users_full.last_name,
                                VKAccessToken.currentToken().email,
                                PROFILE_PHOTO_LOCATION,
                                users_full.id,
                                users_full.photo_200);

                        loadAvatar(users_full.photo_200, FilePath, FileName, new AvatarLoadingCallbacks() {
                            @Override
                            public void onSuccess() {
                                loadingDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .putExtra(JUST_SIGNED_KEY, true));
                                finish();
                            }

                            @Override
                            public void onFailed() {
                                Snackbar.make(rootView, getString(R.string.loading_avatar_failed), Snackbar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPrepare() {

                            }
                        });

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
                /**
                 * Error during login
                 */
                Snackbar.make(rootView, getString(R.string.VK_sign_error), Snackbar.LENGTH_LONG).show();
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_skip: {
                hideKeyboard();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                break;
            }
            case R.id.login_vk: {
                hideKeyboard();
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
        String username = "";
        String password = "";
        try {
            if (editTextUserName.getEditText() != null)
                username = editTextUserName.getEditText().getText().toString().trim();
            if (editTextPassword.getEditText() != null)
                password = editTextPassword.getEditText().getText().toString().trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.d(TAG, "login() -> \nUsername: " + username + "\nPassword:" + password);

        if (!isUsernameValid(username)) {
            editTextUserName.setError("Введите логин");
        } else if (!isPasswordValid(password)) {
            editTextPassword.setError("Введите пароль");
        } else {
            userLogin(username, password);
        }
    }

    private boolean isUsernameValid(String username) {
        if (username.equals("")) {
            return false;
        } else {
            editTextUserName.setError("");
            return true;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.equals("")) {
            return false;
        } else {
            editTextPassword.setError("");
            return true;
        }
    }

    private void userLogin(final String username, final String password) {
        new AsyncTask<String, Void, String>() {

            MaterialDialog loadingDialog = APIDialogs.ProgressDialogs.loading(LoginActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<>();

                data.put("username", params[0]);
                data.put("password", params[1]);

                APIHTTPUtils httpUtils = new APIHTTPUtils();
                return httpUtils.sendPostRequestWithParams(APIUrl.RequestUrl.LOGIN_URL, data);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                if (!response.equalsIgnoreCase("error")) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("error").equals("true")) {
                            loadingDialog.dismiss();
                            APIDialogs.AlertDialogs.badLoginOrUsername(LoginActivity.this);
                        } else if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                            doLoginStuff(
                                    jsonObject.getString("name"),
                                    jsonObject.getString("unique_id"),
                                    jsonObject.getString("email"),
                                    jsonObject.getString("photo_url"),
                                    jsonObject.getString("token"),
                                    loadingDialog);
                        }
                    } catch (Throwable t) {
                        loadingDialog.dismiss();
                        Log.e(TAG, "Could not parse malformed JSON: \"" + response + "\"");
                    }
                } else {
                    loadingDialog.dismiss();
                    Snackbar.make(rootView, "Connection error", Snackbar.LENGTH_LONG).show();
                }
            }
        }.execute(username, password);
    }

    private void doLoginStuff(String name, String uniqueId, String email, String photoUrl, String token, final MaterialDialog loadingDialog) {
        FilePath = getApplicationInfo().dataDir + "/images";
        FileName = "user_avatar.png";
        PROFILE_PHOTO_LOCATION = FilePath + "/" + FileName;

        /**********************************************************************************
         *                 IMPORTANT! ADD THIS AFTER EACH SUCCESS LOGIN                   *
         *                                                                                */
        sharedPrefUtils.performLogin(name, email, uniqueId, token, PROFILE_PHOTO_LOCATION, photoUrl);
        /**********************************************************************************/

        loadAvatar(photoUrl, FilePath, FileName, new CircleTransform(), new AvatarLoadingCallbacks() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }

            @Override
            public void onFailed() {
                loadingDialog.dismiss();
                Snackbar.make(rootView, getString(R.string.loading_avatar_failed), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onPrepare() {

            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    public void loadAvatar(String Uri, final String FilePath, final String FileName, final AvatarLoadingCallbacks callbacks) {
        if (loadtarget == null) loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
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
                } finally {
                    callbacks.onSuccess();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                callbacks.onFailed();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                callbacks.onPrepare();
            }
        };

        Picasso.with(this).load(Uri).into(loadtarget);
    }

    public void loadAvatar(String Uri, final String FilePath, final String FileName, Transformation transformation, final AvatarLoadingCallbacks callbacks) {
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
                } finally {
                    callbacks.onSuccess();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                callbacks.onFailed();

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                callbacks.onPrepare();

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

    @Override
    public void onBackPressed() {
        if (editTextUserName.getEditText().hasFocus()) {
            editTextUserName.getEditText().clearFocus();
        } else if (editTextPassword.getEditText().hasFocus()) {
            editTextPassword.getEditText().clearFocus();
        } else {
            super.onBackPressed();
        }
    }

    private interface AvatarLoadingCallbacks {
        void onSuccess();

        void onFailed();

        void onPrepare();
    }

}