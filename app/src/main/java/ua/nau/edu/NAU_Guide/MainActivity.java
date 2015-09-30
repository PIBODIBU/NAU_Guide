package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.payments.VKIInAppBillingService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseNavigationDrawerActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>, View.OnClickListener,
        CheckBox.OnCheckedChangeListener, GoogleApiClient.ServerAuthCodeCallbacks {

    public MainActivity() {
    }

    private SearchView searchView;
    private InputMethodManager inputMethodManager = null;

    /**
     * Google Plus Variables
     */
    private GoogleApiClient mClient;

    private boolean mRequestServerAuthCode = false;

    // Client ID for a web server that will receive the auth code and exchange it for a
    // refresh token if offline access is requested.
    private static final String WEB_CLIENT_ID = "831107394181-12esgqnst5bcfd87gif6tc0ck499ogp4.apps.googleusercontent.com";

    // Base URL for your token exchange server, no trailing slash.
    private static final String SERVER_BASE_URL = "https://ua.nau.edu";

    // URL where the client should GET the scopes that the server would like granted
    // before asking for a serverAuthCode
    private static final String EXCHANGE_TOKEN_URL = SERVER_BASE_URL + "/exchangetoken";

    // URL where the client should POST the serverAuthCode so that the server can exchange
    // it for a refresh token,
    private static final String SELECT_SCOPES_URL = SERVER_BASE_URL + "/selectscopes";

    private static final String SAVED_PROGRESS = "sign_in_progress";

    // We use mSignInProgress to track whether user has clicked sign in.
    // mSignInProgress can be one of three values:
    //
    //       STATE_DEFAULT: The default state of the application before the user
    //                      has clicked 'sign in', or after they have clicked
    //                      'sign out'.  In this state we will not attempt to
    //                      resolve sign in errors and so will display our
    //                      Activity in a signed out state.
    //       STATE_SIGN_IN: This state indicates that the user has clicked 'sign
    //                      in', so resolve successive errors preventing sign in
    //                      until the user has successfully authorized an account
    //                      for our app.
    //   STATE_IN_PROGRESS: This state indicates that we have started an intent to
    //                      resolve an error, and so we should not start further
    //                      intents until the current intent completes.
    private int mSignInProgress;

    // Used to store the PendingIntent most recently returned by Google Play
    // services until the user clicks 'sign in'.
    private PendingIntent mSignInIntent;

    private boolean mServerHasToken = true;

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;

    private static final int RC_SIGN_IN = 0;

    // Used to store the error code most recently returned by Google Play services
    // until the user clicks 'sign in'.
    private int mSignInError;

    //views
    private SignInButton mSignInButoon;
    private TextView plusText;
    private TextView plus_user;
    private ListView mCirclesListView;
    private Button mSignOutButton;

    private ArrayList<String> mCirclesList;
    private ArrayAdapter<String> mCirclesAdapter;

    // VKONTAKTE SDK VARIABLES
    private int appId = 5084652;
    public static final String VK_PREFERENCES = "VK_PREFERENCES";
    public static final String VK_INFO_KEY = "VK_INFO_KEY";
    public static final String VK_PHOTO_KEY = "VK_PHOTO_KEY";
    public static final String VK_EMAIL_KEY = "VK_EMAIL_KEY";
    private static final String VK_SIGNED_KEY = "VK_SIGNED_KEY";
    private boolean SIGNED_IN;
    VKRequest request_info = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_50, photo_100, photo_200"));
//

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
                        VKApiUserFull users = ((VKList<VKApiUserFull>) response.parsedModel).get(0);
                        SIGNED_IN = true;

/********** <setShared Preferences> **********/
                        SharedPreferences settings = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putString(VK_INFO_KEY, users.first_name + " " + users.last_name);
                        editor.putString(VK_PHOTO_KEY, users.photo_200);
                        editor.putString(VK_EMAIL_KEY, VKSdk.getAccessToken().email);

                        editor.putBoolean(VK_SIGNED_KEY, SIGNED_IN);

                        editor.apply();
/********** </setShared Preferences> **********/

                        startActivity(new Intent(MainActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

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
                TextView text = (TextView) findViewById(R.id.textView2);
                text.setText("SOMETHING WRONG, BRO");
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // If the error resolution was successful we should continue
                    // processing errors.
                    mSignInProgress = STATE_SIGN_IN;
                } else {
                    // If the error resolution was not successful or the user canceled,
                    // we should stop processing errors.
                    mSignInProgress = STATE_DEFAULT;
                }

                if (!mClient.isConnecting()) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
                    mClient.connect();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
// VKAccessToken is invalid
                toastShowLong("Invalid access token");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        super.onCreate(savedInstanceState);

/********** < getShared Preferences> **********/
        SharedPreferences settings = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
/********** </getShared Preferences> **********/

//VK initialize
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(getApplicationContext(), appId, "");
//
        setContentView(R.layout.activity_main);

// Get system services
        inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

// Create object and load Navigation Drawer
        /*BaseNavigationDrawerActivity NavigationDrawer = new BaseNavigationDrawerActivity(
                settings.getString(VK_INFO_KEY, ""),
                settings.getString(VK_PHOTO_KEY, ""));*/

        //NavigationDrawer.getDrawer();

        getDrawer(
                settings.getString(VK_INFO_KEY, ""),
                settings.getString(VK_PHOTO_KEY, ""),
                settings.getString(VK_EMAIL_KEY, ""),
                settings.getBoolean(VK_SIGNED_KEY, false)
        );
//

// VK sing in button
        Button vk_log_in = (Button) findViewById(R.id.vk_sign_in);
        vk_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// VK login execute
                VKSdk.login(MainActivity.this, VKScope.EMAIL, VKScope.PHOTOS);
            }
        });
