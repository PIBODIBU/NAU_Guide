package ua.nau.edu.NAU_Guide.LoginLector;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gc.materialdesign.views.CustomView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.NAU_Guide.MainActivity;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.Systems.CircleTransform;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class LoginLectorActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String USER_NAME = "USER_NAME";
    public static final String PASSWORD = "PASSWORD";
    private static final String LOGIN_URL = "http://nauguide.esy.es/include/log.php";

    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String SIGNED_IN_KEY = EnumSharedPreferences.SIGNED_IN_KEY.toString();
    private static final String TOKEN_KEY = EnumSharedPreferences.TOKEN_KEY.toString();
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

    private Target loadtarget;

    private EditText editTextUserName;
    private EditText editTextPassword;

    private CustomView buttonLogin;

    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;
    private SharedPrefUtils sharedPrefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);

        editTextUserName = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);

        buttonLogin = (CustomView) findViewById(R.id.buttonUserLogin);

        buttonLogin.setOnClickListener(this);

        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(VK_PREFERENCES, LoginLectorActivity.MODE_PRIVATE);
        sharedPrefUtils = new SharedPrefUtils(settings, settingsVK);
    }


    private void login() {
        String username = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        userLogin(username, password);
    }

    private void userLogin(final String username, final String password) {
        new AsyncTask<String, Void, String>() {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginLectorActivity.this, null, "Please Wait", true, true);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                loading.dismiss();
                if (!response.equalsIgnoreCase("error")) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("error").equals("true")) {
                            showDialogBadUsername();
                        } else if (jsonObject.getString("error").equals("false")) {
                            doLoginStuff(
                                    jsonObject.getString("name"),
                                    jsonObject.getString("email"),
                                    jsonObject.getString("photo_url"),
                                    jsonObject.getString("token"));
                        }

                    } catch (Throwable t) {
                        Log.e("ActivityLogin", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                } else {
                    Toast.makeText(LoginLectorActivity.this, "Connection error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<>();

                data.put("username", params[0]);
                data.put("password", params[1]);

                LoginLectorUtils httpUtils = new LoginLectorUtils();
                return httpUtils.sendPostRequestWithParam(LOGIN_URL, data);
            }
        }.execute(username, password);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonLogin) {
            login();
        }
    }

    private void showDialogBadUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Неправильный логин или пароль. Пожалуйста, попробуйте еще раз.")
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(LoginLectorActivity.this, R.color.colorAppPrimary));
            }
        });
        dialog.show();
    }

    private void doLoginStuff(String name, String email, String photoUrl, String token) {
        FilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NAU Guide";
        FileName = "profilePhoto_200.png";
        PROFILE_PHOTO_LOCATION = FilePath + "/" + FileName;

        settings
                .edit()
                .putBoolean(SIGNED_IN_KEY, true) /*** Important! Add this after each success login ***/
                .putString(PROFILE_PHOTO_LOCATION_KEY, PROFILE_PHOTO_LOCATION)
                .putString(TOKEN_KEY, token)
                .apply();

        loadAvatar(photoUrl, FilePath, FileName);

        sharedPrefUtils.setName(name);
        sharedPrefUtils.setEmail(email);

        startActivity(new Intent(LoginLectorActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
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

        Picasso.with(this).load(Uri).transform(new CircleTransform()).into(loadtarget);
    }

}
