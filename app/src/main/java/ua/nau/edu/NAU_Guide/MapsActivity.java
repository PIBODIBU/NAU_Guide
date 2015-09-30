package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;

import ua.nau.edu.University.NAU;
import ua.nau.edu.University.University;

public class MapsActivity extends BaseNavigationDrawerActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public MapsActivity() {
    }

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private NAU university;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private TextView titleSlidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        university = new NAU();
        university.init();

        SharedPreferences settings = getSharedPreferences("VK_PREFERENCES", MainActivity.MODE_PRIVATE);

        getDrawer(
                settings.getString("VK_INFO_KEY", ""),
                settings.getString("VK_PHOTO_KEY", ""),
                settings.getString("VK_EMAIL_KEY", ""),
                settings.getBoolean("VK_SIGNED_KEY", false)
        );

        setUpMapIfNeeded();

        initSlidingPanel();

        Toast toast = Toast.makeText(getApplicationContext(),
                "Пора покормить кота!", Toast.LENGTH_SHORT);
        toast.show();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nau, 17));

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
                int id = getMarkerId(marker);

                //Отображение названия объекта
                titleSlidingLayout.setText(marker.getTitle());

                //Отображаем слайдер
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                return false;
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
        this.slidingUpPanelLayout.setShadowHeight(100);
        //пока не понятно что это
        this.slidingUpPanelLayout.setClipPanel(false);
        //пока не понятно что это
        this.slidingUpPanelLayout.setAnchorPoint(100.0f);
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
