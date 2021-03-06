package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class BaseToolbarActivity extends AppCompatActivity {

    private static final String TAG = "BaseToolbarActivity";

    private InputMethodManager MethodManager;
    private SharedPrefUtils sharedPrefUtils;
    private TextView title;

    private int menuId = -1;

    public BaseToolbarActivity() {
    }

    public void getToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.toolbar_title);

        try {
            title.setText(getSupportActionBar().getTitle().toString());
        } catch (Exception ex) {
            Log.e(TAG, "getToolbar() -> ", ex);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU: {
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    super.onBackPressed();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setToolbarTitle(String titleText) {
        try {
            title.setText(titleText);
        } catch (Exception ex) {
            Log.e(TAG, "setToolbarTitle() -> ", ex);
        }
    }

    public void setMenuId(int menu) {
        this.menuId = menu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (menuId != -1)
            inflater.inflate(menuId, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        sharedPrefUtils = new SharedPrefUtils(this);
    }
}
