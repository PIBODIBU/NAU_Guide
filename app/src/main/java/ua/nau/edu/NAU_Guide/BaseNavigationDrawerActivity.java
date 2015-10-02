package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

//import com.mikepenz.iconics.typeface.FontAwesome;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiUserFull;

import java.io.InputStream;
import java.net.URL;


public class BaseNavigationDrawerActivity extends AppCompatActivity {
    private static final String VK_PREFERENCES = "VK_PREFERENCES";
    private static final String VK_INFO_KEY = "VK_INFO_KEY";
    private static final String VK_PHOTO_KEY = "VK_PHOTO_KEY";
    private static final String VK_EMAIL_KEY = "VK_EMAIL_KEY";
    private static final String VK_SIGNED_KEY = "VK_SIGNED_KEY";
    private static final String VK_ID_KEY = "VK_ID_KEY";

    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";

    SharedPreferences settings_global = null;
    SharedPreferences settings_vk = null;
    SharedPreferences.Editor editor_global;
    SharedPreferences.Editor editor_vk;

    protected Drawer drawerResult = null;
    private InputMethodManager MethodManager = null;
    private SearchView searchView;
    private boolean wasInputActive = false;

    BaseNavigationDrawerActivity() {
    }

    public void getCurrentSelection() {
        switch (BaseNavigationDrawerActivity.this.getClass().getSimpleName()) {
            case "MainActivity": {
                drawerResult.setSelection(0);
                break;
            }
            case "MapsActivity": {
                drawerResult.setSelection(1);
                break;
            }
            case "SearchActivity": {
                drawerResult.setSelection(5);
                break;
            }
            default: {
                drawerResult.setSelection(-1);
                break;
            }
        }
    }

    public void getDrawer(String ACCOUNT_NAME, String ACCOUNT_PHOTO, String ACCOUNT_EMAIL) {

// Инициализируем Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_home)
                .withIcon(FontAwesome.Icon.faw_home)
                .withIdentifier(0);

        final PrimaryDrawerItem map = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_map)
                .withIcon(FontAwesome.Icon.faw_map_marker)
                .withIdentifier(1);

        final PrimaryDrawerItem download = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_download)
                .withIcon(FontAwesome.Icon.faw_download)
                .withIdentifier(2);

        final PrimaryDrawerItem settings = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_settings)
                .withIcon(FontAwesome.Icon.faw_cog)
                .withIdentifier(4);

        final PrimaryDrawerItem search = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_search)
                .withIcon(FontAwesome.Icon.faw_search)
                .withIdentifier(5);

        final PrimaryDrawerItem exit = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_exit)
                .withIcon(FontAwesome.Icon.faw_close)
                .withIdentifier(7);

// Image Downloader for Drawer
        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context context) {
                return null;
            }

            @Override
            public Drawable placeholder(Context ctx, String s) {
                return null;
            }
        });
//

// Create the AccountHeader
        ProfileDrawerItem profile_1;

        if (settings_vk.getBoolean(VK_SIGNED_KEY, false)) {
            profile_1 = new ProfileDrawerItem().withName(ACCOUNT_NAME).withEmail(ACCOUNT_EMAIL).withIcon(ACCOUNT_PHOTO);
        } else {
            profile_1 = new ProfileDrawerItem().withName("I am giraffe").withEmail("giraffe@giraffe.com").withIcon(getResources().getDrawable(R.drawable.default_header_icon));
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_png)
                .addProfiles(
                        profile_1
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

// Инициализируем Navigation Drawer
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                        //.withHeader(R.layout.drawer_header)
                .withAccountHeader(headerResult)
                .withHeaderDivider(false)
                .addDrawerItems(
                        home,
                        map,
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

                            switch (drawerItem.getIdentifier()) {
                                case 0: {
                                    if (CURRENT_CLASS.equals(MAIN_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, MainActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    }
                                    break;
                                }
                                case 1: {
                                    if (CURRENT_CLASS.equals(MAP_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, MapsActivity.class));
                                        break;
                                    }
                                }
                                case 4: {
                                    startActivity(new Intent(BaseNavigationDrawerActivity.this, SettingsActivity.class));
                                    break;
                                }
                                case 5: {
                                    if (CURRENT_CLASS.equals(SEARCH_CLASS)) {
                                        break;
                                    } else {
                                        startActivity(new Intent(BaseNavigationDrawerActivity.this, SearchActivity.class));
                                    }
                                    break;
                                }
                                case 7: {
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

        settings_global = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
        settings_vk = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        editor_global = settings_global.edit();
        editor_vk = settings_vk.edit();
    }
}
