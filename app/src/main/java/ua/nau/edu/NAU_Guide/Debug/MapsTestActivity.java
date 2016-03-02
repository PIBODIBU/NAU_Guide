package ua.nau.edu.NAU_Guide.Debug;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.nau.edu.Enum.EnumExtras;
import ua.nau.edu.NAU_Guide.Animation;
import ua.nau.edu.NAU_Guide.BaseNavigationDrawerActivity;
import ua.nau.edu.NAU_Guide.FloorActivity;
import ua.nau.edu.NAU_Guide.InfoActivity;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.RecyclerViews.MapsActivity.MapsAdapter;
import ua.nau.edu.RecyclerViews.MapsActivity.MapsDataModel;
import ua.nau.edu.Support.GoogleMap.GoogleMapUtils;
import ua.nau.edu.Support.GoogleMap.RouteDrawer.Route;
import ua.nau.edu.Support.View.SearchViewUtils;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.University.NAU;

public class MapsTestActivity extends BaseNavigationDrawerActivity
        implements GoogleMap.OnMarkerClickListener, SearchView.OnQueryTextListener {
    public MapsTestActivity() {
    }

    private final String TAG = this.getClass().getSimpleName();

    private SharedPrefUtils sharedPrefUtils;

    private Bundle savedInstanceState;

    private int currentMarkerID = -1;
    private String currentMarkerLabel = "";
    private Marker mainActivityMarker = null;

    private InputMethodManager methodManager;
    private SharedPreferences settings = null;
    private SearchView searchView;
    private HashMap<Integer, Marker> markerHashMap = new HashMap<>();

    private RelativeLayout rootView;

    private GoogleApiClient googleApiClient;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private NAU university;
    private Route supportRoute = new Route();
    private FloatingActionMenu fab_menu;

    private static final String CORP_ID_KEY = EnumExtras.CORP_ID_KEY.toString();
    private static final String CORP_LABEL_KEY = EnumExtras.CORP_LABEL_KEY.toString();

    private RecyclerView recyclerView;
    private static MapsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MapsDataModel> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPrefUtils = new SharedPrefUtils(this);
        settings = getSharedPreferences(SharedPrefUtils.APP_PREFERENCES, MODE_PRIVATE);

        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState;
        }

        university = new NAU(this);
        university.init();

