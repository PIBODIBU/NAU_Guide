package ua.nau.edu.NAU_Guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ua.nau.edu.University.NAU;

public class MapsActivity extends BaseNavigationDrawerActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private int GLOBAL_MARKER_ID = -1;
    private String GLOBAL_MARKER_LABEL = "";
    private Marker activeMarker = null;
    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private static final String GLOBAL_SIGNED_KEY = "GLOBAL_SIGNED_KEY";

    SharedPreferences settings_global = null;
    SharedPreferences settings_vk = null;
    SharedPreferences.Editor editor_global;
    SharedPreferences.Editor editor_vk;

    /***
     * VKONTAKTE SDK VARIABLES
     ***/

    private int appId = 5084652;

    private static final String VK_PREFERENCES = "VK_PREFERENCES";
    private static final String VK_INFO_KEY = "VK_INFO_KEY";
    private static final String VK_PHOTO_KEY = "VK_PHOTO_KEY";
    private static final String VK_EMAIL_KEY = "VK_EMAIL_KEY";
    private static final String VK_SIGNED_KEY = "VK_SIGNED_KEY";
    private static final String VK_ID_KEY = "VK_ID_KEY";

    /*****/

    public MapsActivity() {
    }

    /***
     * VIEWS
     ***/

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private NAU university;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private TextView titleSlidingLayout;

    public Button button_photo;
    public Button button_scheme;
    public Button button_info;


    /*****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        university = new NAU();
        university.init();

// Get and set system services & Buttons & SharedPreferences & Requests
        settings_global = getSharedPreferences(GLOBAL_PREFERENCES, MODE_PRIVATE);
        settings_vk = getSharedPreferences(VK_PREFERENCES, MainActivity.MODE_PRIVATE);
        editor_global = settings_global.edit();
        editor_vk = settings_vk.edit();

        button_scheme = (Button) findViewById(R.id.button_scheme);
        button_info = (Button) findViewById(R.id.button_info);
//

        getDrawer(
                settings_vk.getString(VK_INFO_KEY, ""),
                settings_vk.getString(VK_PHOTO_KEY, ""),
                settings_vk.getString(VK_EMAIL_KEY, "")
        );

        setUpMapIfNeeded();

        initSlidingPanel();

        button_scheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, InfoActivity.class));
            }
        });

        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, InfoActivity.class)
                        .putExtra("Corp_id", GLOBAL_MARKER_ID)
                        .putExtra("Corp_label", GLOBAL_MARKER_LABEL));
            }
        });
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
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else
                    super.onBackPressed();
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
                    addMarker_custom(i, R.drawable.mark_cki, getString(R.string.cki));
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
                    addMarker_custom(i, R.drawable.mark_host, i - 16 + getString(R.string.host));
                    break;
                }
                /*case 18: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 +getString(R.string.sport));
                    break;
                }*/
                case 19: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 + getString(R.string.host));
                    break;
                }
                case 20: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 + getString(R.string.host));
                    break;
                }
                case 21: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 + getString(R.string.host));
                    break;
                }
                case 22: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 + getString(R.string.host));
                    break;
                }
                case 23: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 + getString(R.string.host));
                    break;
                }
                case 24: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 + getString(R.string.host));
                    break;
                }
                case 25: {
                    addMarker_custom(i, R.drawable.mark_host, i - 16 + getString(R.string.host));
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
                //Отображаем маленькую панель в 30dp+15dp
                slidingUpPanelLayout.setPanelHeight(45);

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
                        title_inst = "";
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
                        title_inst = "";
                        break;
                    }
                    case 11: {
                        title_inst = "ИИДС";
                        break;
                    }
                    case 12: {
                        title_inst = "";
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

                titleSlidingLayout.setText(title_inst);

                //Записываем id текущего маркера в глобальную переменную
                GLOBAL_MARKER_ID = getMarkerId(marker);

                //Записываем label текущего маркера в глобальную переменную
                GLOBAL_MARKER_LABEL = title_inst;

                activeMarker = marker;
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    activeMarker.showInfoWindow();
                } else
                    slidingUpPanelLayout.setPanelHeight(0);
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

    private void initSlidingPanel() {
        this.slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        //Высота слайдера в закрытом режиме
        this.slidingUpPanelLayout.setPanelHeight(0);
        //Фактор тени слайдера
        this.slidingUpPanelLayout.setShadowHeight(0);
        //пока не понятно что это
        this.slidingUpPanelLayout.setClipPanel(false);
        //пока не понятно что это
        this.slidingUpPanelLayout.setAnchorPoint(0.0f);
        //Увеличение нижнего отступа карты-елемента при открытии слайдера
        this.slidingUpPanelLayout.setParalaxOffset(0);

        this.titleSlidingLayout = (TextView) findViewById(R.id.titleSlidingLayout);
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