package ua.nau.edu.NAU_Guide;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APICreateBuilder;
import ua.nau.edu.API.APIStrings;
import ua.nau.edu.API.APIValues;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class CreatePostActivity extends BaseToolbarActivity {

    private static final String TAG = "CreatePostActivity";

    private EditText messageEditText;
    private APICreateBuilder apiCreateBuilder;

    private SharedPrefUtils sharedPrefUtils;

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        getToolbar();
        setMenuId(R.menu.menu_create_post);

        setUpAPI();

        sharedPrefUtils = new SharedPrefUtils(getSharedPreferences(sharedPrefUtils.APP_PREFERENCES, MODE_PRIVATE),
                getSharedPreferences(sharedPrefUtils.VK_PREFERENCES, MainActivity.MODE_PRIVATE));

        messageEditText = (EditText) findViewById(R.id.post_message);
    }

    private void setUpAPI() {
        apiCreateBuilder = new APICreateBuilder()
                .withActivity(this)
                .withContext(this)
                .withLoadingDialog(true)
                .withTag(TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
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

    private boolean isMessageEmpty() {
        return message.equals("");
    }

    private boolean isValidLength() {
        return message.length() <= APIValues.maxMessageLength;
    }
}