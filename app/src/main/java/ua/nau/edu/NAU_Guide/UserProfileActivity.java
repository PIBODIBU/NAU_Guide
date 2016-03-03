package ua.nau.edu.NAU_Guide;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ua.nau.edu.Adapters.UserProfileAdapter.Fragments.FragmentInfo;
import ua.nau.edu.Adapters.UserProfileAdapter.Fragments.FragmentPosts;
import ua.nau.edu.Adapters.UserProfileAdapter.Fragments.FragmentTimetable;
import ua.nau.edu.Adapters.UserProfileAdapter.UserProfileAdapter;
import ua.nau.edu.Enum.Activities;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class UserProfileActivity extends BaseNavigationDrawerActivity {

    private static final String TAG = "UserProfileActivity";

    private SharedPrefUtils sharedPrefUtils;

    public CoordinatorLayout rootView;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public AppBarLayout mAppBarLayout;
    private String titleAcivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        sharedPrefUtils = new SharedPrefUtils(this);

        getDrawerWithBackArrow(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail());

        new AsyncTask<Void, Void, Void>() {

            TabLayout tabs;
            ViewPager pager;
            UserProfileAdapter adapter;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                rootView = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                //collapsingToolbarLayout.setTitle("BIG BIG BIG BIG TEXT");
                //collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(UserProfileActivity.this, android.R.color.transparent));

                mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
            }

            @Override
            protected Void doInBackground(Void... params) {
                tabs = (TabLayout) findViewById(R.id.tabs);
                pager = (ViewPager) findViewById(R.id.viewpager);

                adapter = new UserProfileAdapter(getSupportFragmentManager());
                adapter.addFrag(new FragmentInfo(), "Информация");
                adapter.addFrag(new FragmentPosts(), "Записи");
                adapter.addFrag(new FragmentTimetable(), "Расписание");

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                pager.setAdapter(adapter);
                pager.setOffscreenPageLimit(5); // Лимит хранения Фрагментов в памяти
                tabs.setupWithViewPager(pager);
            }
        }.execute();

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
                onBackPressed();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setDrawerToMyPage() {
        drawer.setSelection(Activities.UserProfileActivity.ordinal());
    }

}
