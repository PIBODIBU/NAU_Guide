package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ua.nau.edu.Enum.EnumExtras;
import ua.nau.edu.Enum.EnumMaps;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.Systems.JSONParser;
import ua.nau.edu.Systems.Route;
import ua.nau.edu.University.NAU;

public class MapsActivity extends BaseNavigationDrawerActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public MapsActivity() {
    }

    private int currentMarkerID = -1;
    private String currentMarkerLabel = "";
    private Marker mainActivityMarker = null;

    private InputMethodManager MethodManager = null;
    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private NAU university;
    private Route supportRoute = new Route();
    private FloatingActionMenu fab_menu;

    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_INFO_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
    private static final String VK_EMAIL_KEY = EnumSharedPreferencesVK.VK_EMAIL_KEY.toString();

    private static final String CORP_ID_KEY = EnumExtras.CORP_ID_KEY.toString();
    private static final String CORP_LABEL_KEY = EnumExtras.CORP_LABEL_KEY.toString();

    private static final String CURRENT_LATITUDE = EnumMaps.CURRENT_LATITUDE.toString();
    private static final String CURRENT_LONGTITUDE = EnumMaps.CURRENT_LONGTITUDE.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        university = new NAU(this);
        university.init();

// Get and set system services & Buttons & SharedPreferences & Requests
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        MethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        getDrawer(
                settingsVK.getString(VK_INFO_KEY, ""),
                settingsVK.getString(VK_EMAIL_KEY, "")
        );

        setMenuId(R.menu.menu_maps);
        setUpMapIfNeeded();

        final Animation animRevealReverse = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_reveal_reverse);
        final RelativeLayout layoutHelp = (RelativeLayout) findViewById(R.id.layout_help);
        Button layoutHelp_button_exit = (Button) findViewById(R.id.layout_help_exit);

        layoutHelp_button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutHelp.setVisibility(View.GONE);
                layoutHelp.startAnimation(animRevealReverse);
            }
        });

        initFloatingActionMenu();

        if (mainActivityMarker != null) {
            //Записываем id текущего маркера в глобальную переменную
            currentMarkerID = getMarkerId(mainActivityMarker);

            //Записываем label текущего маркера в глобальную переменную
            currentMarkerLabel = university.getCorpsLabel().get(getMarkerId(mainActivityMarker));

            //Открываем FabMenu
            fab_menu.open(true);

            //Manually open the window
            mainActivityMarker.showInfoWindow();

            //Animate to center
            mMap.animateCamera(CameraUpdateFactory.newLatLng(mainActivityMarker.getPosition()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.location: {
                zoomToMyLocation();
                return true;
            }
            case R.id.traffic: {
                if (mMap.isTrafficEnabled())
                    mMap.setTrafficEnabled(false);
                else
                    mMap.setTrafficEnabled(true);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void addMarkerCustom(Integer i, int icon, String title) {
        Marker mMapMarker = mMap.addMarker(new MarkerOptions()
                .position(university.getCorps().get(i))
                .title(title));

        mMapMarker.setIcon(BitmapDescriptorFactory.fromResource(icon));

        if (getIntent().getIntExtra("MAINACTIVITY_CORP_ID", -1) == i)
            mainActivityMarker = mMapMarker;
    }

    private void setUpMap() {
        //Стартовое положение камеры
        LatLng nau = new LatLng(50.437476, 30.428322);
        CameraPosition cameraPosition_start = new CameraPosition.Builder()
                .target(nau)      // Sets the center of the map to NAU
                .zoom(15)                   // Sets the zoom
                .bearing(160)                // Sets the orientation of the camera to east
                        //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_start));

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        //Добавление маркеров на карту из класса НАУ
        for (int i = 1; i <= 28; i++) {
            addMarkerCustom(i, university.getCorpsIcon().get(i), university.getCorpsMarkerLabel().get(i));
        }

        //Обработчик нажатия на маркер
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Открываем FabMenu
                fab_menu.open(true);

                //Записываем id текущего маркера в глобальную переменную
                currentMarkerID = getMarkerId(marker);

                //Записываем label текущего маркера в глобальную переменную
                currentMarkerLabel = university.getCorpsLabel().get(getMarkerId(marker));

                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (fab_menu.isOpened()) {
                    fab_menu.close(true);
                    currentMarkerID = -1;
                    currentMarkerLabel = "";
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Открываем FabMenu
        fab_menu.open(true);

        //Manually open the window
        marker.showInfoWindow();

        //Animate to center
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        //Consume the method
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    private void initFloatingActionMenu() {
        fab_menu = (FloatingActionMenu) findViewById(R.id.fab_menu);

        FloatingActionButton fab_info = (FloatingActionButton) findViewById(R.id.fab_info);
        FloatingActionButton fab_location = (FloatingActionButton) findViewById(R.id.fab_location);
        FloatingActionButton fab_help = (FloatingActionButton) findViewById(R.id.fab_help);
        FloatingActionButton fab_route = (FloatingActionButton) findViewById(R.id.fab_route);

        fab_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    supportRoute.drawRoute(mMap, MapsActivity.this, getMyCoordinate(), university.getCorps().get(currentMarkerID), Route.TRANSPORT_TRANSIT, true, Route.LANGUAGE_RUSSIAN, R.drawable.ic_place_black_24dp);

                    CameraPosition currentPosition = new CameraPosition.Builder()
                            .target(getMyCoordinate())
                            .bearing(180)
                            .zoom(13f)
                            .build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPosition));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fab_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation animReveal = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_reveal);
                final RelativeLayout layoutHelp = (RelativeLayout) findViewById(R.id.layout_help);

                if (layoutHelp.getVisibility() == View.GONE) {
                    layoutHelp.setVisibility(View.VISIBLE); //It has to be invisible before here
                    layoutHelp.startAnimation(animReveal);
                    fab_menu.close(true);
                }
            }
        });

        fab_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!currentMarkerLabel.equals("") && currentMarkerID != 0 && currentMarkerID > 0 && currentMarkerID <= university.getHashMapSize()) {
                        settings.edit().putInt(CORP_ID_KEY, currentMarkerID).apply();

                        startActivity(new Intent(MapsActivity.this, InfoActivity.class)
                                .putExtra(CORP_ID_KEY, currentMarkerID)
                                .putExtra(CORP_LABEL_KEY, currentMarkerLabel)
                                .putExtra(CURRENT_LATITUDE, mMap.getMyLocation().getLatitude())
                                .putExtra(CURRENT_LONGTITUDE, mMap.getMyLocation().getLongitude()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fab_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, FloorActivity.class));
            }
        });
    }

    //Получение айди маркера
    private int getMarkerId(Marker marker) {
        String s = marker.getId();
        s = s.substring(1, s.length());
        Integer i = Integer.parseInt(s, 10);
        i++;
        return i;
    }

    private void zoomToMyLocation() {
        LatLng latLng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mMap.animateCamera(cameraUpdate);
    }

    private LatLng getMyCoordinate() {
        return new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
    }

    public void initNavigationWindow(int markerid) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?   saddr=" +
                            mMap.getMyLocation().getLatitude() + "," +
                            mMap.getMyLocation().getLongitude() + "&daddr=" +
                            university.getCorps().get(markerid).latitude + "," + university.getCorps().get(markerid).longitude));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}