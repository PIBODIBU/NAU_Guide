package ua.nau.edu.NAU_Guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ua.nau.edu.University.NAU;

public class MapsActivity extends BaseNavigationDrawerActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String GLOBAL_PREFERENCES = "GLOBAL_PREFERENCES";
    private static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";

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
//

        getDrawer(
                settings_vk.getString(VK_INFO_KEY, ""),
                settings_vk.getString(VK_PHOTO_KEY, ""),
                settings_vk.getString(VK_EMAIL_KEY, "")
        );

        setUpMapIfNeeded();

        initSlidingPanel();

        Toast toast = Toast.makeText(getApplicationContext(),
                "Пора покормить кота!", Toast.LENGTH_SHORT);
        toast.show();

        /*TextView text = (TextView) findViewById(R.id.titleSlidingLayout);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShowShort("text clicked");
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });*/

        button_photo = (Button) findViewById(R.id.button_photo);
        button_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, FloorActivity.class));
            }
        });

        button_scheme = (Button) findViewById(R.id.button_scheme);
        button_scheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, FloorActivity.class));
            }
        });

        button_info = (Button) findViewById(R.id.button_info);
        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, FloorActivity.class));
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

    private void setUpMap() {
        //Стартовое положение камеры
        LatLng nau = new LatLng(50.437476, 30.428322);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nau, 15));

        //Добавление маркеров на карту из класса НАУ
        for (Integer i : university.getCorps().keySet()) {
            if (i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 10 || i == 11 || i == 12) {
                addMarker_custom(i, R.drawable.mark_corp, i + getString(R.string.corp));
            } else if (i == 13) { // CKI
                addMarker_custom(i, R.drawable.mark_cki, getString(R.string.cki));
            } else if (i == 14) { // BISTRO
                addMarker_custom(i, R.drawable.mark_bistro, getString(R.string.bistro));
            } else if (i == 15) { // MED CENTER
                addMarker_custom(i, R.drawable.mark_med, getString(R.string.med));
            } else if (i == 16) { // SPORT
                addMarker_custom(i, R.drawable.mark_sport, getString(R.string.sport));
            }
        }

        //Обработчик нажатия на маркер
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Получаем идентефикатор маркера
                Toast.makeText(getApplicationContext(), Integer.toString(getMarkerId(marker)), Toast.LENGTH_SHORT).show();

                //Отображаем маленькую панель в 30dp+15dp
                slidingUpPanelLayout.setPanelHeight(45);

                //Отображение названия объекта
                titleSlidingLayout.setText(marker.getTitle());

                //Отображаем слайдер
                //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else if (slidingUpPanelLayout.getPanelHeight() != 0) {
                    slidingUpPanelLayout.setPanelHeight(0);
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

    private void initSlidingPanel() {
        this.slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        //Высота слайдера в закрытом режиме
        this.slidingUpPanelLayout.setPanelHeight(0);
        //Фактор тени слайдера
        this.slidingUpPanelLayout.setShadowHeight(0);
        //пока не понятно что это
        this.slidingUpPanelLayout.setClipPanel(true);
        //пока не понятно что это
        this.slidingUpPanelLayout.setAnchorPoint(1000.0f);
        //Увеличение нижнего отступа карты-елемента при открытии слайдера
        this.slidingUpPanelLayout.setParalaxOffset(100);

        // TEST
        //this.slidingUpPanelLayout.hed

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
