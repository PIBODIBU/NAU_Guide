package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;

public class BaseToolbarActivity extends AppCompatActivity {
    private InputMethodManager MethodManager = null;
    private SharedPreferences sharedPrefs = null;
    private SharedPreferences sharedPrefsVK = null;

    BaseToolbarActivity() {
    }

    public void getToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String text = getSupportActionBar().getTitle().toString();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(text);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU: {
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    //super.onKeyDown(KeyEvent.KEYCODE_BACK, null);
                    super.onBackPressed();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(BaseToolbarActivity.this, TEXT, Toast.LENGTH_SHORT).show();
    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        /*sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        sharedPrefsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);*/
    }
}
