package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.picasso.Picasso;

import ua.nau.edu.Dialogs.AccountHeaderBgPicker;
import ua.nau.edu.Enum.Activities;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class BaseNavigationDrawerActivity extends AppCompatActivity {
    private static final String TAG = "BaseNavigationDrawer";

    protected DrawerBuilder drawerBuilder = null;
    protected Drawer drawer = null;
    private InputMethodManager MethodManager = null;
    private SearchView searchView;
    private boolean wasInputActive = false;
    private int menuId = -1;

    private static final String EXIT_KEY = EnumSharedPreferences.EXIT.toString();
    private static String profilePhotoLocation;

    private SharedPrefUtils sharedPrefUtils;

    public BaseNavigationDrawerActivity() {
    }

    public void getCurrentSelection() {
        switch (BaseNavigationDrawerActivity.this.getClass().getSimpleName()) {
            case "MainActivity": {
                drawer.setSelection(Activities.MainActivity.ordinal());
                break;
            }
            case "MapsActivity": {
                drawer.setSelection(Activities.MapsActivity.ordinal());
                break;
            }
            case "LectorsListActivity": {
                drawer.setSelection(Activities.LectorsListActivity.ordinal());
                break;
            }
            case "NewsActivity": {
                drawer.setSelection(Activities.NewsActivity.ordinal());
                break;
            }
            case "SettingsActivity": {
                drawer.setSelection(Activities.SettingsActivity.ordinal());
                break;
            }
            case "UserProfileActivity": {
                drawer.setSelection(-1);
                break;
            }
            default: {
                drawer.setSelection(-1);
                break;
            }
        }
    }

    private void setToolbarTitle() {
        try {
            String text = getSupportActionBar().getTitle().toString();
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            TextView title = (TextView) findViewById(R.id.toolbar_title);
            title.setText(text);
        } catch (Exception ex) {
            Log.e(TAG, "setToolbarTitle() -> ", ex);
        }
    }

    public void getSecondDrawer(@NonNull Drawer.OnDrawerItemClickListener onDrawerItemClickListener) {
        new DrawerBuilder()
                .withActivity(BaseNavigationDrawerActivity.this)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIcon(GoogleMaterial.Icon.gmd_map)
                                .withName("Show people")
                                .withOnDrawerItemClickListener(onDrawerItemClickListener))
                .withDrawerGravity(Gravity.END)
                .append(drawer);
    }

    public void setToolbarTitle(String text) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(text);
    }

    public void getDrawer(String ACCOUNT_NAME, String ACCOUNT_EMAIL) {
        // Creating DrawerBuilder
        setUpDrawerBuilder(ACCOUNT_NAME, ACCOUNT_EMAIL);

        Log.i("Drawer", "getDrawer");

        // Creating Drawer from DrawerBuilder
        setUpDrawer();

        // Getting current Drawer selection
        getCurrentSelection();
    }

    public void getDrawerWithBackArrow(String ACCOUNT_NAME, String ACCOUNT_EMAIL) {
        // Creating DrawerBuilder
        setUpDrawerBuilder(ACCOUNT_NAME, ACCOUNT_EMAIL);

        Log.i("Drawer", "getDrawerWithBackArrow");

        // Creating Drawer from DrawerBuilder
        setUpDrawer();

        // Show back arrow icon
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Getting current Drawer selection
        getCurrentSelection();
    }

    public void setUpDrawer() {
        if (drawerBuilder != null) {
            final PrimaryDrawerItem myPage = new PrimaryDrawerItem()
                    .withName(R.string.drawer_item_mypage)
                    .withIcon(GoogleMaterial.Icon.gmd_grade)
                    .withIdentifier(Activities.UserProfileActivity.ordinal());

            drawer = drawerBuilder.build();
            drawer.getRecyclerView().setVerticalScrollBarEnabled(false);

            if (!sharedPrefUtils.getToken().equals("")) {
                drawer.addItemAtPosition(myPage, 2);
            }
        }
    }

    public void setUpDrawerBuilder(String ACCOUNT_NAME, String ACCOUNT_EMAIL) {
        // Инициализируем Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarTitle();

        final PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_home)
                .withIcon(GoogleMaterial.Icon.gmd_domain)
                .withIdentifier(Activities.MainActivity.ordinal());

        final PrimaryDrawerItem posts = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_posts)
                .withIcon(GoogleMaterial.Icon.gmd_reorder)
                .withIdentifier(Activities.NewsActivity.ordinal());

        final PrimaryDrawerItem map = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_map)
                .withIcon(GoogleMaterial.Icon.gmd_map)
                .withIdentifier(Activities.MapsActivity.ordinal());

        final PrimaryDrawerItem lectors = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_lectors)
                .withIcon(GoogleMaterial.Icon.gmd_perm_identity)
                .withIdentifier(Activities.LectorsListActivity.ordinal());

        final PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_settings)
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withIdentifier(Activities.SettingsActivity.ordinal());

        final PrimaryDrawerItem exit = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_exit)
                .withIcon(GoogleMaterial.Icon.gmd_close)
                .withIdentifier(Activities.Exit.ordinal());

        /**
         * AccountHeader setup
         */
        ProfileDrawerItem profileMain;
        profilePhotoLocation = sharedPrefUtils.getProfilePhotoLocation();

        if (sharedPrefUtils.getSignedState()) {
            profileMain = new ProfileDrawerItem().withName(ACCOUNT_NAME).withEmail(ACCOUNT_EMAIL).withIcon(profilePhotoLocation);
        } else {
            profileMain = new ProfileDrawerItem().withIcon(R.drawable.avatar_default);
        }

        /**
         * AccountHeader implementing
         */
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        profileMain
                )
                .withTypeface(Typeface.defaultFromStyle(Typeface.BOLD)) // Make text in AccountHeader BOLD
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        if (!sharedPrefUtils.getSignedState()) {
                            startActivity(new Intent(BaseNavigationDrawerActivity.this, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            if (!sharedPrefUtils.getToken().equals("")) {
                                startActivity(new Intent(BaseNavigationDrawerActivity.this, UserProfileActivity.class)
                                        .putExtra("action", "getMyPage")
                                        .putExtra("uniqueId", sharedPrefUtils.getUniqueId())
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        }
                        return false;
                    }
                })
                .withProfileImagesClickable(true)
                .withSelectionListEnabled(false)
                .build();

        /**
         * Set up AccountHeader Background using Picasso
         */
        final ImageView accountHeaderBackground = accountHeader.getHeaderBackgroundView();

        // Load AccountHeader background image from SharedPreferences
        int accountHeaderBgResId = sharedPrefUtils.getAccountheaderBgImage();
        if (accountHeaderBgResId != -1) {
            Picasso.with(BaseNavigationDrawerActivity.this).load(accountHeaderBgResId).into(accountHeaderBackground);
            Log.d(TAG, "Current AccountHeader image id: " + Integer.toString(accountHeaderBgResId));
        } else {
            Picasso.with(BaseNavigationDrawerActivity.this).load(R.drawable.material_bg_6).into(accountHeaderBackground);
        }

        accountHeaderBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Set up AlertDialog for background picker
                 */
                AccountHeaderBgPicker dialog = new AccountHeaderBgPicker();
                dialog.setOnBackgroundChangedListener(new AccountHeaderBgPicker.OnBackgroundChangedListener() {
                    @Override
                    public void onBackgroundChanged(int imageId) {
                        // Refreshing AccountHeader background image, if new one was selected
                        Picasso.with(BaseNavigationDrawerActivity.this).load(imageId).into(accountHeaderBackground);
                    }
                });
                dialog.show(getSupportFragmentManager(), TAG);

            }
        });

        /**
         * Implementing DrawerBuilder
         */
        drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(accountHeader)
                .withHeaderDivider(false)
                .addDrawerItems(
                        home,
                        posts,
                        map,
                        lectors,
                        new DividerDrawerItem(),
                        settings,
                        exit
                )
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    /**
                     * This method is only called if the Arrow icon is shown. The hamburger is automatically managed by the MaterialDrawer
                     * If the back arrow is shown - close the activity
                     *
                     * @param clickedView
                     * @return true if we have consumed the event
                     */
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        BaseNavigationDrawerActivity.this.finish();
                        return true;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        if (MethodManager.isAcceptingText()) {
                            if (getCurrentFocus() != null) {
                                MethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
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
                            String MAP_CLASS = "MapsActivity";
                            String LECTORS_CLASS = "LectorsListActivity";
                            String USER_CLASS = "UserProfileActivity";
                            String POSTS_CLASS = "NewsActivity";
                            String SETTINGS_CLASS = "SettingsActivity";

                            Activities activities = Activities.values()[(int) drawerItem.getIdentifier()];

                            switch (activities) {
                                case MainActivity: {
                                    if (CURRENT_CLASS.equals(MAIN_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, MainActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
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
                                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
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
                                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                                        break;
                                    }
                                }
                                case UserProfileActivity: {
                                    if (CURRENT_CLASS.equals(USER_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, UserProfileActivity.class)
                                                .putExtra("action", "getMyPage")
                                                .putExtra("uniqueId", sharedPrefUtils.getUniqueId())
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                                        break;
                                    }
                                }
                                case NewsActivity: {
                                    if (CURRENT_CLASS.equals(POSTS_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, NewsActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                                        break;
                                    }
                                }
                                case SettingsActivity: {
                                    if (CURRENT_CLASS.equals(POSTS_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, SettingsActivity.class));
                                        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                                        break;
                                    }
                                }
                                case Exit: {
                                    Log.d(TAG, "Exit Item selected");
                                    startActivity(new Intent(BaseNavigationDrawerActivity.this, MainActivity.class)
                                            .putExtra(EXIT_KEY, true)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return false;
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown():\nkeyCode: " + Integer.toString(keyCode) + "\nkeyEvent: " + event);
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU: {
                    if (drawer.isDrawerOpen())
                        drawer.closeDrawer();
                    else
                        drawer.openDrawer();
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    if (drawer.isDrawerOpen()) { // Check if Drawer is opened
                        drawer.closeDrawer();
                    } else {
                        super.onBackPressed();
                    }

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        sharedPrefUtils = new SharedPrefUtils(this);
    }

    @Override
    protected void onPostResume() {
        getCurrentSelection();
        super.onPostResume();
    }
}
