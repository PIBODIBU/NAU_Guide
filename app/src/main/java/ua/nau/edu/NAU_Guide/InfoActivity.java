package ua.nau.edu.NAU_Guide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.maps.model.LatLng;

import ua.nau.edu.Enum.EnumExtras;
import ua.nau.edu.Enum.EnumMaps;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Adapters.AdapterInfoActivity.AdapterInfo;

public class InfoActivity extends BaseToolbarActivity {

    private final String TAG = getClass().getSimpleName();

    private static final String CORP_ID_KEY = EnumExtras.CORP_ID_KEY.toString();
    private static final String CORP_LABEL_KEY = EnumExtras.CORP_LABEL_KEY.toString();

    private static final String CURRENT_LATITUDE = EnumMaps.CURRENT_LATITUDE.toString();
    private static final String CURRENT_LONGTITUDE = EnumMaps.CURRENT_LONGTITUDE.toString();

    private ViewPager pager;
    private PagerSlidingTabStrip tabs;

    private int currentCorp = -1;

    public InfoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setActivityLabel();
        getToolbar();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        currentCorp = getIntent().getIntExtra(CORP_ID_KEY, -1);

        Log.d(TAG, "currentCorp == " + currentCorp);

        if (currentCorp <= 12 && currentCorp != -1) {
            // This is corp
            setUpLayoutCorp();
        } else if (currentCorp == 13) {
            //This is CKM
            setUpLayoutCkm();
        } else if (currentCorp == 14) {
            //This is Bistro
            setUpLayoutBistro();
        } else if (currentCorp == 15) {
            // This is MED Center
            setUpLayoutMed();
        } else if (currentCorp == 16) {
            // This is Sport
            setUpLayoutSport();
        } else if (currentCorp <= 27 && currentCorp >= 17) {
            //This is host
            setUpLayoutHost();
        } else if (currentCorp == 28) {
            //This is library
            setUpLayoutLibrary();
        } else {
            setUpLayoutDefault();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setUpLayoutCorp() {
        Log.d(TAG, "setUpLayoutCorp() called");

        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new AdapterInfo(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        tabs.setViewPager(pager);
    }

    private void setUpLayoutCkm() {
        Log.d(TAG, "setUpLayoutCkm() called");

        tabs.setVisibility(View.GONE);

    }

    private void setUpLayoutBistro() {
        tabs.setVisibility(View.GONE);

    }

    private void setUpLayoutMed() {
        tabs.setVisibility(View.GONE);

    }

    private void setUpLayoutSport() {
        tabs.setVisibility(View.GONE);

    }

    private void setUpLayoutHost() {
        tabs.setVisibility(View.GONE);

    }

    private void setUpLayoutLibrary() {
        tabs.setVisibility(View.GONE);

    }

    private void setUpLayoutDefault() {
        tabs.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_SHORT).show();
    }

    public void setActivityLabel() {
        this.setTitle(getIntent().getStringExtra(CORP_LABEL_KEY));
    }

    public LatLng getMyCoordinate() {
        return new LatLng(getIntent().getDoubleExtra(CURRENT_LATITUDE, 1.0), getIntent().getDoubleExtra(CURRENT_LONGTITUDE, 1.0));
    }
}