package ua.nau.edu.NAU_Guide;

import android.content.DialogInterface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import ua.nau.edu.Enum.Activities;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.NAU_Guide.LoginLector.LoginLectorActivity;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.Adapters.UserProfileAdapter.Fragments.FragmentInfo;
import ua.nau.edu.Adapters.UserProfileAdapter.Fragments.FragmentPosts;
import ua.nau.edu.Adapters.UserProfileAdapter.Fragments.FragmentTimetable;
import ua.nau.edu.Adapters.UserProfileAdapter.UserAdapter;

public class UserProfileActivity extends BaseNavigationDrawerActivity {

    private static final String REQUEST_URL = "http://nauguide.esy.es/include/getMyPage.php";
    private static final String TAG = "UserProfile";

    private SharedPrefUtils sharedPrefUtils;

    private TextView textView;
    private ImageView userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        sharedPrefUtils = new SharedPrefUtils(
                getSharedPreferences(EnumSharedPreferences.APP_PREFERENCES.toString(), MODE_PRIVATE),
                getSharedPreferences(EnumSharedPreferencesVK.VK_PREFERENCES.toString(), LoginLectorActivity.MODE_PRIVATE));

        getDrawer(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail()
        );

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //collapsingToolbarLayout.setTitle("Профиль");
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);

        UserAdapter adapter = new UserAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentInfo(), "Информация");
        adapter.addFrag(new FragmentPosts(), "Записи");
        adapter.addFrag(new FragmentTimetable(), "Расписание");

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5); // Лимит хранения Фрагментов в памяти
        tabs.setupWithViewPager(pager);

        if (getIntent().getStringExtra("action").equals("getMyPage"))
            this.drawer.setSelection(Activities.UserProfileActivity.ordinal());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Log.i(TAG, "Back arrow pressed");
                finish();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
