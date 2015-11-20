package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;

public class BaseNavigationDrawerActivity extends AppCompatActivity {
    protected Drawer drawerResult = null;
    private InputMethodManager MethodManager = null;
    private SearchView searchView;
    private boolean wasInputActive = false;

    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String SIGNED_IN_KEY = EnumSharedPreferences.SIGNED_IN_KEY.toString();
    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_SIGNED_KEY = EnumSharedPreferencesVK.VK_SIGNED_KEY.toString();
    private static final String PROFILE_PHOTO_LOCATION_KEY = EnumSharedPreferences.PROFILE_PHOTO_LOCATION_KEY.toString();
    private static String profilePhotoLocation;

    private SharedPreferences sharedPrefs = null;
    private SharedPreferences sharedPrefsVK = null;

    BaseNavigationDrawerActivity() {
    }

    public void getCurrentSelection() {
        switch (BaseNavigationDrawerActivity.this.getClass().getSimpleName()) {
            case "MainActivity": {
                drawerResult.setSelection(Activities.MainActivity.ordinal());
                break;
            }
            case "MapsActivity": {
                drawerResult.setSelection(Activities.MapsActivity.ordinal());
                break;
            }
            case "SearchActivity": {
                drawerResult.setSelection(Activities.SearchActivity.ordinal());
                break;
            }
            case "TimetableActivity": {
                drawerResult.setSelection(Activities.TimetableActivity.ordinal());
                break;
            }
            default: {
                drawerResult.setSelection(-1);
                break;
            }
        }
    }

    public void getDrawer(String ACCOUNT_NAME, String ACCOUNT_EMAIL) {

// Инициализируем Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_home)
                .withIcon(GoogleMaterial.Icon.gmd_home)
                .withIdentifier(Activities.MainActivity.ordinal());

        final PrimaryDrawerItem map = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_map)
                .withIcon(GoogleMaterial.Icon.gmd_map)
                .withIdentifier(Activities.MapsActivity.ordinal());

        final PrimaryDrawerItem download = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_download)
                .withIcon(GoogleMaterial.Icon.gmd_file_download)
                .withIdentifier(Activities.DownloadActivity.ordinal());

        final PrimaryDrawerItem chat = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_chat)
                .withIcon(GoogleMaterial.Icon.gmd_chat)
                .withIdentifier(Activities.ChatActivity.ordinal())
                .withEnabled(false);

        final PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_settings)
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withIdentifier(Activities.SettingsActivity.ordinal())
                .withEnabled(false);

        final PrimaryDrawerItem timetable = new PrimaryDrawerItem()
                .withName(R.string.timetable)
                .withIcon(GoogleMaterial.Icon.gmd_event_note)
                .withIdentifier(Activities.TimetableActivity.ordinal());

        final PrimaryDrawerItem search = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_search)
                .withIcon(GoogleMaterial.Icon.gmd_search)
                .withIdentifier(Activities.SearchActivity.ordinal());

        final PrimaryDrawerItem exit = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_exit)
                .withIcon(GoogleMaterial.Icon.gmd_close)
                .withIdentifier(Activities.Exit.ordinal());

// Create the AccountHeader;
        ProfileDrawerItem profileMain;
        profilePhotoLocation = sharedPrefs.getString(PROFILE_PHOTO_LOCATION_KEY, "");

        if (sharedPrefsVK.getBoolean(VK_SIGNED_KEY, false) && !profilePhotoLocation .equals("")) {
            profileMain = new ProfileDrawerItem().withName(ACCOUNT_NAME).withEmail(ACCOUNT_EMAIL).withIcon(profilePhotoLocation);
        } else {
            profileMain = new ProfileDrawerItem().withIcon(R.drawable.ic_account_circle_white_48dp);
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_png)
                .addProfiles(
                        profileMain
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        if(!BaseNavigationDrawerActivity.this.sharedPrefs.getBoolean(SIGNED_IN_KEY, false)) {
                            startActivity(new Intent(BaseNavigationDrawerActivity.this, FirstLaunchActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                        return false;
                    }
                })
                .withProfileImagesClickable(true)
                .withSelectionListEnabled(false)
                .build();

// Инициализируем Navigation Drawer
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .withHeaderDivider(false)
                .withDrawerWidthDp(250)
                .addDrawerItems(
                        home,
                        map,
                        timetable,
                        download,
                        chat,
                        new DividerDrawerItem(),
                        settings,
                        search,
                        new DividerDrawerItem(),
                        exit
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        if (MethodManager.isAcceptingText()) {
                            MethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            wasInputActive = true;
                        } else {
                            wasInputActive = false;
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Показать клавиатуру при закрытии Navigation Drawer, если она была открыта
                        if (wasInputActive)
                            MethodManager.showSoftInput(getCurrentFocus(), 0);
                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        try {
                            String CURRENT_CLASS = BaseNavigationDrawerActivity.this.getClass().getSimpleName();
                            String MAIN_CLASS = "MainActivity";
                            String SEARCH_CLASS = "SearchActivity";
                            String MAP_CLASS = "MapsActivity";
                            String TIMETABLE_CLASS = "TimetableActivity";

                            Activities activities = Activities.values()[drawerItem.getIdentifier()];

                            switch (activities) {
                                case MainActivity: {
                                    if (CURRENT_CLASS.equals(MAIN_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, MainActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    }
                                    break;
                                }
                                case MapsActivity: {
                                    if (CURRENT_CLASS.equals(MAP_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, MapsActivity.class));
                                        break;
                                    }
                                }
                                case TimetableActivity: {
                                    if (CURRENT_CLASS.equals(TIMETABLE_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, TimetableActivity.class));
                                        break;
                                    }
                                }
                                case SettingsActivity: {
                                    break;
                                }
                                case SearchActivity: {
                                    if (CURRENT_CLASS.equals(SEARCH_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, SearchActivity.class));
                                    }
                                    break;
                                }
                                case Exit: {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                            .putExtra("EXIT", true)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //getCurrentSelection();

                        /*if (drawerItem instanceof Nameable) {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            if (badgeable.getBadge() != null) {
                                // учтите, не делайте так, если ваш бейдж содержит символ "+"
                                try {
                                    int badge = Integer.valueOf(badgeable.getBadge());
                                    if (badge > 0) {
                                        drawerResult.updateBadge(String.valueOf(badge - 1), position);
                                    }
                                } catch (Exception e) {
                                    Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
                                }
                            }
                        }*/
                        return false;
                    }
                })
                /*.withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            //Toast.makeText(BaseNavigationDrawerActivity.this, BaseNavigationDrawerActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })*/
                .build();
        getCurrentSelection();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Закрытие/Открытие Navigation Drawer при нажатии клавиш "МЕНЮ" и "НАЗАД"
        try {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (drawerResult.isDrawerOpen())
                    drawerResult.closeDrawer();
                else {
                    drawerResult.openDrawer();

                }
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (drawerResult.isDrawerOpen())
                    drawerResult.closeDrawer();
                else if (!searchView.isIconified())
                    searchView.onActionViewCollapsed();
                else
                    super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
        return true;
    }

    public void toastShowShort(String TEXT) {
        Toast.makeText(BaseNavigationDrawerActivity.this, TEXT, Toast.LENGTH_SHORT).show();
    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        sharedPrefsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
    }
}
