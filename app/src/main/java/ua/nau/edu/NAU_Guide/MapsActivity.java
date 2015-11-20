package ua.nau.edu.NAU_Guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ua.nau.edu.Enum.EnumSharedPreferences;
import ua.nau.edu.Enum.EnumSharedPreferencesVK;
import ua.nau.edu.University.NAU;

public class MapsActivity extends BaseNavigationDrawerActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private int GLOBAL_MARKER_ID = -1;
    private String GLOBAL_MARKER_LABEL = "";

    public MapsActivity() {
    }

    /***
     * VIEWS
     ***/

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private NAU university;

    private FloatingActionMenu fab_menu;

    /*****/

    private static final String APP_PREFERENCES = EnumSharedPreferences.APP_PREFERENCES.toString();
    private static final String VK_PREFERENCES = EnumSharedPreferencesVK.VK_PREFERENCES.toString();
    private static final String VK_INFO_KEY = EnumSharedPreferencesVK.VK_INFO_KEY.toString();
    private static final String VK_EMAIL_KEY = EnumSharedPreferencesVK.VK_EMAIL_KEY.toString();
    private static final String CORP_ID_KEY = EnumSharedPreferences.CORP_ID_KEY.toString();
    private static final String CORP_LABEL_KEY = EnumSharedPreferences.CORP_LABEL_KEY.toString();

    private SharedPreferences settings = null;
    private SharedPreferences settingsVK = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        university = new NAU();
        university.init();

