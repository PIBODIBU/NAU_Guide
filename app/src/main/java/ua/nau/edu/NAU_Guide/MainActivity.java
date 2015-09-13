package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseNavigationDrawerActivity {
    private SearchView searchView;
    private InputMethodManager inputMethodManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get system services
        inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //

        getDrawer(); // Load Navigation Drawer

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), SplashActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    public void toastShowLong (String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    public void toastShowShort (String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        getCurrentSelection();
        super.onResume();
    }
}

/*
    <item name="colorPrimary">@color/material_drawer_primary</item>
    <item name="colorPrimaryDark">@color/material_drawer_primary_dark</item>
    <item name="colorAccent">@color/material_drawer_accent</item>
    <!-- MaterialDrawer specific values -->
    <item name="material_drawer_background">@color/material_drawer_background</item>
    <item name="material_drawer_icons">@color/material_drawer_icons</item>
    <item name="material_drawer_primary_text">@color/material_drawer_primary_text</item>
    <item name="material_drawer_primary_icon">@color/material_drawer_primary_icon</item>
    <item name="material_drawer_secondary_text">@color/material_drawer_secondary_text</item>
    <item name="material_drawer_hint_text">@color/material_drawer_hint_text</item>
    <item name="material_drawer_divider">@color/material_drawer_divider</item>
    <item name="material_drawer_selected">@color/material_drawer_selected</item>
    <item name="material_drawer_selected_text">@color/material_drawer_selected_text</item>
    <item name="material_drawer_header_selection_text">@color/material_drawer_header_selection_text</item>
*/