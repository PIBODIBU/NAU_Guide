package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import ua.nau.edu.Enum.EnumExtras;
import ua.nau.edu.Enum.EnumMaps;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.Fragments.MapsFragment;
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
        //setFragment(R.id.fragment_maps);

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
        initFloatingActionMenu();

//Запустили активити не из дровера
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setFragment(int fragmentId) {
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentId, new MainFragment());
        fragmentTransaction.commit();*/

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(fragmentId, new MapsFragment()).commit();

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Try to obtain the map from the SupportMapFragment.
                    mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                            .getMap();
                }
            }).run();

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
        FloatingActionButton fab_route = (FloatingActionButton) findViewById(R.id.fab_route);

        fab_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(MapsActivity.this, "Получение маршрута", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        if (!currentMarkerLabel.equals("") && currentMarkerID != 0 && currentMarkerID > 0 && currentMarkerID <= university.getHashMapSize()) {
                            if (isInternetAvailable()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        supportRoute.drawRoute(mMap, MapsActivity.this, getMyCoordinate(), university.getCorps().get(currentMarkerID), Route.TRANSPORT_WALKING, false, Route.LANGUAGE_RUSSIAN, R.drawable.ic_place_black_24dp);

                                        CameraPosition currentPosition = new CameraPosition.Builder()
                                                .target(getMyCoordinate())
                                                .bearing(180)
                                                .zoom(13f)
                                                .build();
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPosition));
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showInternetDisabledAlertToUser();
                                    }
                                });
                            }
                        }
                        return null;
                    }
                }.execute();
            }
        });

        fab_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentMarkerLabel.equals("") && currentMarkerID != 0 && currentMarkerID > 0 && currentMarkerID <= university.getHashMapSize()) {
                    settings.edit().putInt(CORP_ID_KEY, currentMarkerID).apply();

                    startActivity(new Intent(MapsActivity.this, InfoActivity.class)
                            .putExtra(CORP_ID_KEY, currentMarkerID)
                            .putExtra(CORP_LABEL_KEY, currentMarkerLabel));
                    //.putExtra(CURRENT_LATITUDE, mMap.getMyLocation().getLatitude())
                    //.putExtra(CURRENT_LONGTITUDE, mMap.getMyLocation().getLongitude()));
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
        new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                Toast.makeText(getApplicationContext(), "Получение координат...", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected Boolean doInBackground(Boolean... params) {
                if (isGPSEnabled()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LatLng latLng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                            mMap.animateCamera(cameraUpdate);
                        }
                    });

                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBool) {
                if (aBool)
                    Toast.makeText(getApplicationContext(), "Получено!", Toast.LENGTH_SHORT).show();
                else
                    showGPSDisabledAlertToUser();
            }
        }.execute();
    }

    private LatLng getMyCoordinate() {
        return new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
    }

    public boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return true;
        else
            return false;
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Для определения местоположения необходимо включить GPS. Включить GPS сейчас?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAppPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAppPrimary));
            }
        });

        dialog.show();
    }

    private void showInternetDisabledAlertToUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Ошибка")
                .setMessage("Нету соединения с Интернетом. Пожалуйста, проверьте настройки сети.")
                .setCancelable(false)
                .setNegativeButton("Ок", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAppPrimary));
            }
        });

        dialog.show();
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