// Get and set system services & Buttons & SharedPreferences & Requests
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        settingsVK = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);


        getDrawer(
                settingsVK.getString(VK_INFO_KEY, ""),
                settingsVK.getString(VK_EMAIL_KEY, "")
        );

        setUpMapIfNeeded();
        setUpMapIfNeeded();

        initFloatingActionMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();
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

    private void addMarker_custom(Integer i, int icon, String title) {
        mMap.addMarker(new MarkerOptions()
                .position(university.getCorps().get(i))
                .title(title))
                .setIcon(BitmapDescriptorFactory.fromResource(icon)
                );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                /*if () {
                } else
                    super.onBackPressed();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void setUpMap() {
        //Стартовое положение камеры
        LatLng nau = new LatLng(50.437476, 30.428322);
        CameraPosition cameraPosition_start = new CameraPosition.Builder()
                .target(nau)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(160)                // Sets the orientation of the camera to east
                        //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_start));

        mMap.getUiSettings().setMapToolbarEnabled(false);

        //Добавление маркеров на карту из класса НАУ
        for (Integer i : university.getCorps().keySet()) {
            switch (i) {
                case 1: {
                    addMarker_custom(i, R.drawable.corp_1, i + getString(R.string.corp));
                    break;
                }
                case 2: {
                    addMarker_custom(i, R.drawable.corp_2, i + getString(R.string.corp));
                    break;
                }
                case 3: {
                    addMarker_custom(i, R.drawable.corp_3, i + getString(R.string.corp));
                    break;
                }
                case 4: {
                    addMarker_custom(i, R.drawable.corp_4, i + getString(R.string.corp));
                    break;
                }
                case 5: {
                    addMarker_custom(i, R.drawable.corp_5, i + getString(R.string.corp));
                    break;
                }
                case 6: {
                    addMarker_custom(i, R.drawable.corp_6, i + getString(R.string.corp));
                    break;
                }
                case 7: {
                    addMarker_custom(i, R.drawable.corp_7, i + getString(R.string.corp));
                    break;
                }
                case 8: {
                    addMarker_custom(i, R.drawable.corp_8, i + getString(R.string.corp));
                    break;
                }
                case 9: {
                    addMarker_custom(i, R.drawable.corp_9, i + getString(R.string.corp));
                    break;
                }
                case 10: {
                    addMarker_custom(i, R.drawable.corp_10, i + getString(R.string.corp));
                    break;
                }
                case 11: {
                    addMarker_custom(i, R.drawable.corp_11, i + getString(R.string.corp));
                    break;
                }
                case 12: {
                    addMarker_custom(i, R.drawable.corp_12, i + getString(R.string.corp));
                    break;
                }
                case 13: {
                    addMarker_custom(i, R.drawable.mark_ckm, getString(R.string.cki));
                    break;
                }
                case 14: {
                    addMarker_custom(i, R.drawable.mark_bistro, getString(R.string.bistro));
                    break;
                }
                case 15: {
                    addMarker_custom(i, R.drawable.mark_med, getString(R.string.med));
                    break;
                }
                case 16: {
                    addMarker_custom(i, R.drawable.mark_sport, getString(R.string.sport));
                    break;
                }
                case 17: {
                    addMarker_custom(i, R.drawable.host_1, i - 16 + getString(R.string.host));
                    break;
                }
                /*case 18: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 +getString(R.string.sport));
                    break;
                }*/
                case 19: {
                    addMarker_custom(i, R.drawable.host_3, i - 16 + getString(R.string.host));
                    break;
                }
                case 20: {
                    addMarker_custom(i, R.drawable.host_4, i - 16 + getString(R.string.host));
                    break;
                }
                case 21: {
                    addMarker_custom(i, R.drawable.host_5, i - 16 + getString(R.string.host));
                    break;
                }
                case 22: {
                    addMarker_custom(i, R.drawable.host_6, i - 16 + getString(R.string.host));
                    break;
                }
                case 23: {
                    addMarker_custom(i, R.drawable.host_7, i - 16 + getString(R.string.host));
                    break;
                }
                case 24: {
                    addMarker_custom(i, R.drawable.host_8, i - 16 + getString(R.string.host));
                    break;
                }
                case 25: {
                    addMarker_custom(i, R.drawable.host_9, i - 16 + getString(R.string.host));
                    break;
                }
                case 26: {
                    addMarker_custom(i, R.drawable.host_10, i - 16 + getString(R.string.host));
                    break;
                }
                case 27: {
                    addMarker_custom(i, R.drawable.host_11, i - 16 + getString(R.string.host));
                    break;
                }
                default: {
                    break;
                }
            }
        }

        //Обработчик нажатия на маркер
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Отображение названия объекта
                String title_inst = "";

                switch (getMarkerId(marker)) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        title_inst = "ЮИ";
                        break;
                    }
                    case 2: {
                        title_inst = "ИЕМ";
                        break;
                    }
                    case 3: {
                        title_inst = "ИАП";
                        break;
                    }
                    case 4: {
                        title_inst = "CORP";
                        break;
                    }
                    case 5: {
                        title_inst = "ИАН";
                        break;
                    }
                    case 6: {
                        title_inst = "ИКИТ";
                        break;
                    }
                    case 7: {
                        title_inst = "ИМО";
                        break;
                    }
                    case 8: {
                        title_inst = "ГМИ";
                        break;
                    }
                    case 9: {
                        title_inst = "НДИ-Дизайн";
                        break;
                    }
                    case 10: {
                        title_inst = "CORP";
                        break;
                    }
                    case 11: {
                        title_inst = "ИИДС";
                        break;
                    }
                    case 12: {
                        title_inst = "CORP";
                        break;
                    }
                    case 13: {
                        title_inst = "ЦКИ";
                        break;
                    }
                    case 14: {
                        title_inst = "Бистро";
                        break;
                    }
                    case 15: {
                        title_inst = "Мед.центр";
                        break;
                    }
                    case 16: {
                        title_inst = "Спорткомплекс";
                        break;
                    }
                    default: {
                        break;
                    }
                }

                //Записываем id текущего маркера в глобальную переменную
                GLOBAL_MARKER_ID = getMarkerId(marker);

                //Записываем label текущего маркера в глобальную переменную
                GLOBAL_MARKER_LABEL = title_inst;

                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (fab_menu.isOpened()) {
                    fab_menu.close(true);
                    GLOBAL_MARKER_ID = -1;
                    GLOBAL_MARKER_LABEL = "";
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
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

        fab_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!GLOBAL_MARKER_LABEL.equals("") && GLOBAL_MARKER_ID != 0) {
                    startActivity(new Intent(MapsActivity.this, InfoActivity.class)
                            .putExtra(CORP_ID_KEY, GLOBAL_MARKER_ID)
                            .putExtra(CORP_LABEL_KEY, GLOBAL_MARKER_LABEL));
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
//

}