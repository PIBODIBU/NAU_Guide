package ua.nau.edu.Adapters.UserProfileAdapter.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.API.APIStrings;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.NAU_Guide.MainActivity;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.NAU_Guide.UserProfileActivity;
import ua.nau.edu.Systems.CircleTransform;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class FragmentInfo extends Fragment {
    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();

    private static final String URL_GETMYPAGE = "http://nauguide.esy.es/include/getMyPage.php";
    private static final String URL_GETPAGE = "http://nauguide.esy.es/include/getLector.php";

    private static final String TAG = "FragmentInfo";
    private boolean isBigAvatarLoadingStarted = false;

    private SharedPreferences settings = null;

    private View FragmentView;
    private CoordinatorLayout coordinatorLayout;
    private UserProfileActivity supportActivity;

    private SharedPrefUtils sharedPrefUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        supportActivity = (UserProfileActivity) getActivity();
        FragmentView = inflater.inflate(R.layout.fragment_user_info, container, false);

        sharedPrefUtils = new SharedPrefUtils(
                supportActivity.getSharedPreferences(EnumSharedPreferences.APP_PREFERENCES.toString(), supportActivity.MODE_PRIVATE),
                supportActivity.getSharedPreferences(EnumSharedPreferencesVK.VK_PREFERENCES.toString(), supportActivity.MODE_PRIVATE));
        coordinatorLayout = (CoordinatorLayout) supportActivity.findViewById(R.id.coordinatorLayout);

        switch (getActivity().getIntent().getExtras().getString("action", "")) {
            case "getMyPage": {
                getMyPage();
                break;
            }
            case "getPage": {
                getPage(getActivity().getIntent().getExtras().getString("uniqueId", ""));
                break;
            }
        }

        return FragmentView;
    }

    private void getMyPage() {
        new AsyncTask<String, Void, JSONObject>() {

            ImageView avatarSmall;
            ImageView avatarBig;

            MaterialDialog dialog = APIDialogs.ProgressDialogs.loadingCancelable(supportActivity, new APIDialogs.ProgressDialogs.ProgressDialogCallbackInterface() {
                @Override
                public void onCancel() {
                    dialog.dismiss();
                    startActivity(new Intent(supportActivity, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    supportActivity.finish();
                }
            });

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.show();

                // Set up Views
                avatarSmall = (ImageView) supportActivity.findViewById(R.id.imageViewPhoto);
                avatarBig = (ImageView) supportActivity.findViewById(R.id.reveal_image);

                // Loading default avatar
                Picasso.with(supportActivity)
                        .load(R.drawable.avatar_default)
                        .transform(new CircleTransform())
                        .into(avatarSmall);
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                APIHTTPUtils apiUtils = new APIHTTPUtils();
                HashMap<String, String> data = new HashMap<>();
                data.put("token", params[0]);

                String response = apiUtils.sendPostRequestWithParams(URL_GETMYPAGE, data);
                if (response.equalsIgnoreCase("error_connection")) {
                    Log.e(TAG, "No Internet avalible");
                    supportActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIDialogs.AlertDialogs.internetConnectionErrorWithExit(supportActivity);
                        }
                    });
                } else if (response.equalsIgnoreCase("error_server")) {
                    supportActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIDialogs.AlertDialogs.serverConnectionErrorWithExit(supportActivity);
                        }
                    });
                    Log.e(TAG, "Server error. Response code != 200");
                    return null;
                } else {
                    try {
                        final JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                            return jsonObject;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Could not parse malformed JSON: \"" + response + "\"");
                        return null;
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                if (result != null) {
                    try {
                        final String PHOTO_URL = result.getString(APIStrings.ResponseKeys.PageLoading.PHOTO_URL);

                        TextView textViewName = (TextView) supportActivity.findViewById(R.id.textViewName);
                        ExpandableTextView expTv1 = (ExpandableTextView) supportActivity.findViewById(R.id.bio_text_expand);

                        /*
                            Setting Up NAME
                        */
                        textViewName.setText(result.getString(APIStrings.ResponseKeys.PageLoading.NAME));

                        /*
                            Setting Up BIOGRAPHY
                        */
                        CardView cardBio = (CardView) supportActivity.findViewById(R.id.card_bio);
                        if (!result.getString(APIStrings.ResponseKeys.PageLoading.BIOGRAPHY).equals("")) {
                            cardBio.setVisibility(View.VISIBLE);
                            expTv1.setText(result.getString(APIStrings.ResponseKeys.PageLoading.BIOGRAPHY));
                        }

                        /*
                            Setting Up USER_PHOTO
                        */
                        if (!PHOTO_URL.equals("")) {
                            Picasso.with(supportActivity)
                                    .load(Uri.parse(PHOTO_URL))
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .transform(new CircleTransform())
                                    .into(avatarSmall);
                            avatarSmall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "avatarSmall clicked");

                                    AvatarDialog avatarDialog = new AvatarDialog();
                                    avatarDialog.init(supportActivity, PHOTO_URL);
                                    avatarDialog.show(getActivity().getSupportFragmentManager().beginTransaction(), TAG);
                                }
                            });
                        }

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse malformed JSON: " + result);
                    }

                    dialog.dismiss();

                } else {
                    dialog.dismiss();
                }
            }
        }.execute(sharedPrefUtils.getToken());
    }

    private void getPage(final String UniqueId) {
        new AsyncTask<String, Void, JSONObject>() {

            ImageView avatarSmall;

            MaterialDialog dialog = APIDialogs.ProgressDialogs.loadingCancelable(supportActivity, new APIDialogs.ProgressDialogs.ProgressDialogCallbackInterface() {
                @Override
                public void onCancel() {
                    Log.d(TAG, "Dialog canceled");
                    dialog.dismiss();
                    supportActivity.finish();
                }
            });

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.show();

                // Set up Views
                avatarSmall = (ImageView) supportActivity.findViewById(R.id.imageViewPhoto);

                // Loading default avatar
                Picasso.with(supportActivity)
                        .load(R.drawable.avatar_default)
                        .transform(new CircleTransform())
                        .into(avatarSmall);
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                APIHTTPUtils apiUtils = new APIHTTPUtils();
                HashMap<String, String> data = new HashMap<>();
                data.put("unique_id", params[0]);

                String response = apiUtils.sendPostRequestWithParams(URL_GETPAGE, data);

                if (response.equalsIgnoreCase("error_connection")) {
                    supportActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIDialogs.AlertDialogs.internetConnectionErrorWithExit(supportActivity);
                        }
                    });
                } else if (response.equalsIgnoreCase("error_server")) {
                    Log.e(TAG, "Server error. Response code != 200");
                    supportActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            APIDialogs.AlertDialogs.serverConnectionErrorWithExit(supportActivity);
                        }
                    });
                    return null;
                } else {

                    try {
                        final JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                            return jsonObject;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Could not parse malformed JSON: \"" + response + "\"");
                        return null;
                    }

                }

                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                if (result != null) {
                    try {
                        final String PHOTO_URL = result.getString(APIStrings.ResponseKeys.PageLoading.PHOTO_URL);

                        TextView textViewName = (TextView) supportActivity.findViewById(R.id.textViewName);
                        ExpandableTextView expTv1 = (ExpandableTextView) supportActivity.findViewById(R.id.bio_text_expand);

                        /*
                            Setting Up NAME
                        */
                        textViewName.setText(result.getString(APIStrings.ResponseKeys.PageLoading.NAME));

                        /*
                            Setting Up BIOGRAPHY
                        */
                        CardView cardBio = (CardView) supportActivity.findViewById(R.id.card_bio);
                        if (!result.getString(APIStrings.ResponseKeys.PageLoading.BIOGRAPHY).equals("")) {
                            cardBio.setVisibility(View.VISIBLE);
                            expTv1.setText(result.getString(APIStrings.ResponseKeys.PageLoading.BIOGRAPHY));
                        }

                        /*
                            Setting Up USER_PHOTO
                        */
                        if (!PHOTO_URL.equals("")) {
                            Picasso.with(supportActivity)
                                    .load(Uri.parse(PHOTO_URL))
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .transform(new CircleTransform())
                                    .into(avatarSmall);

                            avatarSmall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "avatarSmall clicked");

                                    AvatarDialog avatarDialog = new AvatarDialog();
                                    avatarDialog.init(supportActivity, PHOTO_URL);
                                    avatarDialog.show(getActivity().getSupportFragmentManager().beginTransaction(), TAG);
                                }
                            });
                        }

                        /*
                            Checking if loading page is my page and set Drawer selection to my page if true
                         */
                        if (result.getString("unique_id").equals(sharedPrefUtils.getUniqueId())) {
                            supportActivity.setDrawerToMyPage();
                        }

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse malformed JSON: " + result);
                    }

                    dialog.dismiss();

                } else {
                    dialog.dismiss();
                }

            }
        }.execute(UniqueId);
    }

    public static class AvatarDialog extends DialogFragment {

        private static final String TAG = "FragmentInfo";

        private AlertDialog.Builder dialogBuilder;
        private ImageView avatarBig;

        private Activity activity;
        private String PHOTO_URL;

        public void init(Activity activity, String PHOTO_URL) {
            this.activity = activity;
            this.PHOTO_URL = PHOTO_URL;
        }

        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
            dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View mainLayout = inflater.inflate(R.layout.dialog_avatar, null);

            avatarBig = (ImageView) mainLayout.findViewById(R.id.reveal_image);

            dialogBuilder
                    .setView(mainLayout);

            setCancelable(true);

            AlertDialog dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            if (PHOTO_URL != null && !PHOTO_URL.equals("")) {
                Picasso
                        .with(activity)
                        .load(Uri.parse(PHOTO_URL))
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(avatarBig);
            }

            Log.d("FragmentInfo", "Dialog created");
            return dialog;
        }

    }

}