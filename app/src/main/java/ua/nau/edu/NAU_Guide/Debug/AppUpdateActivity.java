package ua.nau.edu.NAU_Guide.Debug;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.NAU_Guide.BaseNavigationDrawerActivity;
import ua.nau.edu.NAU_Guide.R;

public class AppUpdateActivity extends BaseNavigationDrawerActivity {
    private final String TAG = getClass().getSimpleName();

    private static final String URL_UPDATE_GET_VERSION = "http://nauguide.esy.es/include/api.php";
    private static final String URL_UPDATE_GET_APP_URL = "http://nauguide.esy.es/include/api.php";
    private static final String BUTTON_CHECK = "Check for update";
    private static final String BUTTON_UPDATE = "UPDATE";
    private static final String BUTTON_CANCEL = "CANCEL";

    private TextView versionCurrentTV;
    private TextView versionAvailableTV;
    private AppCompatButton button;
    private MaterialProgressBar progressBarDet;
    private MaterialProgressBar progressBarIndet;

    private UpdateUtils updateUtils;
    private final CheckUtils checkUtils = new CheckUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);

        getDrawer(
                "AppUpdateActivity",
                "AppUpdateActivity@email.com");

        versionCurrentTV = (TextView) findViewById(R.id.version_current);
        versionAvailableTV = (TextView) findViewById(R.id.version_available);
        button = (AppCompatButton) findViewById(R.id.update_check);
        progressBarDet = (MaterialProgressBar) findViewById(R.id.progressBar_det);
        progressBarIndet = (MaterialProgressBar) findViewById(R.id.progressBar_indet);

        String currentVersion = versionCurrentTV.getText() + getString(R.string.app_version);
        versionCurrentTV.setText(currentVersion);

        button.setText(BUTTON_CHECK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonTitle = button.getText().toString();

                if (buttonTitle.equals(BUTTON_CHECK)) {
                    checkUtils.execute();
                } else if (buttonTitle.equals(BUTTON_UPDATE)) {
                    updateUtils = new UpdateUtils(AppUpdateActivity.this);
                    updateUtils.execute();
                } else if (buttonTitle.equals(BUTTON_CANCEL)) {
                    updateUtils.cancel(true);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (updateUtils != null) {
            if (!updateUtils.isCancelled()) {
                updateUtils.cancel(true);
                Log.d(TAG, "onDestroy() -> Canceling UpdateUtils");
            }
        }
        if (!checkUtils.isCancelled()) {
            checkUtils.cancel(true);
            Log.d(TAG, "onDestroy() -> Canceling CheckUtils");
        }
        super.onDestroy();
    }

    public class CheckUtils extends AsyncTask<Void, Void, String> {

        private final APIHTTPUtils httpUtils = new APIHTTPUtils();

        @Override
        protected void onPreExecute() {
            progressBarIndet.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> requestGetVersionParams = new HashMap<>();
            requestGetVersionParams.put("action", "getVersion");

            return httpUtils.sendPostRequestWithParams(URL_UPDATE_GET_VERSION, requestGetVersionParams);
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                Log.d(TAG, "CheckUtils -> Server response:\n" + response);

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                    Log.d(TAG, "Available version: " + jsonObject.getString("version"));

                    String availableVersion = versionAvailableTV.getText() + jsonObject.getString("version");

                    versionAvailableTV.setText(availableVersion);
                    progressBarIndet.setVisibility(View.INVISIBLE);
                    button.setText(BUTTON_UPDATE);

                } else {
                    progressBarIndet.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "Error while sending post request. Error message: " + jsonObject.getString("error_msg"));
                }
            } catch (JSONException ex) {
                Log.e(TAG, "onCreate() -> onPostExecute() -> ", ex);
            }
        }
    }

    public class UpdateUtils extends AsyncTask<Void, Integer, Void> {

        private Context context;
        private final APIHTTPUtils httpUtils = new APIHTTPUtils();

        public UpdateUtils(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressBarDet.setVisibility(View.VISIBLE);
            button.setText(BUTTON_CANCEL);
        }

        @Override
        protected Void doInBackground(Void... arg) {
            try {
                HashMap<String, String> requestGetUrlParams = new HashMap<>();
                requestGetUrlParams.put("action", "getUpdateUrl");
                String response = httpUtils.sendPostRequestWithParams(URL_UPDATE_GET_APP_URL, requestGetUrlParams);
                Log.d(TAG, "UpdateUtils -> Server response: " + response);
                JSONObject jsonObject = new JSONObject(response);
                String REQUEST_URL = jsonObject.getString("url");

                Log.d(TAG, "REQUEST_URL: " + REQUEST_URL);
                URL url = new URL(REQUEST_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                String PATH = Environment.getExternalStorageDirectory().getPath();
                Log.d(TAG, "Loading path: " + PATH);
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "NAU Guide.apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }

                FileOutputStream outputStream = new FileOutputStream(outputFile);
                InputStream inputStream = urlConnection.getInputStream();

                byte[] buffer = new byte[1024];
                int count = 0;
                int total = 0;
                int fileLength = urlConnection.getContentLength();
                Log.d(TAG, "File length: " + fileLength);
                while ((count = inputStream.read(buffer)) != -1) {
                    if (isCancelled()) {
                        Log.e(TAG, "Download task is canceled");
                        inputStream.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setText(BUTTON_UPDATE);
                                progressBarDet.setVisibility(View.INVISIBLE);
                            }
                        });
                        return null;
                    }
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    outputStream.write(buffer, 0, count);
                }
                outputStream.close();
                inputStream.close();

                Log.d(TAG, "Start Activity from Intent...\nPath: " + outputFile.getPath() + "\nName: " + outputFile.getName());
                context.startActivity(new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.fromFile(new File(outputFile.getPath())), "application/vnd.android.package-archive")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e) {
                Log.e(TAG, "CheckUtils -> doInBackground() -> ", e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBarDet.setProgress(progress[0]);
            Log.d(TAG, "Downloading progress: " + progress[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBarDet.setVisibility(View.INVISIBLE);
            button.setText(BUTTON_CHECK);
        }
    }
}