// Get and set system services & Buttons & SharedPreferences & Requests
        methodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        rootView = (RelativeLayout) findViewById(R.id.root_view);

        setUpMapIfNeeded();
        initFloatingActionMenu();
        setUpRecyclerView();

        getDrawer(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail()
        );
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_user_data);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MapsAdapter(data, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            outState.putString("SearchViewQuery", searchView.getQuery().toString());
            outState.putBoolean("RecyclerViewWasVisible", true);
        } else if (recyclerView.getVisibility() == View.GONE) {
            outState.putBoolean("RecyclerViewWasVisible", false);
        }


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //setUpMapIfNeeded();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
                    } else if (searchView != null) {
                        if (!searchView.isIconified()) {
                            Animation.Reveal.revealCloseTopRight(recyclerView);
                            searchView.onActionViewCollapsed();
                        } else {
                            super.onBackPressed();
                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                        }
                    } else {
                        super.onBackPressed();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        /**
         * Setting Up SearchView
         */
        setUpSearchView(searchView, searchItem);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "Query changed: " + newText);

        // Clearing ArrayList before adding new items
        data.clear();

        if (!newText.equals("")) {
            // Searching for results
            for (Map.Entry<Integer, String> entry : university.getCorpsInfoNameFull().entrySet()) {
                if (entry.getValue().toLowerCase().contains(newText.toLowerCase().trim()))
                    data.add(new MapsDataModel(entry.getValue(), entry.getKey()));
            }

            for (Map.Entry<Integer, String> entry : university.getCorpsInfoNameShort().entrySet()) {
                if (entry.getValue().toLowerCase().contains(newText.toLowerCase().trim()))
                    data.add(new MapsDataModel(entry.getValue(), entry.getKey()));
            }


            // Nothing was found. Show warning
            if (data.size() == 0) {
                // TODO add warning message
                Snackbar.make(rootView, "Ничего не найдено", Snackbar.LENGTH_LONG).show();
            }
        } else {
            for (Map.Entry<Integer, String> entry : university.getCorpsInfoNameFull().entrySet()) {
                data.add(new MapsDataModel(entry.getValue(), entry.getKey()));
            }
        }

        adapter.setDataSet(data);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new MapsAdapter.OnItemClickListener() {
            @Override
            public void onClick(int itemId) {
                // Hide RecyclerView & SearchView & keyboard
                hideKeyboard();
                Animation.Reveal.revealCloseTopRight(recyclerView);
                searchView.onActionViewCollapsed();

                // Zooming to marker
                zoomToMarker(markerHashMap.get(itemId));
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location: {
                zoomToMyLocation();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Setting Up SearchView
     *
     * @param searchView SearchView for setup
     */
    private void setUpSearchView(final SearchView searchView, MenuItem searchMenu) {
        searchView.setQueryHint(getResources().getString(R.string.maps_search_hint));
        searchView.setOnQueryTextListener(this);
        SearchViewUtils.setHintColor(this, searchView, R.color.searchView_hint_color);

        /**
         * Restoring saved state of SearchView & RecyclerView
         */
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("RecyclerViewWasVisible", false)) {
                searchView.onActionViewExpanded();
                searchView.setQuery(savedInstanceState.getString("SearchViewQuery", ""), false);

                Animation.Reveal.revealOpenTopRight(recyclerView);
            }
        }

        /**
         * SearchView is closing with button (on Toolbar)
         */
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "OnCloseListener/ onClose() called");

                // Hide keyboard
                hideKeyboard();

                // Close RecyclerView & SearchView
                searchView.onActionViewCollapsed();
                Animation.Reveal.revealCloseTopRight(recyclerView);

                return true;
            }
        });

        /**
         * SearchView is pressed, opening...
         */
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show RecyclerView
                Animation.Reveal.revealOpenTopRight(recyclerView);

                addDefaultItems();

                // Animating to clicked position
                adapter.setOnItemClickListener(new MapsAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int itemId) {
                        // Hide RecyclerView & SearchView & keyboard
                        hideKeyboard();
                        Animation.Reveal.revealCloseTopRight(recyclerView);
                        searchView.onActionViewCollapsed();

                        // Zooming to marker
                        zoomToMarker(markerHashMap.get(itemId));
                    }
                });

            }
        });

    }

    /**
     * Adding default items to RecyclerView.Adapter
     */
    private void addDefaultItems() {
        data.clear();
        for (Map.Entry<Integer, String> entry : university.getCorpsInfoNameFull().entrySet()) {
            data.add(new MapsDataModel(entry.getValue(), entry.getKey()));
        }
        adapter.setDataSet(data);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new MapsAdapter.OnItemClickListener() {
            @Override
            public void onClick(int itemId) {
                // Hide RecyclerView & SearchView & keyboard
                hideKeyboard();
                Animation.Reveal.revealCloseTopRight(recyclerView);
                searchView.onActionViewCollapsed();

                // Zooming to marker
                zoomToMarker(markerHashMap.get(itemId));
            }
        });
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
                        /*mMap.addCircle(new CircleOptions()
                                .center(new LatLng(50.437476, 30.428322))
                                .radius(1000)
                                .fillColor(ContextCompat.getColor(MapsTestActivity.this, R.color.blue_30))
                                .strokeColor(ContextCompat.getColor(MapsTestActivity.this, R.color.colorAppPrimary)));*/

                        mMap.addGroundOverlay(new GroundOverlayOptions()
                                .position(new LatLng(50.437476, 30.428322), 197, 63)
                                .transparency(0.5f)
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.map_1)));

                        setUpMap();
                        initGoogleApiClient();
                    }
                }
            });
        }
    }

    /**
     * Adding custom marker on GoogleMap
     *
     * @param i     loop iterator
     * @param icon  marker's icon resource id
     * @param title title of icon
     */
    private void addMarkerCustom(Integer i, int icon, String title) {
        Marker mMapMarker = mMap.addMarker(new MarkerOptions()
                .position(university.getCorps().get(i))
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(icon)));

        if (getIntent().getIntExtra("MAINACTIVITY_CORP_ID", -1) == i)
            mainActivityMarker = mMapMarker;

        markerHashMap.put(i, mMapMarker);
    }

    /**
     * Adding custom marker on GoogleMap
     *
     * @param i                  loop iterator == merker Id
     * @param markerOptions      new MarkerOptions for custom Marker
     * @param markerIdFromIntent Activity was started from button (e.g. on MainActivity), so
     *                           need to get Id of Marker to open
     */
    private void addMarkerCustom(int i, MarkerOptions markerOptions, int markerIdFromIntent) {
        Marker mMapMarker = mMap.addMarker(markerOptions);

        if (markerIdFromIntent == i) {
            Log.d(TAG, "addMarkerCustom()/ Intent: " + markerIdFromIntent + " == Iterator: " + i);
            openMarkerFromIntent(mMapMarker);
        }

        markerHashMap.put(i, mMapMarker);
    }

    /**
     * Activity wasn't started from Drawer
     * Need to open marker
     */
    private void openMarkerFromIntent(Marker marker) {
        if (marker != null) {
            Log.d(TAG, "openMarkerFromIntent() opening marker...");

            //Записываем id текущего маркера в глобальную переменную
            currentMarkerID = getMarkerId(marker);
            Log.i("MainActivity", "currentMarkerID =  " + Integer.toString(currentMarkerID));

            //Записываем label текущего маркера в глобальную переменную
            currentMarkerLabel = university.getCorpsLabel().get(getMarkerId(marker));

            //Открываем FabMenu
            fab_menu.open(true);

            //Manually open the window
            marker.showInfoWindow();

            //Animate to center
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        } else {
            Log.e(TAG, "openMarkerFromIntent() mainActivityMarker == null");
        }
    }

    /**
     * Setting Up GoogleMap
     */
    private void setUpMap() {
        // Camera start position
        LatLng nau = new LatLng(50.437476, 30.428322);
        CameraPosition cameraPosition_start = new CameraPosition.Builder()
                .target(nau)      // Sets the center of the map to NAU
                .zoom(15)                   // Sets the zoom
                .bearing(160)                // Sets the orientation of the camera to east
                .build();                   // Creates a CameraPosition from the builder

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_start));

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        /**
         * Adding markers from {@link ua.nau.edu.University.NAU} using {@link android.os.AsyncTask}
         * Important! Loop needs to be started from 1
         */
        new AsyncTask<Void, Void, HashMap<Integer, MarkerOptions>>() {
            @Override
            protected void onPostExecute(HashMap<Integer, MarkerOptions> markerOptions) {
                int markerIdFromIntent = getIntent().getIntExtra("MAINACTIVITY_CORP_ID", -1);
                for (int i = 1; i <= markerOptions.size(); i++) {
                    addMarkerCustom(i, markerOptions.get(i), markerIdFromIntent);
                }
            }

            @Override
            protected HashMap<Integer, MarkerOptions> doInBackground(Void... params) {
                HashMap<Integer, MarkerOptions> markerOptions = new HashMap<>();

                for (int i = 1; i <= university.getHashMapSize(); i++) {
                    markerOptions.put(i, new MarkerOptions()
                            .position(university.getCorps().get(i))
                            .title(university.getCorpsMarkerLabel().get(i))
                            .icon(BitmapDescriptorFactory.fromResource(university.getCorpsIcon().get(i))));
                }

                return markerOptions;
            }
        }.execute();

        //Обработчик нажатия на маркер
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Open FabMenu
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

    /**
     * Zoom camera to marker & show InfoWindow
     *
     * @param marker marker for zooming
     */
    private void zoomToMarker(Marker marker) {
        currentMarkerID = getMarkerId(marker);
        currentMarkerLabel = university.getCorpsLabel().get(currentMarkerID);
        fab_menu.open(true);
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    private LatLng getMyCoordinate() {
        return new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
    }

    private void initGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Log.d(TAG, "googleApiClient/ onConnected()");

                            LocationRequest mLocationRequest = new LocationRequest();
                            mLocationRequest.setInterval(1000);
                            mLocationRequest.setFastestInterval(1000);
                            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            mLocationRequest.setSmallestDisplacement(0);

                            try {
                                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        double distance = GoogleMapUtils.getDistanceBetweenPoints(
                                                new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(50.437476, 30.428322));
                                        Snackbar.make(rootView, "Distance to NAU == " + distance, Snackbar.LENGTH_SHORT).show();
                                        Log.d(TAG, "getDistanceBetweenPoints()/ Distance to NAU == " + distance);
                                    }
                                });
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.d(TAG, "googleApiClient/ onConnectionSuspended()");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Log.d(TAG, "googleApiClient/ onConnectionFailed()");
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }

        googleApiClient.connect();
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
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
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

    private void hideKeyboard() {
        if (getCurrentFocus() != null)
            methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}