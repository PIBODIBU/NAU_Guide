package ua.nau.edu.NAU_Guide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.InfoAdapter.InfoAdapter;

public class InfoActivity extends BaseToolbarActivity {
    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String CORP_ID_KEY = EnumSharedPreferences.CORP_ID_KEY.toString();
    private static final String CORP_LABEL_KEY = EnumSharedPreferences.CORP_LABEL_KEY.toString();

    private SharedPreferences settings = null;

    ViewPager pager;
    PagerSlidingTabStrip tabs;

    public InfoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settings.edit().putInt(CORP_ID_KEY, getIntent().getIntExtra(CORP_ID_KEY, -1)).apply();

        /*** SUPPORT METHODS ***/
        setLabel();
        getToolbar();

        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new InfoAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void rotateImageView(ImageView imgView, float degree) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        imgView.startAnimation(rotateAnim);
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

    public void setLabel() {
        this.setTitle(getString(R.string.corp) +
                " " +
                Integer.toString(getIntent().getIntExtra(CORP_ID_KEY, -1)) +
                ", " +
                getIntent().getStringExtra(CORP_LABEL_KEY));
    }
}