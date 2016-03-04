package ua.nau.edu.NAU_Guide;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import ua.nau.edu.Dialogs.Settings.DebugListDialog;
import ua.nau.edu.Dialogs.Settings.MapLayerDialog;
import ua.nau.edu.Dialogs.Settings.PrefsListDialog;

public class SettingsActivity extends BaseToolbarActivity {

    public static final String TAG = "SettingsActivity";
    private LinearLayout rootView;

    private ImageButton itemDebug;
    private ImageButton itemPrefs;
    private ImageButton itemMapLayer;
    private AppCompatCheckBox itemMapPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getToolbar();
        setToolbarTitle("Настройки");

        rootView = (LinearLayout) findViewById(R.id.root_view);
        itemDebug = (ImageButton) findViewById(R.id.itemDebug);
        itemPrefs = (ImageButton) findViewById(R.id.itemPrefs);
        itemMapLayer = (ImageButton) findViewById(R.id.itemMapLayer);
        itemMapPeople = (AppCompatCheckBox) findViewById(R.id.itemMapPeople);

        itemDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugListDialog debugListDialog = new DebugListDialog();
                debugListDialog.init(SettingsActivity.this, rootView)
                        .show(getSupportFragmentManager(), TAG);
            }
        });
        itemPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefsListDialog prefsListDialog = new PrefsListDialog();
                prefsListDialog.init(SettingsActivity.this, rootView)
                        .show(getSupportFragmentManager(), TAG);
            }
        });
        itemMapLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapLayerDialog mapLayerDialog = new MapLayerDialog();
                mapLayerDialog.init(SettingsActivity.this, rootView)
                        .show(getSupportFragmentManager(), TAG);
            }
        });

        itemMapPeople.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Snackbar.make(rootView, "Checked", Snackbar.LENGTH_SHORT).show();
                else
                    Snackbar.make(rootView, "Unchecked", Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK: {
                    super.onBackPressed();
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
