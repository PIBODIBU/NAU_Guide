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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ua.nau.edu.University.NAU;

public class MapsActivity extends BaseNavigationDrawerActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private int GLOBAL_MARKER_ID = -1;
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

        button_photo = (Button) findViewById(R.id.button_photo);
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

        /*TextView text = (TextView) findViewById(R.id.titleSlidingLayout);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShowShort("text clicked");
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });*/

        button_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, InfoActivity.class));
            }
        });

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
                        .putExtra("Corp", GLOBAL_MARKER_ID));
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
                //Отображаем маленькую панель в 30dp+15dp
                slidingUpPanelLayout.setPanelHeight(45);

                //Отображение названия объекта
                String title_inst = "";

                switch (getMarkerId(marker)) {
                    case 0: {

                        break;
                    }
                    case 1: {

                        break;
                    }
                    case 2: {

                        break;
                    }
                    case 3: {

                        break;
                    }
                    case 4: {

                        break;
                    }
                    case 5: {

                        break;
                    }
                    case 6: {
                        title_inst = " ИКИТ";
                        break;
                    }
                    case 7: {

                        break;
                    }
                    case 8: {

                        break;
                    }
                    case 9: {

                        break;
                    }
                    case 10: {

                        break;
                    }
                    case 11: {

                        break;
                    }
                    case 12: {

                        break;
                    }
                    default: {
                        break;
                    }
                }

                titleSlidingLayout.setText(title_inst);

                //Записываем id текущего маркера в глобальную переменную
                GLOBAL_MARKER_ID = getMarkerId(marker);

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