package ua.nau.edu.Adapters.UserProfileAdapter.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import ua.nau.edu.API.APIUrl;
import ua.nau.edu.API.APIValues;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.NAU_Guide.UserProfileActivity;
import ua.nau.edu.Support.Picasso.CircleTransform;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.Support.System.Utils;
import ua.nau.edu.Support.View.CircleImageView;

public class FragmentInfo extends Fragment {

    private static final String TAG = "FragmentInfo";

    private View FragmentView;
    private CoordinatorLayout coordinatorLayout;
    private UserProfileActivity supportActivity;

    private SharedPrefUtils sharedPrefUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        supportActivity = (UserProfileActivity) getActivity();
        FragmentView = inflater.inflate(R.layout.fragment_user_info, container, false);

        sharedPrefUtils = new SharedPrefUtils(supportActivity);
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

            CircleImageView avatarSmall;

            MaterialDialog dialog = APIDialogs.ProgressDialogs.loading(supportActivity);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.show();

                // Set up Views
                avatarSmall = (CircleImageView) supportActivity.findViewById(R.id.imageViewPhoto);

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

                String response = apiUtils.sendPostRequestWithParams(APIUrl.RequestUrl.GET_MYPAGE, data);
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
                        final String PHOTO_URL = result.getString(APIUrl.ResponseKeys.PageLoading.PHOTO_URL);

                        ExpandableTextView expTv1 = (ExpandableTextView) supportActivity.findViewById(R.id.bio_text_expand);
                        TextView textViewName = (TextView) supportActivity.findViewById(R.id.expandable_text);

                        /*
                            Setting Up NAME
                        */
                        //textViewName.setText(result.getString(APIStrings.ResponseKeys.PageLoading.NAME));
                        String name = result.getString(APIUrl.ResponseKeys.PageLoading.NAME);
                        supportActivity.collapsingToolbarLayout.setTitle(name);

                        /*
                            Setting Up BIOGRAPHY
                        */
                        CardView cardBio = (CardView) supportActivity.findViewById(R.id.card_bio);
                        if (!result.getString(APIUrl.ResponseKeys.PageLoading.BIOGRAPHY).equals("")) {
                            cardBio.setVisibility(View.VISIBLE);
                            expTv1.setText(result.getString(APIUrl.ResponseKeys.PageLoading.BIOGRAPHY));
                            if (textViewName.getLineCount() < APIValues.maxLinesBeforeExpand) {
                                textViewName.setPadding(0, 0, 0, (int) Utils.convertDpToPixel(8, supportActivity));
                            }
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

                    } catch (Exception ex) {
                        Log.e(TAG, "getMyPage() ->  onPostExecute() -> ", ex);
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

            CircleImageView avatarSmall;

            MaterialDialog dialog = APIDialogs.ProgressDialogs.loading(supportActivity);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.show();

                // Set up Views
                avatarSmall = (CircleImageView) supportActivity.findViewById(R.id.imageViewPhoto);

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

                String response = apiUtils.sendPostRequestWithParams(APIUrl.RequestUrl.GET_USER_PAGE, data);

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
                        final String PHOTO_URL = result.getString(APIUrl.ResponseKeys.PageLoading.PHOTO_URL);

                        TextView textViewName = (TextView) supportActivity.findViewById(R.id.textViewName);
                        ExpandableTextView expTv1 = (ExpandableTextView) supportActivity.findViewById(R.id.bio_text_expand);

                        /*
                            Setting Up NAME
                        */
                        //textViewName.setText(result.getString(APIStrings.ResponseKeys.PageLoading.NAME));
                        String name = result.getString(APIUrl.ResponseKeys.PageLoading.NAME);
                        supportActivity.collapsingToolbarLayout.setTitle(name);

                        /*
                            Setting Up BIOGRAPHY
                        */
                        CardView cardBio = (CardView) supportActivity.findViewById(R.id.card_bio);
                        if (!result.getString(APIUrl.ResponseKeys.PageLoading.BIOGRAPHY).equals("")) {
                            cardBio.setVisibility(View.VISIBLE);
                            expTv1.setText(result.getString(APIUrl.ResponseKeys.PageLoading.BIOGRAPHY));
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

        private static final String TAG = "AvatarDialog";

        private AlertDialog.Builder dialogBuilder;
        private AlertDialog dialog;
        private ImageView avatarBig;

        private Activity activity;
        private String PHOTO_URL;

        public void init(Activity activity, String PHOTO_URL) {
            this.activity = activity;
            this.PHOTO_URL = PHOTO_URL;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                PHOTO_URL = savedInstanceState.getString("PHOTO_URL");
            }

            dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View mainLayout = inflater.inflate(R.layout.dialog_avatar, null);

            avatarBig = (ImageView) mainLayout.findViewById(R.id.reveal_image);

            dialogBuilder
                    .setCancelable(true)
                    .setView(mainLayout);

            dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            if (PHOTO_URL != null && !PHOTO_URL.equals("")) {
                Log.d(TAG, "Loading big avatar...");
                Picasso
                        .with(activity)
                        .load(Uri.parse(PHOTO_URL))
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(avatarBig);
            }

            Log.d(TAG, "Dialog created");
            return dialog;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putString("PHOTO_URL", PHOTO_URL);

            avatarBig.buildDrawingCache();
            Bitmap avatarBigBitmap = avatarBig.getDrawingCache();
            outState.putParcelable("BIG_IMAGE", avatarBigBitmap);

            super.onSaveInstanceState(outState);
        }
    }

}