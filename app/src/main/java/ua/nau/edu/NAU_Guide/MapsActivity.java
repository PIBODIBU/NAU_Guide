package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.Enum.EnumExtras;
import ua.nau.edu.Enum.EnumMaps;
import ua.nau.edu.NAU_Guide.Debug.MapsDistanceDataModel;
import ua.nau.edu.RecyclerViews.MapsActivity.MapsAdapter;
import ua.nau.edu.RecyclerViews.MapsActivity.MapsDataModel;
import ua.nau.edu.Support.GoogleMap.GoogleMapUtils;
import ua.nau.edu.Support.GoogleMap.RouteDrawer.Route;
import ua.nau.edu.Support.System.HardwareChecks;
import ua.nau.edu.Support.View.SearchViewUtils;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.University.NAU;

public class MapsActivity extends BaseNavigationDrawerActivity
        implements GoogleMap.OnMarkerClickListener, SearchView.OnQueryTextListener {
    public MapsActivity() {
    }

    private final String TAG = this.getClass().getSimpleName();
    private final Context activityContext = MapsActivity.this;

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
    private static final String CURRENT_LATITUDE = EnumMaps.CURRENT_LATITUDE.toString();
    private static final String CURRENT_LONGTITUDE = EnumMaps.CURRENT_LONGTITUDE.toString();

    private RecyclerView recyclerView;
    private static MapsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MapsDataModel> data = new ArrayList<>();

    private boolean isSnackBarDistanceShowing = false;

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
        try {
            Log.d(TAG, "Disconnecting GoogleClientApi...");
            googleApiClient.disconnect();
        } catch (NullPointerException ex) {
            Log.d(TAG, "Can't disconnect. GoogleClientApi == null", ex);
        }

        Log.d(TAG, "onDestroy()");
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
    private void setUpSearchView(final SearchView searchView, final MenuItem searchMenu) {
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
                searchView.clearFocus();

                //Show RecyclerView
                Animation.Reveal.revealOpenTopRight(recyclerView);

                searchView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchView.requestFocus();
                        showKeyboard();
                    }
                }, Animation.Reveal.animDuration);

                addDefaultSearchViewItems();

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
    private void addDefaultSearchViewItems() {
        data.clear();

        HashMap<Integer, String> corpsNames = university.getCorpsInfoNameFull();
        for (int entryId = 1; entryId <= corpsNames.size(); entryId++) {
            data.add(new MapsDataModel(corpsNames.get(entryId), entryId));
            Log.d(TAG, "adding search item with/" + "i: " + entryId);
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
                .bearing(160)               // Sets the orientation of the camera to east
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
                        Toast.makeText(activityContext, "Получение маршрута", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        if (!currentMarkerLabel.equals("") && currentMarkerID != 0 && currentMarkerID > 0 && currentMarkerID <= university.getHashMapSize()) {
                            if (HardwareChecks.isInternetAvailable()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        supportRoute.clearPath();

                                        supportRoute.drawRoute(mMap, activityContext, getMyCoordinate(), university.getCorps().get(currentMarkerID),
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
                                        APIDialogs.AlertDialogs.internetConnectionError(activityContext);
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
                if (!currentMarkerLabel.equals("") &&
                        currentMarkerID != 0 && currentMarkerID > 0 &&
                        currentMarkerID <= university.getHashMapSize()) {

                    startActivity(new Intent(activityContext, InfoActivity.class)
                            .putExtra(CORP_ID_KEY, currentMarkerID)
                            .putExtra(CORP_LABEL_KEY, currentMarkerLabel)
                            .putExtra(CURRENT_LATITUDE, getMyCoordinate().latitude)
                            .putExtra(CURRENT_LONGTITUDE, getMyCoordinate().longitude));

                }
            }
        });

        fab_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIDialogs.AlertDialogs.customDialog(activityContext, "Внимание", "Сервис находится в разработке");
                //startActivity(new Intent(activityContext, FloorActivity.class));
            }
        });
    }

    private int getMarkerId(Marker marker) {
        String s = marker.getId();
        s = s.substring(1, s.length());
        Integer i = Integer.parseInt(s, 10);
        i++;
        return i;
    }

    private void zoomToMyLocation() {
        if (HardwareChecks.isWifiEnabled(this)) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(getMyCoordinate(), 16);
            mMap.animateCamera(cameraUpdate);
        } else {
            APIDialogs.AlertDialogs.wifiDisabled(this);
        }
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

    public LatLng getMyCoordinate() {
        Location currentLocation = null;

        try {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            Log.e(TAG, "getMyCoordinate() -> ", ex);
        }

        if (currentLocation != null)
            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        else
            return null;
    }

    /**
     * Find the nearest point to user's location
     *
     * @param myLocation LatLng Object which points to user's current location
     * @param points     HashMap<Integer, LatLng> Object, which contains points for comparing
     * @return Key of points - object, which has minimal distance to user
     */
    private MapsDistanceDataModel findMinDistance(LatLng myLocation, HashMap<Integer, LatLng> points) {
        int minId = -1;
        double currentMin;
        double previousMin = -1;
        double minimalDistance = -1;

        for (Map.Entry<Integer, LatLng> entry : points.entrySet()) {
            currentMin = GoogleMapUtils.getDistanceBetweenPoints(myLocation, entry.getValue());
            if (currentMin < previousMin) {
                minId = entry.getKey();
                minimalDistance = currentMin;
            }
            previousMin = currentMin;
        }

        Log.d(TAG, "minDistance = " + minimalDistance + "\nminId = " + minId);

        return new MapsDistanceDataModel(minId, minimalDistance);
    }

    private void initGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            /*Log.d(TAG, "googleApiClient/ onConnected()");

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

                                        Log.d(TAG, "getDistanceBetweenPoints()/ Distance to NAU == " + distance);

                                        if (distance <= 1.0) {
                                            Log.d(TAG, "You are in NAU area");
                                        }

                                        MapsDistanceDataModel minDistance = findMinDistance(getMyCoordinate(), university.getCorps());
                                        isSnackBarDistanceShowing = true;
                                        final Snackbar snackbarD = Snackbar.make(rootView, university.getCorpsLabel().get(minDistance.getMinId())
                                                + " -> " + minDistance.getDistance(), Snackbar.LENGTH_INDEFINITE);
                                        snackbarD.setAction("Ok. bro", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                snackbarD.dismiss();
                                                isSnackBarDistanceShowing = false;
                                            }
                                        }).show();
                                    }
                                });
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }*/
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

    private void hideKeyboard() {
        if (getCurrentFocus() != null)
            methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void showKeyboard() {
        if (getCurrentFocus() != null)
            methodManager.showSoftInput(getCurrentFocus(), 0);
    }

}