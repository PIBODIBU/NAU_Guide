package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ua.nau.edu.Enum.Activities;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.Systems.SharedPrefUtils.SharedPrefUtils;

public class BaseNavigationDrawerActivity extends AppCompatActivity {
    protected Drawer drawerResult = null;
    private InputMethodManager MethodManager = null;
    private SearchView searchView;
    private boolean wasInputActive = false;
    private int menuId = -1;

    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String SIGNED_IN_KEY = EnumSharedPreferences.SIGNED_IN_KEY.toString();
    private static final String JUST_SIGNED_KEY = EnumSharedPreferences.JUST_SIGNED_KEY.toString();
    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_SIGNED_KEY = EnumSharedPreferencesVK.VK_SIGNED_KEY.toString();
    private static final String VK_PHOTO_KEY = EnumSharedPreferencesVK.VK_PHOTO_KEY.toString();
    private static final String PROFILE_PHOTO_LOCATION_KEY = EnumSharedPreferences.PROFILE_PHOTO_LOCATION_KEY.toString();
    private static final String EXIT_KEY = EnumSharedPreferences.EXIT.toString();
    private static String profilePhotoLocation;

    private SharedPreferences sharedPrefs = null;
    private SharedPreferences sharedPrefsVK = null;
    private SharedPrefUtils sharedPrefUtils;

    public BaseNavigationDrawerActivity() {
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
            case "LectorsListActivity": {
                drawerResult.setSelection(Activities.LectorsListActivity.ordinal());
                break;
            }
            case "UserProfileActivity": {
                drawerResult.setSelection(Activities.UserProfileActivity.ordinal());
                break;
            }
            default: {
                drawerResult.setSelection(-1);
                break;
            }
        }
    }

    public void setToolbarTitle(Toolbar toolbar) {
        String text = getSupportActionBar().getTitle().toString();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(text);
    }

    public void getDrawer(String ACCOUNT_NAME, String ACCOUNT_EMAIL) {
// Инициализируем Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarTitle(toolbar);


        final PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_home)
                .withIcon(GoogleMaterial.Icon.gmd_home)
                .withIdentifier(Activities.MainActivity.ordinal());

        final PrimaryDrawerItem myPage = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_mypage)
                .withIcon(GoogleMaterial.Icon.gmd_pageview)
                .withIdentifier(Activities.UserProfileActivity.ordinal());

        final PrimaryDrawerItem map = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_map)
                .withIcon(GoogleMaterial.Icon.gmd_map)
                .withIdentifier(Activities.MapsActivity.ordinal());

        final PrimaryDrawerItem lectors = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_lectors)
                .withIcon(GoogleMaterial.Icon.gmd_account_circle)
                .withIdentifier(Activities.LectorsListActivity.ordinal());

        final PrimaryDrawerItem download = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_download)
                .withIcon(GoogleMaterial.Icon.gmd_file_download)
                .withIdentifier(Activities.DownloadActivity.ordinal());

        final PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_settings)
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withIdentifier(Activities.SettingsActivity.ordinal())
                .withEnabled(false);

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

        if (sharedPrefs.getBoolean(SIGNED_IN_KEY, false)) {
            profileMain = new ProfileDrawerItem().withName(ACCOUNT_NAME).withEmail(ACCOUNT_EMAIL).withIcon(profilePhotoLocation);
        } else {
            profileMain = new ProfileDrawerItem().withIcon(R.drawable.ic_account_circle_white_48dp);
        }

// Create AccountHeader
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        profileMain
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        if (!BaseNavigationDrawerActivity.this.sharedPrefs.getBoolean(SIGNED_IN_KEY, false)) {
                            startActivity(new Intent(BaseNavigationDrawerActivity.this, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            if (!sharedPrefs.getString(sharedPrefUtils.TOKEN_KEY, "").equals("")) {
                                startActivity(new Intent(BaseNavigationDrawerActivity.this, UserProfileActivity.class));
                            }
                        }
                        return false;
                    }
                })
                .withProfileImagesClickable(true)
                .withSelectionListEnabled(false)
                .build();
        // Set up AccountHeader Background using Picasso
        ImageView accountHeaderBackground = accountHeader.getHeaderBackgroundView();
        Picasso.with(BaseNavigationDrawerActivity.this).load(R.drawable.header_png).into(accountHeaderBackground);

// Инициализируем Navigation Drawer
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(accountHeader)
                .withHeaderDivider(false)
                .addDrawerItems(
                        home,
                        map,
                        lectors,
                        download,
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
                            String LECTORS_CLASS = "LectorsListActivity";
                            String USER_CLASS = "UserProfileActivity";

                            Activities activities = Activities.values()[drawerItem.getIdentifier()];

                            switch (activities) {
                                case MainActivity: {
                                    if (CURRENT_CLASS.equals(MAIN_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, MainActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                        break;
                                    }
                                }
                                case MapsActivity: {
                                    if (CURRENT_CLASS.equals(MAP_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, MapsActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                        break;
                                    }
                                }
                                case LectorsListActivity: {
                                    if (CURRENT_CLASS.equals(LECTORS_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, LectorsListActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                        break;
                                    }
                                }
                                case UserProfileActivity: {
                                    if (CURRENT_CLASS.equals(USER_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, UserProfileActivity.class)
                                                .putExtra("action", "getMyPage")
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
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
                                            .putExtra(EXIT_KEY, true)
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
        if (!sharedPrefUtils.getToken().equals("")) {
            drawerResult.addItemAtPosition(myPage, 2);
        }
        getCurrentSelection();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU: {
                    if (drawerResult.isDrawerOpen())
                        drawerResult.closeDrawer();
                    else
                        drawerResult.openDrawer();
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    if (drawerResult.isDrawerOpen())
                        drawerResult.closeDrawer();
                    else if (!searchView.isIconified())
                        searchView.onActionViewCollapsed();
                    else
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
        Toast.makeText(BaseNavigationDrawerActivity.this, TEXT, Toast.LENGTH_SHORT).show();
    }

    public void toastShowLong(String TEXT) {
        Toast.makeText(getApplicationContext(), TEXT, Toast.LENGTH_LONG).show();
    }

    void setMenuId(int menu) {
        this.menuId = menu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (menuId != -1)
            inflater.inflate(menuId, menu);
        else
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
        sharedPrefUtils = new SharedPrefUtils(sharedPrefs, sharedPrefsVK);
    }
}
