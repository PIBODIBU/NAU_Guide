package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
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
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ua.nau.edu.API.APIDialogs;
import ua.nau.edu.API.APIHTTPUtils;
import ua.nau.edu.API.APIUrl;
import ua.nau.edu.NAU_Guide.Debug.MapsDistanceDataModel;
import ua.nau.edu.RecyclerViews.MapsActivity.Search.MapsSearchAdapter;
import ua.nau.edu.RecyclerViews.MapsActivity.Search.MapsSearchDataModel;
import ua.nau.edu.Support.Glide.CircleTransform;
import ua.nau.edu.Support.GoogleMap.GoogleMapUtils;
import ua.nau.edu.Support.GoogleMap.MarkerDataModel;
import ua.nau.edu.Support.GoogleMap.RouteDrawer.Route;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;
import ua.nau.edu.Support.System.HardwareChecks;
import ua.nau.edu.Support.System.Utils;
import ua.nau.edu.Support.View.AnimationSupport;
import ua.nau.edu.Support.View.SearchViewUtils;

public class MapsActivity extends BaseNavigationDrawerActivity
        implements SearchView.OnQueryTextListener {
    public MapsActivity() {
    }

    private final String TAG = this.getClass().getSimpleName();
    private final Context activityContext = MapsActivity.this;

    private SharedPrefUtils sharedPrefUtils;

    private Bundle savedInstanceState;

    private int currentMarkerID = 1;

    private InputMethodManager methodManager;
    private SearchView searchView;

    private CoordinatorLayout rootView;

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available
    private Route supportRoute = new Route();
    private FloatingActionButton FABMyLocation;

    private RecyclerView recyclerView;
    private static MapsSearchAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MapsSearchDataModel> dataSearch = new ArrayList<>();

    private BottomSheetBehavior bottomSheetBehavior;

    private ArrayList<MarkerDataModel> markerPrimaryModels = new ArrayList<>();
    private HashMap<Marker, MarkerDataModel> markerAllHashMap = new HashMap<>();
    private ArrayList<Marker> markerPeopleArrayList = new ArrayList<>();

    private int idFromIntent;
    private Timer usersLocationsLoaderTaskTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sharedPrefUtils = new SharedPrefUtils(this);

        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState;
        }

        idFromIntent = getIntent().getIntExtra("MAINACTIVITY_CORP_ID", -1);
        Log.d(TAG, "idFromIntent: " + idFromIntent);

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

        View view = getLayoutInflater().inflate(R.layout.drawer_view, null);
        TextView textView = (TextView) view.findViewById(R.id.text);

        getSupportDrawer(textView,
                new PrimaryDrawerItem()
                        .withName("Show people")
                        .withIdentifier(1)
                        .withIcon(GoogleMaterial.Icon.gmd_people)
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            private boolean isTimerStarted = false;

                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                if (!isTimerStarted) {
                                    Log.d(TAG, "onItemClick() -> Timer started");

                                    isTimerStarted = true;
                                    usersLocationsLoaderTaskTimer = new Timer();
                                    TimerTask doAsynchronousTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                            UsersLocationsLoaderTask usersLocationsLoaderTask = new UsersLocationsLoaderTask();
                                            usersLocationsLoaderTask.execute();
                                        }
                                    };
                                    usersLocationsLoaderTaskTimer.schedule(doAsynchronousTask, 0, 5 * 1000);
                                } else {
                                    Log.d(TAG, "onItemClick() -> Timer canceled");

                                    isTimerStarted = false;
                                    usersLocationsLoaderTaskTimer.cancel();
                                }
                                return false;
                            }
                        }),
                new PrimaryDrawerItem()
                        .withName("Push me hard")
                        .withIdentifier(2)
                        .withIcon(GoogleMaterial.Icon.gmd_people)
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                Snackbar.make(rootView, "Hi, bro!", Snackbar.LENGTH_LONG).show();
                                getSupportDrawer().deselect(2);
                                return false;
                            }
                        })
        );

        getSupportDrawer().setSelection(-1);
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_search);
        layoutManager = new LinearLayoutManager(this);

        try {
            recyclerView.setHasFixedSize(true);
        } catch (Exception ex) {
            Log.e(TAG, "setUpRecyclerView() -> ", ex);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MapsSearchAdapter(dataSearch, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        try {
            Log.d(TAG, "Canceling Timer...");
            usersLocationsLoaderTaskTimer.cancel();
        } catch (NullPointerException ex) {
            Log.d(TAG, "Can't cancel Timer -> ", ex);
        }


        try {
            Log.d(TAG, "Disconnecting GoogleClientApi...");
            googleApiClient.disconnect();
        } catch (NullPointerException ex) {
            Log.d(TAG, "Can't disconnect -> ", ex);
        }

        try {
            Log.d(TAG, "Sending location disconnect request...");
            DisconnectLocationTask disconnectLocationTask = new DisconnectLocationTask(sharedPrefUtils.getToken());
            disconnectLocationTask.execute();
        } catch (Exception ex) {
            Log.e(TAG, "onDestroy() -> Error occurred while disconnecting location. Trying again");
            DisconnectLocationTask disconnectLocationTask = new DisconnectLocationTask(sharedPrefUtils.getToken());
            disconnectLocationTask.execute();
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
            for (MarkerDataModel entry : markerPrimaryModels) {
                if (entry.getNameFull().toLowerCase().contains(newText.toLowerCase().trim()))
                    dataSearch.add(new MapsSearchDataModel(entry.getNameFull(), entry.getId()));
            }

            // Nothing was found. Show warning
            if (dataSearch.size() == 0) {
                Snackbar.make(rootView, "Ничего не найдено", Snackbar.LENGTH_LONG).show();
            }
        } else {
            for (MarkerDataModel entry : markerPrimaryModels) {
                dataSearch.add(new MapsSearchDataModel(entry.getNameFull(), entry.getId()));
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
                zoomToMarker(markerPrimaryModels.get(itemId - 1).getMarker());
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
            }
        });

    }

    /**
     * Adding default items to RecyclerView.Adapter
     */
    private void addDefaultSearchViewItems() {
        dataSearch.clear();

        for (MarkerDataModel entry : markerPrimaryModels) {
            dataSearch.add(new MapsSearchDataModel(entry.getNameFull(), entry.getId()));
            Log.d(TAG, "adding search item with/" + "i: " + entry.getId());

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
                zoomToMarker(markerPrimaryModels.get(itemId - 1).getMarker());
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
                    MapsActivity.this.googleMap = googleMap;

                    if (MapsActivity.this.googleMap != null) {
                        setUpMap();
                        initGoogleApiClient();
                    }
                }
            });
        }
    }

    /**
     * Setting Up GoogleMap
     */
    private void setUpMap() {
        // Camera start position
        LatLng nau = new LatLng(50.440259, 30.429853);
        CameraPosition cameraPosition_start = new CameraPosition.Builder()
                .target(nau)      // Sets the center of the map to NAU
                .zoom(17)                   // Sets the zoom
                .bearing(160)               // Sets the orientation of the camera to east
                .tilt(90)
                .build();                   // Creates a CameraPosition from the builder

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_start));

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
        new AsyncTask<String, Void, ArrayList<MarkerDataModel>>() {
            private MaterialDialog loadingDialog;

            @Override
            protected void onPreExecute() {
                loadingDialog = APIDialogs.ProgressDialogs.loading(activityContext);
                loadingDialog.show();
            }

            @Override
            protected ArrayList<MarkerDataModel> doInBackground(String... params) {
                ArrayList<MarkerDataModel> markerDataModels = new ArrayList<>();

                try {
                    APIHTTPUtils httpUtils = new APIHTTPUtils();
                    HashMap<String, String> requestParams = new HashMap<>();

                    requestParams.put("action", "getMarkers");
                    String markersData = httpUtils.sendPostRequestWithParams(params[0], requestParams);
                    Log.d(TAG, "Server response: " + markersData);

                    JSONArray jsonArray = new JSONArray(markersData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        markerDataModels.add(new MarkerDataModel(
                                MarkerDataModel.TYPE_PRIMARY,
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
                                "type: " + MarkerDataModel.TYPE_PRIMARY + "\n" +
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
                    Log.e(TAG, "setUpMap() -> ", ex);
                    return null;
                }

                return markerDataModels;
            }

            @Override
            protected void onPostExecute(ArrayList<MarkerDataModel> markerDataModels) {
                setMarkerPrimaryModels(markerDataModels);

                if (markerDataModels != null && markerDataModels.size() != 0) {

                    for (int i = 0; i < markerDataModels.size(); i++) {
                        MarkerDataModel markerDataModel = markerDataModels.get(i);

                        final Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(markerDataModel.getLat(), markerDataModel.getLng()))
                                .title(markerDataModel.getLabel()));

                        if (idFromIntent - 1 == i) {
                            Glide
                                    .with(activityContext)
                                    .load(markerDataModel.getIcon())
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            Log.d(TAG, "Glide -> Bitmap loaded: " +
                                                    "\nWidth: " + resource.getWidth() +
                                                    "\nHeight: " + resource.getHeight()
                                            );
                                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(resource));
                                            try {
                                                ((FloatingActionButton) findViewById(R.id.fab_route_bsheet)).hide();
                                            } catch (NullPointerException ex) {
                                                Log.e(TAG, "setUpMap() ->", ex);
                                            }
                                            zoomToMarker(marker);
                                        }
                                    });
                        } else {
                            Glide
                                    .with(activityContext)
                                    .load(markerDataModel.getIcon())
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            Log.d(TAG, "Glide -> Bitmap loaded: " +
                                                    "\nWidth: " + resource.getWidth() +
                                                    "\nHeight: " + resource.getHeight()
                                            );
                                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(resource));
                                        }
                                    });
                        }

                        markerDataModel.setMarker(marker);
                        markerAllHashMap.put(marker, markerDataModel);
                    }
                }
                loadingDialog.dismiss();
            }
        }.execute(APIUrl.RequestUrl.API);

        supportRoute.setRouteCallbacks(new Route.RouteCallbacks() {
            @Override
            public void onDrawSuccess() {
                // Animate & move camera to current position
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(getMyCoordinate())
                        .bearing(180)
                        .zoom(15)
                        .build()));
            }
        });

        //Обработчик нажатия на маркер
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                closeBottomSheet();

                if (marker.isInfoWindowShown())
                    marker.hideInfoWindow();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(marker.getPosition())
                        .zoom(18)
                        .tilt(90)
                        .bearing(160)
                        .build()));
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MarkerDataModel markerDataModel = markerAllHashMap.get(marker);
                try {
                    Log.d(TAG, "onMarkerClick() -> " + markerDataModel.getType());
                } catch (Exception ex) {
                    Log.e(TAG, "onMarkerClick() -> ", ex);
                }
                switch (markerDataModel.getType()) {
                    case MarkerDataModel.TYPE_PRIMARY: {
                        currentMarkerID = markerAllHashMap.get(marker).getId() - 1;

                        openBottomSheet(
                                markerDataModel.getNameShort(),
                                markerDataModel.getNameFull(),
                                markerDataModel.getInformation()
                        );
                        Log.d(TAG, "onMarkerClick() -> \nLabel: " + markerAllHashMap.get(marker).getNameShort() + "\nId: " + markerDataModel.getId());

                        // Manually open the window
                        marker.showInfoWindow();

                        break;
                    }
                    case MarkerDataModel.TYPE_PEOPLE: {
                        Log.d(TAG, "onMarkerClick() -> Marker type: TYPE_PEOPLE");
                        try {
                            closeBottomSheet();
                            Snackbar.make(rootView, markerDataModel.getUniqueId() + "\n" + markerDataModel.getRegisterTime(), Snackbar.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Log.e(TAG, "onMarkerClick() -> MarkerDataModel.TYPE_PEOPLE -> ", ex);
                        }
                        break;
                    }
                }

                // Animate to center
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(marker.getPosition())
                        .zoom(18)
                        .build()));

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

    public void setMarkerPrimaryModels(ArrayList<MarkerDataModel> markerModels) {
        this.markerPrimaryModels = markerModels;
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
            final FloatingActionButton sheetFABRoute = (FloatingActionButton) findViewById(R.id.fab_route_bsheet);

            // Main content
            final SliderLayout sheetContentSlider = (SliderLayout) findViewById(R.id.slider);
            final ExpandableTextView sheetContentExpandText = (ExpandableTextView) findViewById(R.id.text_expandable);

            final ImageView sheetContentImageGoogleMap = (ImageView) findViewById(R.id.google_map);
            final ImageView sheetContentImageGoogleStreet = (ImageView) findViewById(R.id.google_street);

            @Override
            protected void onPreExecute() {
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
                            drawPathToMarker(markerPrimaryModels.get(currentMarkerID).getLatLng());
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
                                    //AnimationSupport.Fade.fadeOut(activityContext, sheetOverlapHead);
                                    sheetOverlapHead.setVisibility(View.INVISIBLE);
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
                                //AnimationSupport.Fade.fadeIn(activityContext, sheetOverlapHead);
                                sheetOverlapHead.setVisibility(View.VISIBLE);
                                sheetOverlapHeadTitle.setText(markerPrimaryModels.get(currentMarkerID).getNameShort());
                                sheetOverlapHeadSubtitle.setText(markerPrimaryModels.get(currentMarkerID).getNameFull());

                                sheetCollapsingToolbarTitle.setText(markerPrimaryModels.get(currentMarkerID).getNameShort());

                                sheetToolbarTitle.setText(markerPrimaryModels.get(currentMarkerID).getNameFull());
                                sheetToolbar.setVisibility(View.INVISIBLE);

                                sheetFABRoute.setVisibility(View.VISIBLE);

                                /************************** Main container ********************/
                                sheetContentExpandText.setText(markerPrimaryModels.get(currentMarkerID).getInformation());

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

    public void closeBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void openBottomSheet(String nameShort, String nameFull, String information) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        try {
            final TextView sheetOverlapHeadTitle = (TextView) findViewById(R.id.title_sheet);
            final TextView sheetOverlapHeadSubtitle = (TextView) findViewById(R.id.subtitle_sheet);
            final TextView sheetCollapsingToolbarTitle = (TextView) findViewById(R.id.title_collapse_sheet);
            final TextView sheetToolbarTitle = (TextView) findViewById(R.id.toolbar_title_bsheet);

            final ExpandableTextView sheetContentExpandText = (ExpandableTextView) findViewById(R.id.text_expandable);

            sheetOverlapHeadTitle.setText(nameShort);
            sheetOverlapHeadSubtitle.setText(nameFull);
            sheetToolbarTitle.setText(nameFull);
            sheetCollapsingToolbarTitle.setText(nameShort);

            sheetContentExpandText.setText(information);
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
        currentMarkerID = markerAllHashMap.get(marker).getId() - 1;

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(marker.getPosition())
                .zoom(18)
                .tilt(90)
                .build()));

        marker.showInfoWindow();

        openBottomSheet(
                markerAllHashMap.get(marker).getNameShort(),
                markerAllHashMap.get(marker).getNameFull(),
                markerAllHashMap.get(marker).getInformation()
        );
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

    /**
     * Create new instance fo GoogleApiClient & LocationRequest
     */
    private void initGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Log.d(TAG, "GoogleApiClient -> onConnected()");

                            if (sharedPrefUtils.getSignedState()) {
                                LocationRequest locationRequest = new LocationRequest();
                                locationRequest.setInterval(1000);
                                locationRequest.setFastestInterval(1000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationRequest.setSmallestDisplacement(0);

                                try {
                                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            new RegisterLocationTask(location, sharedPrefUtils.getToken()).execute();
                                        }
                                    });
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.d(TAG, "GoogleApiClient -> onConnectionSuspended()");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.d(TAG, "GoogleApiClient() -> onConnectionFailed() -> " + connectionResult.getErrorMessage());
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }

        googleApiClient.connect();
    }

    public class RegisterLocationTask extends AsyncTask<Void, Void, String> {
        private APIHTTPUtils httpUtils = new APIHTTPUtils();
        private HashMap<String, String> params = new HashMap<>();
        private Location location;
        private String token;

        public RegisterLocationTask(Location location, String token) {
            this.location = location;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "RegisterLocationTask -> onPreExecute() -> Preparing to send location...");
        }

        @Override
        protected String doInBackground(Void... args) {
            Log.d(TAG, "RegisterLocationTask -> doInBackground() -> Sending started");

            if (location != null && !token.trim().equalsIgnoreCase("")) {
                params.put("token", token);
                params.put("lat", Double.toString(location.getLatitude()));
                params.put("lng", Double.toString(location.getLongitude()));
            } else {
                Log.e(TAG, "RegisterLocationTask -> doInBackground() -> Bad token of LatLng");
                return null;
            }

            return httpUtils.sendPostRequestWithParams(APIUrl.RequestUrl.REGISTER_LOCATION, params);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Log.e(TAG, "RegisterLocationTask -> onPostExecute() -> response is null");
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(response);
                String errorStatus = jsonObject.getString("error");
                if (errorStatus.equalsIgnoreCase("true")) {
                    Log.e(TAG, "RegisterLocationTask -> onPostExecute() -> Error occurred while sending coordinates" +
                            "Error message: " + jsonObject.getString("error_msg"));
                } else if (errorStatus.equalsIgnoreCase("false")) {
                    // Coordinates sent successfully
                    Log.d(TAG, "RegisterLocationTask -> onPostExecute() -> Coordinates sent successfully" +
                            "\nLatitude: " + location.getLatitude() +
                            "\nLongitude: " + location.getLongitude() +
                            "\nToken: " + token);
                } else {
                    Log.e(TAG, "RegisterLocationTask -> onPostExecute() -> Unknown error");
                }
            } catch (Exception ex) {
                Log.e(TAG, "RegisterLocationTask -> onPostExecute() -> ", ex);
            }
        }
    }

    public class DisconnectLocationTask extends AsyncTask<Void, Void, String> {
        private APIHTTPUtils httpUtils = new APIHTTPUtils();
        private HashMap<String, String> params = new HashMap<>();
        private String token;

        public DisconnectLocationTask(String token) {
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "DisconnectLocationTask -> onPreExecute() -> Preparing to send disconnecting request...");
        }

        @Override
        protected String doInBackground(Void... args) {
            Log.d(TAG, "DisconnectLocationTask -> doInBackground() -> Sending started");

            if (!token.trim().equalsIgnoreCase("")) {
                params.put("token", token);
            } else {
                Log.e(TAG, "DisconnectLocationTask -> doInBackground() -> Token is empty");
                return null;
            }

            return httpUtils.sendPostRequestWithParams(APIUrl.RequestUrl.DISCONNECT_LOCATION, params);
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Log.e(TAG, "DisconnectLocationTask -> onPostExecute() -> response is null");
                tryAgain();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(response);
                String errorStatus = jsonObject.getString("error");
                if (errorStatus.equalsIgnoreCase("true")) {
                    Log.e(TAG, "DisconnectLocationTask -> onPostExecute() -> Error occurred while disconnecting.\nTrying again..." +
                            "Error message: " + jsonObject.getString("error_msg"));

                    tryAgain();
                } else if (errorStatus.equalsIgnoreCase("false")) {
                    Log.d(TAG, "DisconnectLocationTask -> onPostExecute() -> Location disconnected" +
                            "\nToken: " + token);
                } else {
                    Log.e(TAG, "DisconnectLocationTask -> onPostExecute() -> Unknown error");
                }
            } catch (Exception ex) {
                Log.e(TAG, "DisconnectLocationTask -> onPostExecute() -> ", ex);
                Log.d(TAG, "Exception! Trying again...");
                tryAgain();
            }
        }

        private void tryAgain() {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException ex) {
                Log.e(TAG, "DisconnectLocationTask -> tryAgain() -> ", ex);
            }
            DisconnectLocationTask disconnectLocationTask = new DisconnectLocationTask(token);
            disconnectLocationTask.execute();
        }
    }

    public class UsersLocationsLoaderTask extends AsyncTask<Void, Void, String> {
        private APIHTTPUtils httpUtils = new APIHTTPUtils();
        private HashMap<String, String> params = new HashMap<>();
        private ArrayList<MarkerDataModel> markerDataModels = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "UsersLocationsLoaderTask -> onPreExecute() -> Preparing to send disconnecting request...");
        }

        @Override
        protected String doInBackground(Void... args) {
            Log.d(TAG, "UsersLocationsLoaderTask -> doInBackground() -> Sending started");

            String response = httpUtils.sendPostRequestWithParams(APIUrl.RequestUrl.GET_LOCATIONS, params);
            Log.d(TAG, "UsersLocationsLoaderTask -> doInBackground() -> Server response: " + response);

            if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                return APIHTTPUtils.ERROR_CONNECTION;
            } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                return APIHTTPUtils.ERROR_SERVER;
            } else if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                return APIHTTPUtils.ERROR_CONNECTION_TIMED_OUT;
            } else {
                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        markerDataModels.add(new MarkerDataModel(
                                MarkerDataModel.TYPE_PEOPLE,
                                jsonObject.getInt("id"),
                                jsonObject.getDouble("lat"),
                                jsonObject.getDouble("lng"),
                                jsonObject.getString("photo_url"),
                                jsonObject.getString("user_unique_id"),
                                jsonObject.getString("registered_at")
                        ));

                        Log.d(TAG, "UsersLocationsLoaderTask -> doInBackground() -> Added new MarkerDataModel with: " +
                                "\ntype: " + MarkerDataModel.TYPE_PEOPLE +
                                "\nid: " + jsonObject.getInt("id") +
                                "\nlat: " + jsonObject.getDouble("lat") +
                                "\nlng: " + jsonObject.getDouble("lng") +
                                "\nphoto_url: " + jsonObject.getString("photo_url") +
                                "\nuser_unique_id: " + jsonObject.getString("user_unique_id") +
                                "\nregistered_at: " + jsonObject.getString("registered_at"));

                    }

                } catch (Exception ex) {
                    Log.e(TAG, "UsersLocationsLoaderTask -> doInBackground() -> ", ex);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_SERVER)) {
                    Log.e(TAG, "UsersLocationsLoaderTask -> onPostExecute() -> ERROR_SERVER");
                    return;
                }
                if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                    Log.e(TAG, "UsersLocationsLoaderTask -> onPostExecute() -> ERROR_CONNECTION");
                    return;
                }
                if (response.equalsIgnoreCase(APIHTTPUtils.ERROR_CONNECTION)) {
                    Log.e(TAG, "UsersLocationsLoaderTask -> onPostExecute() -> ERROR_CONNECTION");
                    return;
                }
            }

            for (int i = 0; i < markerPeopleArrayList.size(); i++) {
                markerAllHashMap.remove(markerPeopleArrayList.get(i));
                markerPeopleArrayList.get(i).remove();
            }
            markerPeopleArrayList.clear();

            for (int i = 0; i < markerDataModels.size(); i++) {
                MarkerDataModel markerDataModel = markerDataModels.get(i);

                final Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(markerDataModel.getLat(), markerDataModel.getLng()))
                        .title(markerDataModel.getUniqueId()));

                Glide
                        .with(activityContext)
                        .load(markerDataModel.getPhotoUrl())
                        .asBitmap()
                        .override(
                                (int) Utils.convertDpToPixel(40, activityContext),
                                (int) Utils.convertDpToPixel(40, activityContext)
                        )
                        .transform(new CircleTransform(activityContext))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                Log.d(TAG, "Glide -> Bitmap loaded: " +
                                        "\nWidth: " + resource.getWidth() +
                                        "\nHeight: " + resource.getHeight()
                                );
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(resource));
                            }
                        });

                markerDataModel.setMarker(marker);
                markerAllHashMap.put(marker, markerDataModel);
                markerPeopleArrayList.add(marker);
            }
        }
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