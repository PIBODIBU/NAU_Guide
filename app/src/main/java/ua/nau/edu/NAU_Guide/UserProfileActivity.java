package ua.nau.edu.NAU_Guide;

import android.content.DialogInterface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);

        UserAdapter adapter = new UserAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentInfo(), "Информация");
        adapter.addFrag(new FragmentPosts(), "Записи");
        adapter.addFrag(new FragmentTimetable(), "Расписание");

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5); // Лимит хранения Фрагментов в памяти
        tabs.setupWithViewPager(pager);

        //getMyPage();

    }

    private void showDialogConnectionError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Ошибка соединения с Интернетом")
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.colorAppPrimary));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
