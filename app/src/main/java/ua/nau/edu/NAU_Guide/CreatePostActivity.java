package ua.nau.edu.NAU_Guide;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APIPostBuilder;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class CreatePostActivity extends BaseToolbarActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/makePost.php";
    private static final String TAG = "CreatePostActivity";
    private static int maxMessageLength = 300;

    private EditText messageEditText;
    private APIPostBuilder apiPostBuilder;

    private SharedPrefUtils sharedPrefUtils;

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
        apiPostBuilder = new APIPostBuilder()
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
                String message = messageEditText.getText().toString();
                if (message.equals("")) {
                    APIDialogs.AlertDialogs.emptyString(this);
                } else {
                    if (message.length() > maxMessageLength) {
                        APIDialogs.AlertDialogs.tooLongMeassage(this);
                    } else {
                        apiPostBuilder.postMessage(REQUEST_URL, sharedPrefUtils.getToken(), message);
                    }
                }
                break;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
