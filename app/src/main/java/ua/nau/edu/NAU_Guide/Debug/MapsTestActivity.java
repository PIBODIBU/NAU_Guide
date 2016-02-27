package ua.nau.edu.NAU_Guide.Debug;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import android.support.design.widget.Snackbar;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ua.nau.edu.Enum.EnumExtras;
import ua.nau.edu.Enum.EnumMaps;
import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.NAU_Guide.BaseNavigationDrawerActivity;
import ua.nau.edu.NAU_Guide.FloorActivity;
import ua.nau.edu.NAU_Guide.InfoActivity;
import ua.nau.edu.NAU_Guide.MainActivity;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.RecyclerViews.MapsActivity.MapsAdapter;
import ua.nau.edu.RecyclerViews.MapsActivity.MapsDataModel;
import ua.nau.edu.Systems.Route;
import ua.nau.edu.University.NAU;

public class MapsTestActivity extends BaseNavigationDrawerActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public MapsTestActivity() {
    }

    private static final String TAG = "MapsTestActivity";

    private int currentMarkerID = -1;
    private String currentMarkerLabel = "";
    private Marker mainActivityMarker = null;
    private Marker myLocationMarker;

    private InputMethodManager methodManager;
    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;
    private SearchView searchView;
    private HashMap<Integer, Marker> markerHashMap = new HashMap<>();
    private HashMap<Integer, String> nauCorpNamesFull;
    private HashMap<Integer, String> nauCorpNamesShort;

    private RelativeLayout rootView;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient googleApiClient;
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

    /**
     * GoogleApiClient variables
     */
    private static int UPDATE_INTERVAL = 1000; // 5 sec
    private static int FATEST_INTERVAL = 1000; // 5 sec
    private static int DISPLACEMENT = 0; // 1 meters

    private RecyclerView recyclerView;
    private static MapsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MapsDataModel> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        university = new NAU(this);
        university.init();

