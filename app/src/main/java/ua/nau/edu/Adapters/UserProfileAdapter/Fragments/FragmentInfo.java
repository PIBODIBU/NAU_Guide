package ua.nau.edu.Adapters.UserProfileAdapter.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import ua.nau.edu.API.APIStrings;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.NAU_Guide.UserProfileActivity;
import ua.nau.edu.Systems.CircleTransform;
import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class FragmentInfo extends Fragment {
    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();

    private static final String URL_GETMYPAGE = "http://nauguide.esy.es/include/getMyPage.php";
    private static final String URL_GETPAGE = "http://nauguide.esy.es/include/getLector.php";
    private static final String TAG = "FragmentInfo";

    private SharedPreferences settings = null;

    private View FragmentView;
    private TextView textViewName;
    private ImageView imageViewPhoto;
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

            MaterialDialog dialog = APIDialogs.ProgressDialogs.loading(supportActivity);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.show();
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
                        TextView textViewName = (TextView) supportActivity.findViewById(R.id.textViewName);
                        ImageView imageViewPhoto = (ImageView) supportActivity.findViewById(R.id.imageViewPhoto);
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
                        if (!result.getString(APIStrings.ResponseKeys.PageLoading.PHOTO_URL).equals("")) {
                            Picasso.with(supportActivity)
                                    .load(Uri.parse(result.getString(APIStrings.ResponseKeys.PageLoading.PHOTO_URL))).transform(new CircleTransform()).into(imageViewPhoto);
                        } else {
                            Picasso.with(supportActivity)
                                    .load(Uri.parse(APIStrings.ImageUrl.DEFAULT_AVATAR)).transform(new CircleTransform()).into(imageViewPhoto);
                        }

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse malformed JSON: " + result);
                    }

                    dialog.dismiss();
                }
            }
        }.execute(sharedPrefUtils.getToken());
    }

    private void getPage(String UniqueId) {
        new AsyncTask<String, Void, JSONObject>() {

            MaterialDialog dialog = APIDialogs.ProgressDialogs.loading(supportActivity);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.show();
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
                        TextView textViewName = (TextView) supportActivity.findViewById(R.id.textViewName);
                        ImageView imageViewPhoto = (ImageView) supportActivity.findViewById(R.id.imageViewPhoto);
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
                        if (!result.getString(APIStrings.ResponseKeys.PageLoading.PHOTO_URL).equals("")) {
                            Picasso.with(supportActivity)
                                    .load(Uri.parse(result.getString(APIStrings.ResponseKeys.PageLoading.PHOTO_URL))).transform(new CircleTransform()).into(imageViewPhoto);
                        } else {
                            Picasso.with(supportActivity)
                                    .load(Uri.parse(APIStrings.ImageUrl.DEFAULT_AVATAR)).transform(new CircleTransform()).into(imageViewPhoto);
                        }

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse malformed JSON: " + result);
                    }

                    dialog.dismiss();
                }
            }
        }.execute(UniqueId);
    }

}