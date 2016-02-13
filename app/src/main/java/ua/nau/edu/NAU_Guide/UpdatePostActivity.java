package ua.nau.edu.NAU_Guide;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APIStrings;
import ua.nau.edu.API.APIUpdateBuilder;
import ua.nau.edu.API.APIValues;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class UpdatePostActivity extends BaseToolbarActivity {

    private static final String TAG = "UpdatePostActivity";

    private EditText messageEditText;
    private APIUpdateBuilder apiUpdateBuilder;

    private SharedPrefUtils sharedPrefUtils;

    private String messageNew; // New message
    private String messageToEdit = ""; // Old message
    private int postId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        getToolbar();
        setMenuId(R.menu.menu_create_post);

        setUpAPI();

        sharedPrefUtils = new SharedPrefUtils(getSharedPreferences(sharedPrefUtils.APP_PREFERENCES, MODE_PRIVATE),
                getSharedPreferences(sharedPrefUtils.VK_PREFERENCES, MainActivity.MODE_PRIVATE));

        messageEditText = (EditText) findViewById(R.id.update_message);

        messageToEdit = getIntent().getStringExtra("message");
        postId = getIntent().getIntExtra("postId", -1);

        if (!messageToEdit.equals("")) {
            messageEditText.setText(messageToEdit);
        }
    }

    private void setUpAPI() {
        apiUpdateBuilder = new APIUpdateBuilder()
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
                messageNew = messageEditText.getText().toString().trim();
                if (isMessageEmpty()) {
                    APIDialogs.AlertDialogs.emptyString(this);
                } else {
                    if (!isValidLength()) {
                        APIDialogs.AlertDialogs.tooLongMeassage(this);
                    } else {
                        if (postId != -1) {
                            apiUpdateBuilder.updateMessage(APIStrings.RequestUrl.UPDATE_POST, sharedPrefUtils.getToken(), messageNew, postId);
                        } else {
                            Toast.makeText(this, "Bad post ID", Toast.LENGTH_LONG).show();
                        }
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
        return messageNew.equals("");
    }

    private boolean isValidLength() {
        return messageNew.length() <= APIValues.maxMessageLength;
    }
}