// Get and set system services & Buttons & SharedPreferences & Requests
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        methodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        rootView = (RelativeLayout) findViewById(R.id.root_view);
        nauCorpNamesFull = university.getCorpsInfoNameFull();
        nauCorpNamesShort = university.getCorpsInfoNameShort();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_user_data);
        recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MapsAdapter(data, this);
        recyclerView.setAdapter(adapter);

        getDrawer(
                settingsVK.getString(VK_INFO_KEY, ""),
                settingsVK.getString(VK_EMAIL_KEY, "")
        );

        initGoogleApiClient();
        setUpMapIfNeeded();
        initFloatingActionMenu();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()/ Disconnecting GoogleApiClient...");
        googleApiClient.disconnect();
        super.onDestroy();
    }

    private void initGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();
    }

    private void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "GoogleApiClient/ Connected");

        // TODO Remove debug items
        Snackbar.make(rootView, "Connected to GoogleApiClient", Snackbar.LENGTH_LONG).show();

        try {
            myLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLatitude(),
                            LocationServices.FusedLocationApi.getLastLocation(googleApiClient).getLongitude()))
                    .title("My Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.avatar_default)));
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }

        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient/ ConnectionSuspended");

        // TODO Remove debug items
        Snackbar.make(rootView, "Connection to GoogleApiClient is suspended", Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient/ Connection failed");

        // TODO Remove debug items
        Snackbar.make(rootView, "Connection to GoogleApiClient failed", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "LocationServices/ onLocationChanged(): " + location.getLatitude() + ", " + location.getLongitude());

        // TODO Remove debug items
        Snackbar.make(rootView, location.getLatitude() + ", " + location.getLongitude(), Snackbar.LENGTH_LONG).show();

        myLocationMarker.remove();
        myLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("My Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        //setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setQueryHint(getResources().getString(R.string.maps_search_hint));
        searchView.setOnQueryTextListener(this);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.onActionViewCollapsed();

                if (getCurrentFocus() != null) {
                    methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                recyclerView.setVisibility(View.VISIBLE);

                return true;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);

                data.clear();

                if (data.size() == 0) {
                    for (int i = 1; i <= nauCorpNamesFull.size(); i++) {
                        data.add(new MapsDataModel(nauCorpNamesFull.get(i), i));
                    }
                }

                adapter.setDataSet(data);
                adapter.notifyDataSetChanged();

                adapter.setOnItemClickListener(new MapsAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int itemId) {
                        recyclerView.setVisibility(View.GONE);
                        if (getCurrentFocus() != null) {
                            methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }

                        Marker currentMarker = markerHashMap.get(itemId);

                        currentMarkerID = getMarkerId(currentMarker);
                        currentMarkerLabel = university.getCorpsLabel().get(currentMarkerID);
                        fab_menu.open(true);
                        currentMarker.showInfoWindow();
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentMarker.getPosition()));
                    }
                });
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "Query changed: " + newText);

        data.clear();

        for (int i = 1; i <= nauCorpNamesFull.size(); i++) {
            String corpNameFull = nauCorpNamesFull.get(i);
            String corpNameShort = nauCorpNamesShort.get(i);

            if (corpNameFull.toLowerCase().contains(newText.toLowerCase().trim()))
                data.add(new MapsDataModel(corpNameFull, i));

            if (corpNameShort.toLowerCase().contains(newText.toLowerCase().trim()))
                data.add(new MapsDataModel(corpNameShort, i));
        }

        if (data.size() == 0) {
            for (int i = 1; i <= nauCorpNamesFull.size(); i++) {
                data.add(new MapsDataModel(nauCorpNamesFull.get(i), i));
            }
        }

        if (data != null) {
            adapter.setDataSet(data);
            adapter.notifyDataSetChanged();

            adapter.setOnItemClickListener(new MapsAdapter.OnItemClickListener() {
                @Override
                public void onClick(int itemId) {
                    recyclerView.setVisibility(View.GONE);
                    if (getCurrentFocus() != null) {
                        methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                    Marker currentMarker = markerHashMap.get(itemId);
                    currentMarkerID = getMarkerId(currentMarker);
                    currentMarkerLabel = university.getCorpsLabel().get(currentMarkerID);
                    fab_menu.open(true);
                    currentMarker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currentMarker.getPosition()));
                }
            });
        }

        return true;
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

    private void setUpMapIfNeeded() {
        Log.i(TAG, "setUpMapIfNeeded called");

        if (mMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    if (mMap != null) {
                        setUpMap();

                        Log.d(TAG, "Setting up GoogleApiClient...");
                    }

                    openMarkerFromIntent();
                }
            });
        }
    }

    private void addMarkerCustom(Integer i, int icon, String title) {
        Marker mMapMarker = mMap.addMarker(new MarkerOptions()
                .position(university.getCorps().get(i))
                .title(title));

        mMapMarker.setIcon(BitmapDescriptorFactory.fromResource(icon));
        if (getIntent().getIntExtra("MAINACTIVITY_CORP_ID", -1) == i)
            mainActivityMarker = mMapMarker;

        markerHashMap.put(i, mMapMarker);
    }

    private void openMarkerFromIntent() {
//Запустили активити не из дровера
        if (mainActivityMarker != null) {
            //Записываем id текущего маркера в глобальную переменную
            currentMarkerID = getMarkerId(mainActivityMarker);
            Log.i("MainActivity", "currentMarkerID =  " + Integer.toString(currentMarkerID));

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

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setIndoorEnabled(true);
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
                        Toast.makeText(MapsTestActivity.this, "Получение маршрута", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        if (!currentMarkerLabel.equals("") && currentMarkerID != 0 && currentMarkerID > 0 && currentMarkerID <= university.getHashMapSize()) {
                            if (isInternetAvailable()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        supportRoute.clearPath();

                                        supportRoute.drawRoute(mMap, MapsTestActivity.this, getMyCoordinate(), university.getCorps().get(currentMarkerID),
                                                Route.TRANSPORT_WALKING, false, Route.LANGUAGE_RUSSIAN, R.drawable.ic_place_black_24dp);

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

                    startActivity(new Intent(MapsTestActivity.this, InfoActivity.class)
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
                startActivity(new Intent(MapsTestActivity.this, FloorActivity.class));
                /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(-33.86997, 151.2089), 18));*/
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