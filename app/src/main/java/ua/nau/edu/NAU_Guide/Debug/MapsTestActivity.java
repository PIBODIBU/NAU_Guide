package ua.nau.edu.NAU_Guide.Debug;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
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
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.Support.GoogleMap.MarkerDataModel;
import ua.nau.edu.Support.Picasso.PicassoMarker;
import ua.nau.edu.Support.View.AnimationSupport;
import ua.nau.edu.NAU_Guide.BaseNavigationDrawerActivity;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.RecyclerViews.MapsActivity.Search.MapsSearchAdapter;
import ua.nau.edu.RecyclerViews.MapsActivity.Search.MapsSearchDataModel;
import ua.nau.edu.Support.GoogleMap.GoogleMapUtils;
import ua.nau.edu.Support.GoogleMap.RouteDrawer.Route;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.Support.System.HardwareChecks;
import ua.nau.edu.Support.System.Utils;
import ua.nau.edu.Support.View.SearchViewUtils;
import ua.nau.edu.University.NAU;

public class MapsTestActivity extends BaseNavigationDrawerActivity
        implements SearchView.OnQueryTextListener {
    public MapsTestActivity() {
    }

    private final String TAG = this.getClass().getSimpleName();
    private final Context activityContext = MapsTestActivity.this;

    private SharedPrefUtils sharedPrefUtils;

    private Bundle savedInstanceState;

    private int currentMarkerID = -1;
    private String currentMarkerLabel = "";
    private Marker mainActivityMarker = null;

    private InputMethodManager methodManager;
    private SharedPreferences settings = null;
    private SearchView searchView;
    private HashMap<Integer, Marker> markerHashMap = new HashMap<>();

    private CoordinatorLayout rootView;

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private NAU university;
    private Route supportRoute = new Route();
    private FloatingActionButton FABMyLocation;

    private RecyclerView recyclerView;
    private static MapsSearchAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MapsSearchDataModel> dataSearch = new ArrayList<>();

    private BottomSheetBehavior bottomSheetBehavior;

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

        methodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        rootView = (CoordinatorLayout) findViewById(R.id.root_view);

        setUpMapIfNeeded();
        initBottomSheet();
        setUpRecyclerView();

        getDrawer(
                sharedPrefUtils.getName(),
                sharedPrefUtils.getEmail()
        );

        FABMyLocation = (FloatingActionButton) findViewById(R.id.fab_location);

        FABMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomToMyLocation();
            }
        });

    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_search);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MapsSearchAdapter(dataSearch, this);
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

        if (googleMap != null) {
            if (sharedPrefUtils.getMapLayer() != -1)
                googleMap.setMapType(sharedPrefUtils.getMapLayer());
        }
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
                            AnimationSupport.Reveal.revealCloseTopRight(recyclerView);
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
        dataSearch.clear();

        if (!newText.equals("")) {
            // Searching for results
            for (Map.Entry<Integer, String> entry : university.getCorpsInfoNameFull().entrySet()) {
                if (entry.getValue().toLowerCase().contains(newText.toLowerCase().trim()))
                    dataSearch.add(new MapsSearchDataModel(entry.getValue(), entry.getKey()));
            }

            for (Map.Entry<Integer, String> entry : university.getCorpsInfoNameShort().entrySet()) {
                if (entry.getValue().toLowerCase().contains(newText.toLowerCase().trim()))
                    dataSearch.add(new MapsSearchDataModel(entry.getValue(), entry.getKey()));
            }


            // Nothing was found. Show warning
            if (dataSearch.size() == 0) {
                // TODO add warning message
                Snackbar.make(rootView, "Ничего не найдено", Snackbar.LENGTH_LONG).show();
            }
        } else {
            for (Map.Entry<Integer, String> entry : university.getCorpsInfoNameFull().entrySet()) {
                dataSearch.add(new MapsSearchDataModel(entry.getValue(), entry.getKey()));
            }
        }

        adapter.setDataSet(dataSearch);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new MapsSearchAdapter.OnItemClickListener() {
            @Override
            public void onClick(int itemId) {
                // Hide RecyclerView & SearchView & keyboard
                hideKeyboard();
                AnimationSupport.Reveal.revealCloseTopRight(recyclerView);
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

                AnimationSupport.Reveal.revealOpenTopRight(recyclerView);
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
                AnimationSupport.Reveal.revealCloseTopRight(recyclerView);

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
                AnimationSupport.Reveal.revealOpenTopRight(recyclerView);

                searchView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchView.requestFocus();
                        showKeyboard();
                    }
                }, AnimationSupport.Reveal.animDuration);

                addDefaultSearchViewItems();

                // Animating to clicked position
                adapter.setOnItemClickListener(new MapsSearchAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int itemId) {
                        // Hide RecyclerView & SearchView & keyboard
                        hideKeyboard();
                        AnimationSupport.Reveal.revealCloseTopRight(recyclerView);
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
        dataSearch.clear();

        HashMap<Integer, String> corpsNames = university.getCorpsInfoNameFull();
        for (int entryId = 1; entryId <= corpsNames.size(); entryId++) {
            dataSearch.add(new MapsSearchDataModel(corpsNames.get(entryId), entryId));
            Log.d(TAG, "adding search item with/" + "i: " + entryId);
        }

        adapter.setDataSet(dataSearch);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new MapsSearchAdapter.OnItemClickListener() {
            @Override
            public void onClick(int itemId) {
                // Hide RecyclerView & SearchView & keyboard
                hideKeyboard();
                AnimationSupport.Reveal.revealCloseTopRight(recyclerView);
                searchView.onActionViewCollapsed();

                // Zooming to marker
                zoomToMarker(markerHashMap.get(itemId));
            }
        });
    }

    private void setUpMapIfNeeded() {
        Log.i(TAG, "setUpMapIfNeeded called");

        if (googleMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MapsTestActivity.this.googleMap = googleMap;

                    if (MapsTestActivity.this.googleMap != null) {
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
        Marker mMapMarker = googleMap.addMarker(new MarkerOptions()
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
        Marker mMapMarker = googleMap.addMarker(markerOptions);

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
            Log.d(TAG, "openMarkerFromIntent() -> opening marker...");

            //Записываем id текущего маркера в глобальную переменную
            currentMarkerID = getMarkerId(marker);
            Log.d(TAG, "openMarkerFromIntent() -> currentMarkerID:  " + Integer.toString(currentMarkerID));

            //Записываем label текущего маркера в глобальную переменную
            currentMarkerLabel = university.getCorpsLabel().get(getMarkerId(marker));

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

            openBottomSheet(currentMarkerLabel, university.getCorpsInfoNameFull().get(currentMarkerID));
            marker.showInfoWindow();
        } else {
            Log.e(TAG, "openMarkerFromIntent() marker == null");
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

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_start));

        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setIndoorEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        if (sharedPrefUtils.getMapLayer() != -1)
            googleMap.setMapType(sharedPrefUtils.getMapLayer());

        /**
         * Adding markers from {@link ua.nau.edu.University.NAU} using {@link android.os.AsyncTask}
         * Important! Loop needs to be started from 1
         */
        /*new AsyncTask<Void, Void, HashMap<Integer, MarkerOptions>>() {
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
        }.execute();*/

        new AsyncTask<String, Void, ArrayList<MarkerDataModel>>() {
            private ProgressDialog loadingDialog;
            private ArrayList<MarkerDataModel> markerDataModels = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                loadingDialog = new ProgressDialog(activityContext, "Sending POST request...");
                loadingDialog.setCancelable(false);
                loadingDialog.show();
            }

            @Override
            protected ArrayList<MarkerDataModel> doInBackground(String... params) {
                try {
                    loadingDialog.setTitle("Parsing data");

                    APIHTTPUtils httpUtils = new APIHTTPUtils();
                    HashMap<String, String> requestParams = new HashMap<>();

                    requestParams.put("action", "getMarkers");
                    String markersData = httpUtils.sendPostRequestWithParams(params[0], requestParams);
                    Log.d(TAG, "Server response: " + markersData);

                    JSONArray jsonArray = new JSONArray(markersData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        markerDataModels.add(new MarkerDataModel(
                                jsonObject.getInt("id"),
                                jsonObject.getDouble("lat"),
                                jsonObject.getDouble("lng"),
                                jsonObject.getString("icon"),
                                jsonObject.getString("label"),
                                jsonObject.getString("name_short"),
                                jsonObject.getString("name_full"),
                                jsonObject.getString("phone"),
                                jsonObject.getString("information"),
                                jsonObject.getString("website"),
                                jsonObject.getString("slider_images")
                        ));

                        Log.d(TAG, "Added new MarkerDataModel with: \n" +
                                "id: " + jsonObject.getInt("id") + "\n" +
                                "lat: " + jsonObject.getDouble("lat") + "\n" +
                                "lng: " + jsonObject.getDouble("lng") + "\n" +
                                "icon: " + jsonObject.getString("icon") + "\n" +
                                "label: " + jsonObject.getString("label") + "\n" +
                                "name_short: " + jsonObject.getString("name_short") + "\n" +
                                "name_full: " + jsonObject.getString("name_full") + "\n" +
                                "phone: " + jsonObject.getString("phone") + "\n" +
                                "information: " + jsonObject.getString("information") + "\n" +
                                "website: " + jsonObject.getString("website") + "\n" +
                                "slider_images: " + jsonObject.getString("slider_images"));

                    }
                } catch (Exception ex) {
                    Log.e(TAG, "", ex);
                    return null;
                }

                return markerDataModels;
            }

            @Override
            protected void onPostExecute(ArrayList<MarkerDataModel> markerDataModels) {
                if (markerDataModels != null && markerDataModels.size() != 0) {
                    loadingDialog.setTitle("Adding markers");

                    PicassoMarker picassoMarker;

                    for (int i = 0; i < markerDataModels.size(); i++) {
                        MarkerDataModel markerDataModel = markerDataModels.get(i);

                        final Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(markerDataModel.getLat(), markerDataModel.getLng()))
                                .title(markerDataModel.getLabel()));

                        /*picassoMarker = new PicassoMarker(marker);
                        Picasso.
                                with(activityContext)
                                .load(markerDataModel.getIcon())
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(picassoMarker);*/
                        Glide
                                .with(activityContext)
                                .load(markerDataModel.getIcon()).asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        Log.d(TAG, "Glide -> Bitmap loaded");
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(resource));
                                    }
                                });

                        markerHashMap.put(i, marker);
                    }
                }
                loadingDialog.dismiss();
            }
        }.execute("http://nauguide.esy.es/include/api.php");

        supportRoute.setRouteCallbacks(new Route.RouteCallbacks() {
            @Override
            public void onDrawSuccess() {
                // Animate & move camera to current position
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(getMyCoordinate())
                        .bearing(180)
                        .zoom(15f)
                        .build()));
            }
        });

        //Обработчик нажатия на маркер
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Open FabMenu
                //fab_menu.open(true);

                //Записываем id текущего маркера в глобальную переменную
                currentMarkerID = getMarkerId(marker);

                //Записываем label текущего маркера в глобальную переменную
                currentMarkerLabel = university.getCorpsLabel().get(getMarkerId(marker));

                // Open BottomSheetDialog instead of Fab menu
                openBottomSheet(currentMarkerLabel, university.getCorpsInfoNameFull().get(currentMarkerID));
                Log.d(TAG, "onMarkerClick() -> MarkerId: " + currentMarkerID + " MarkerLabel: " + currentMarkerLabel);

                //Manually open the window
                marker.showInfoWindow();

                //Animate to center
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                return true;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
    }

    /**
     * Bottom Sheet block
     */
    private void initBottomSheet() {
        new AsyncTask<Void, Void, Void>() {

            // Root View
            View bottomSheetFrame = rootView.findViewById(R.id.bottom_sheet);

            // Overlaping head
            final LinearLayout sheetOverlapHead = (LinearLayout) findViewById(R.id.head_bsheet);
            final TextView sheetOverlapHeadTitle = (TextView) bottomSheetFrame.findViewById(R.id.title_sheet);
            final TextView sheetOverlapHeadSubtitle = (TextView) bottomSheetFrame.findViewById(R.id.subtitle_sheet);

            // CollapsingToolbarLayout, Toolbar, FAB
            final CollapsingToolbarLayout sheetCollapsingToolbar = (CollapsingToolbarLayout) bottomSheetFrame.findViewById(R.id.collapsing_toolbar);
            final TextView sheetCollapsingToolbarTitle = (TextView) bottomSheetFrame.findViewById(R.id.title_collapse_sheet);
            final ImageView sheetCollapsingToolbarBackground = (ImageView) bottomSheetFrame.findViewById(R.id.backdrop);
            final Toolbar sheetToolbar = (Toolbar) bottomSheetFrame.findViewById(R.id.toolbar_bsheet);
            final TextView sheetToolbarTitle = (TextView) findViewById(R.id.toolbar_title_bsheet);
            final android.support.design.widget.FloatingActionButton sheetFABRoute = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab_route_bsheet);

            // Main content
            final ExpandableTextView sheetContentExpandText = (ExpandableTextView) findViewById(R.id.text_expandable);
            final SliderLayout sheetContentSlider = (SliderLayout) findViewById(R.id.slider);
            final ImageView sheetContentImageGoogleMap = (ImageView) findViewById(R.id.google_map);
            final ImageView sheetContentImageGoogleStreet = (ImageView) findViewById(R.id.google_street);

            @Override
            protected void onPreExecute() {
                sheetContentExpandText.setText(getString(R.string.lorem_ipsum));

                HashMap<Integer, Integer> sliderImages = new HashMap<>();
                sliderImages.put(0, R.drawable.material_bg_1);
                sliderImages.put(1, R.drawable.material_bg_2);
                sliderImages.put(2, R.drawable.material_bg_3);
                sliderImages.put(3, R.drawable.material_bg_4);
                sliderImages.put(4, R.drawable.material_bg_5);
                for (int i = 0; i < sliderImages.size(); i++) {
                    sheetContentSlider.addSlider(new DefaultSliderView(activityContext)
                            .image(sliderImages.get(i))
                            .setScaleType(BaseSliderView.ScaleType.Fit));
                }

                sheetFABRoute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            drawPathToMarker(university.getCorps().get(currentMarkerID));
                        } catch (Exception ex) {
                            Log.e(TAG, "sheetFABRoute -> onclick(): ", ex);
                        }
                    }
                });

                Picasso.with(activityContext).load(R.drawable.google_map).into(sheetContentImageGoogleMap);
                Picasso.with(activityContext).load(R.drawable.google_street).into(sheetContentImageGoogleStreet);
            }

            @Override
            protected Void doInBackground(Void... params) {
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetFrame);

                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    private boolean isOnTop = false;

                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        Log.d(TAG, "setUpMap() -> onStateChanged() -> newState: " + newState);
                        switch (newState) {

                            case BottomSheetBehavior.STATE_DRAGGING: {
                                if (isOnTop) {
                                    sheetOverlapHead.setVisibility(View.INVISIBLE);
                                } else {
                                    AnimationSupport.Fade.fadeOut(activityContext, sheetOverlapHead);
                                }

                                sheetToolbar.setVisibility(View.INVISIBLE);

                                break;
                            }

                            case BottomSheetBehavior.STATE_SETTLING: {
                                break;
                            }

                            case BottomSheetBehavior.STATE_EXPANDED: {
                                sheetToolbar.setVisibility(View.VISIBLE);

                                break;
                            }

                            case BottomSheetBehavior.STATE_COLLAPSED: {
                                AnimationSupport.Fade.fadeIn(activityContext, sheetOverlapHead);
                                sheetOverlapHeadTitle.setText(currentMarkerLabel);
                                sheetOverlapHeadSubtitle.setText(university.getCorpsInfoNameFull().get(currentMarkerID));

                                sheetCollapsingToolbarTitle.setText(currentMarkerLabel);

                                sheetToolbarTitle.setText(university.getCorpsInfoNameFull().get(currentMarkerID));
                                sheetToolbar.setVisibility(View.INVISIBLE);

                                sheetFABRoute.setVisibility(View.VISIBLE);

                                FABMyLocation.hide();

                                break;
                            }

                            case BottomSheetBehavior.STATE_HIDDEN: {
                                sheetFABRoute.setVisibility(View.GONE);

                                FABMyLocation.show();

                                break;
                            }

                            default: {
                                break;
                            }
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        Log.d(TAG, "setUpMap() -> onSlide() -> slideOffset: " + slideOffset);

                        if (slideOffset > 0.8) {
                            isOnTop = true;

                            sheetFABRoute.hide();
                        } else {
                            isOnTop = false;

                            sheetFABRoute.show();
                        }
                    }
                });

                bottomSheetBehavior.setPeekHeight((int) Utils.convertDpToPixel(100f, activityContext));
                try {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } catch (Exception ex) {
                    Log.e(TAG, "", ex);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public void openBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void openBottomSheet(String title, String subTitle) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        try {
            final TextView titleSheetHead = (TextView) findViewById(R.id.title_sheet);
            final TextView subTitleSheetHead = (TextView) findViewById(R.id.subtitle_sheet);
            final TextView bSheetToolbarTitle = (TextView) findViewById(R.id.toolbar_title_bsheet);
            final TextView titleCollapsingSheet = (TextView) findViewById(R.id.title_collapse_sheet);

            titleSheetHead.setText(title);
            subTitleSheetHead.setText(subTitle);
            titleCollapsingSheet.setText(title);
            bSheetToolbarTitle.setText(subTitle);
        } catch (Exception ex) {
            Log.e(TAG, "openBottomSheet() -> ", ex);
        }
    }

    private int getMarkerId(Marker marker) {
        String s = marker.getId();
        s = s.substring(1, s.length());
        Integer i = Integer.parseInt(s, 10);
        i++;
        return i;
    }

    /**
     * Move and zoom camera to current position
     */
    private void zoomToMyLocation() {
        if (HardwareChecks.isWifiEnabled(this)) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(getMyCoordinate(), 18);
            googleMap.animateCamera(cameraUpdate);
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

        openBottomSheet();

        marker.showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    /**
     * Draw path from current position to {@code latLng}
     *
     * @param latLng destination position
     */
    private void drawPathToMarker(LatLng latLng) {
        if (HardwareChecks.isInternetAvailable()) {
            supportRoute.clearPath();

            supportRoute.drawRoute(googleMap, activityContext, getMyCoordinate(), latLng,
                    Route.TRANSPORT_WALKING, false, Route.LANGUAGE_RUSSIAN, R.drawable.ic_place_black_24dp);

        } else {
            APIDialogs.AlertDialogs.internetConnectionError(activityContext);
        }
    }

    /**
     * Find current location
     *
     * @return new {@link LatLng} instance - current coordinates
     */
    @Nullable
    public LatLng getMyCoordinate() {
        Location currentLocation;

        try {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            Log.e(TAG, "getMyCoordinate() -> ", ex);
            return null;
        }

        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    /**
     * Find the nearest point to user's location
     *
     * @param myLocation LatLng Object which points to user's current location
     * @param points     HashMap<Integer, LatLng> Object, which contains points for comparing
     * @return new {@link MapsDistanceDataModel} instance
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
        if (getCurrentFocus() != null) {
            methodManager.showSoftInput(getCurrentFocus(), 0);

            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }

}