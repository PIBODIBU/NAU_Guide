package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APICreateBuilder;
import ua.nau.edu.API.APIRefreshBuilder;
import ua.nau.edu.API.APIStrings;
import ua.nau.edu.API.APIValues;
import ua.nau.edu.RecyclerViews.NewsActivity.NewsDataModel;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class CreatePostActivity extends BaseToolbarActivity {

    public static final int REQUEST_CODE = 1;
    private static final String TAG = "CreatePostActivity";

    private EditText messageEditText;
    private APICreateBuilder apiCreateBuilder;

    private SharedPrefUtils sharedPrefUtils;
    private InputMethodManager inputMethodManager;

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        getToolbar();
        setMenuId(R.menu.menu_create_post);

        setUpAPI();

        sharedPrefUtils = new SharedPrefUtils(this);
        inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        messageEditText = (EditText) findViewById(R.id.post_message);
    }

    private void setUpAPI() {
        apiCreateBuilder = new APICreateBuilder()
                .withActivity(this)
                .withContext(this)
                .withLoadingDialog(true)
                .withTag(TAG);

        apiCreateBuilder.setOnResultListener(new APICreateBuilder.OnResultListener() {
            @Override
            public void onPosted(String message) {
                setResult(APIValues.RESULT_OK);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                setResult(APIValues.RESULT_ERROR);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                actionBeforeExit();
                break;
            }
            case R.id.create_post: {
                message = messageEditText.getText().toString().trim();
                if (isMessageEmpty()) {
                    APIDialogs.AlertDialogs.emptyString(this);
                } else {
                    if (!isValidLength()) {
                        APIDialogs.AlertDialogs.tooLongMeassage(this);
                    } else {
                        apiCreateBuilder.postMessage(APIStrings.RequestUrl.MAKE_POST, sharedPrefUtils.getToken(), message);
                    }
                }
                break;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {

                Log.d(TAG, "onKeyDown() called");
                actionBeforeExit();

                return false;
            }

        }

        return super.onKeyDown(keyCode, event);
    }

    private void actionBeforeExit() {
        final AlertDialog closeDialog = new AlertDialog.Builder(this)
                .setTitle("Внимание")
                .setMessage("Все данные будут утеряны. Вы уверены, что хотите выйти?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(APIValues.RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        closeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                closeDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(CreatePostActivity.this, R.color.colorAppPrimary));
                closeDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(CreatePostActivity.this, R.color.colorAppPrimary));
            }
        });

        closeDialog.show();
    }

    private boolean isMessageEmpty() {
        return message.equals("");
    }

    private boolean isValidLength() {
        return message.length() <= APIValues.maxMessageLength;
    }


}