//

// VK sign out button
        Button vk_log_out = (Button) findViewById(R.id.vk_sign_out);
        vk_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(VK_PREFERENCES, BaseNavigationDrawerActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                editor.putBoolean(VK_SIGNED_KEY, false);

                editor.apply();

                /*startActivity(new Intent(MainActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*/

                getDrawer(
                        settings.getString(VK_INFO_KEY, ""),
                        settings.getString(VK_PHOTO_KEY, ""),
                        settings.getString(VK_EMAIL_KEY, ""),
                        settings.getBoolean(VK_SIGNED_KEY, false)
                );

            }
        });
//

        Button restart = (Button) findViewById(R.id.button_restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

                startActivity(new Intent(MainActivity.this, SplashActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Button restart_first = (Button) findViewById(R.id.restart_first);
        restart_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences("GLOBAL_PREFERENCES", MODE_PRIVATE);
                settings
                        .edit()
                        .putBoolean("FIRST_LAUNCH_KEY", true)
                        .apply();

                finish();

                startActivity(new Intent(MainActivity.this, SplashActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        /**
         *
         * Google plus
         **/

        if (savedInstanceState != null) {
            mSignInProgress = savedInstanceState
                    .getInt(SAVED_PROGRESS, STATE_DEFAULT);
        }

        mClient = buildGoogleApiClient();

        mSignInButoon = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButoon.setOnClickListener(this);

        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);

        plusText = (TextView) findViewById(R.id.plus_text);
        plus_user = (TextView) findViewById(R.id.plus_user);
        mCirclesListView = (ListView) findViewById(R.id.circles_list);


        mCirclesList = new ArrayList<>();
        mCirclesAdapter = new ArrayAdapter<String>(
                this, R.layout.circle_member, mCirclesList);
        mCirclesListView.setAdapter(mCirclesAdapter);


    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        mClient.disconnect();

        super.onStop();
    }

    @Override
    protected void onResume() {
        getCurrentSelection();
        super.onResume();
    }

    /**
     * Google plus
     **/

    private GoogleApiClient buildGoogleApiClient() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN);

        if (mRequestServerAuthCode) {

            builder = builder.requestServerAuthCode(WEB_CLIENT_ID, this);
        }

        return builder.build();
    }

    @Override
    public void onClick(View view) {
        if (!mClient.isConnecting()) {
            switch (view.getId()) {

                case R.id.sign_in_button:
                    mSignInProgress = STATE_SIGN_IN;
                    mClient.connect();
                    break;

                case R.id.sign_out_button:
                    if (mClient.isConnected()) {
                        Plus.AccountApi.clearDefaultAccount(mClient);
                        mClient.disconnect();
                    }
                    onSignedOut();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInButoon.setEnabled(false);
        mSignOutButton.setEnabled(true);

        Person user = Plus.PeopleApi.getCurrentPerson(mClient);

        if (user == null) {
            createErrorDialog().show();
            mSignInButoon.setEnabled(true);
            mSignOutButton.setEnabled(false);
        } else {
            plus_user.setText(user.getDisplayName());
        }

        Plus.PeopleApi.loadVisible(mClient, null)
                .setResultCallback(this);

        mSignInProgress = STATE_DEFAULT;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.i("", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.
            Log.w("", "API Unavailable.");
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
                resolveSignInError();
            }
        }

        // In this sample we consider the user signed out whenever they do not have
        // a connection to Google Play services.
        onSignedOut();

    }


    private void onSignedOut() {
        // Update the UI to reflect that the user is signed out.
        mSignInButoon.setEnabled(true);
        mSignOutButton.setEnabled(false);

        plus_user.setText("");

        mCirclesList.clear();
        mCirclesAdapter.notifyDataSetChanged();
        mClient.disconnect();
        mClient.connect();
    }

    private void resolveSignInError() {
        if (mSignInIntent != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.i("", "Sign in intent could not be sent: "
                        + e.getLocalizedMessage());
                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN;
                mClient.connect();
            }
        } else {
            // Google Play services wasn't able to provide an intent for some
            // error types, so we show the default Google Play services error
            // dialog which may still start an intent on our behalf if the
            // user can resolve the issue.
            createErrorDialog().show();
        }
    }


    private void checkServerAuthConfiguration() {
        // Check that the server URL is configured before allowing this box to
        // be unchecked
        if ("WEB_CLIENT_ID".equals(WEB_CLIENT_ID) ||
                "SERVER_BASE_URL".equals(SERVER_BASE_URL)) {
            Log.w("", "WEB_CLIENT_ID or SERVER_BASE_URL configured incorrectly.");
            Dialog dialog = new AlertDialog.Builder(this)
                    .setMessage("error")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();

            dialog.show();
        }
    }

    private Dialog createErrorDialog() {
        if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    mSignInError,
                    this,
                    RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.e("", "Google Play services resolution cancelled");
                            mSignInProgress = STATE_DEFAULT;
                        }
                    });
        } else {
            return new AlertDialog.Builder(this)
                    .setMessage("error")
                    .setPositiveButton(R.string.close_app,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e("", "Google Play services error could not be "
                                            + "resolved: " + mSignInError);
                                    mSignInProgress = STATE_DEFAULT;
                                }
                            }).create();
        }
    }

    @Override
    public CheckResult onCheckServerAuthorization(String s, Set<Scope> set) {

        if (!mServerHasToken) {
            // Server does not have a valid refresh token, so request a new
            // auth code which can be exchanged for one.  This will cause the user to see the
            // consent dialog and be prompted to grant offline access. This callback occurs on a
            // background thread so it is OK to do synchronous network access.

            // Ask the server which scopes it would like to have for offline access.  This
            // can be distinct from the scopes granted to the client.  By getting these values
            // from the server, you can change your server's permissions without needing to
            // recompile the client application.
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(SELECT_SCOPES_URL);
            HashSet<Scope> serverScopeSet = new HashSet<Scope>();

            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(httpResponse.getEntity());

                if (responseCode == 200) {
                    String[] scopeStrings = responseBody.split(" ");
                    for (String scope : scopeStrings) {
                        Log.i("", "Server Scope: " + scope);
                        serverScopeSet.add(new Scope(scope));
                    }
                } else {
                    Log.e("", "Error in getting server scopes: " + responseCode);
                }

            } catch (ClientProtocolException e) {
                Log.e("", "Error in getting server scopes.", e);
            } catch (IOException e) {
                Log.e("", "Error in getting server scopes.", e);
            }

            // This tells GoogleApiClient that the server needs a new serverAuthCode with
            // access to the scopes in serverScopeSet.  Note that we are not asking the server
            // if it already has such a token because this is a sample application.  In reality,
            // you should only do this on the first user sign-in or if the server loses or deletes
            // the refresh token.
            return CheckResult.newAuthRequiredResult(serverScopeSet);
        } else {
            // Server already has a valid refresh token with the correct scopes, no need to
            // ask the user for offline access again.
            return CheckResult.newAuthNotRequiredResult();
        }
    }

    @Override
    public boolean onUploadServerAuthCode(String idToken, String serverAuthCode) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(EXCHANGE_TOKEN_URL);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("serverAuthCode", serverAuthCode));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = EntityUtils.toString(response.getEntity());
            Log.i("", "Code: " + statusCode);
            Log.i("", "Resp: " + responseBody);

            // Show Toast on UI Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, responseBody, Toast.LENGTH_LONG).show();
                }
            });
            return (statusCode == 200);
        } catch (ClientProtocolException e) {
            Log.e("", "Error in auth code exchange.", e);
            return false;
        } catch (IOException e) {
            Log.e("", "Error in auth code exchange.", e);
            return false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            mCirclesList.clear();
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    mCirclesList.add(personBuffer.get(i).getDisplayName());
                }
            } finally {
                personBuffer.close();
            }
            mCirclesAdapter.notifyDataSetChanged();
        } else {
            Log.e("", "Error requesting visible circles: " + peopleData.getStatus());
        }
    }
}