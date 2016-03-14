package ua.nau.edu.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ServiceCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.gc.materialdesign.widgets.ProgressDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.API.APIUrl;
import ua.nau.edu.NAU_Guide.R;

public class NewAppVersionDialog extends DialogFragment {

    private static final String TAG = "NewAppVersion";
    private AlertDialog.Builder dialogBuilder;

    private Activity activity;
    private Context context;
    private String version;
    private final int NOTIFICATION_ID = 100001;

    private ProgressDialog progressDialog;

    public void init(Activity activity, Context context, String version) {
        this.version = version;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        dialogBuilder
                .setTitle("Внимание")
                .setMessage("Новая версия приложения доступна (" + version + "). Обновить сейчас?")
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        UpdateUtils updateUtils = new UpdateUtils(context);
                        updateUtils.execute();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        // Call method setCancelable() from AppCompatDialogFragment instead of AlertDialog.Builder
        setCancelable(false);

        // Creating AlertDialog from AlertDialog.Builder
        final AlertDialog dialog = dialogBuilder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogArg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAppPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            }
        });

        return dialog;
    }

    public class UpdateUtils extends AsyncTask<Void, Integer, Void> {

        private NotificationManager notificationManager;
        private NotificationCompat.Builder builder;
        private Context context;
        private final APIHTTPUtils httpUtils = new APIHTTPUtils();

        private MaterialProgressBar progressBar;

        public UpdateUtils(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            Dialog d = new Dialog(context);
            d.setTitle("Загрузка...");
            d.setCancelable(false);
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_update, null);
            progressBar = (MaterialProgressBar) rootView.findViewById(R.id.progressBar);
            progressBar.setMax(100);
            progressBar.setProgress(0);
            d.setContentView(rootView);
            d.show();
        }

        @Override
        protected Void doInBackground(Void... arg) {
            /*notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("NAU Guide")
                    .setContentText("Загрузка приложения")
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_autorenew_white_24dp)
                    .setAutoCancel(false);*/

            try {
                HashMap<String, String> requestGetUrlParams = new HashMap<>();
                requestGetUrlParams.put("action", "getUpdateUrl");
                String response = httpUtils.sendPostRequestWithParams(APIUrl.RequestUrl.API, requestGetUrlParams);
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

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
            /*builder.setProgress(100, progress[0], false);
            notificationManager.notify(NOTIFICATION_ID, builder.build());*/
            progressBar.setProgress(progress[0]);
            Log.d(TAG, "Downloading progress: " + progress[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /*builder.setContentText("Загрузка завершена").setProgress(0, 0, false);
            notificationManager.notify(NOTIFICATION_ID, builder.build());*/
            progressDialog.dismiss();
        }
    }
}
