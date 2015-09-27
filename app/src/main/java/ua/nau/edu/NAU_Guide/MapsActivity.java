package ua.nau.edu.NAU_Guide;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;

import ua.nau.edu.University.NAU;
import ua.nau.edu.University.University;

public class MapsActivity extends BaseNavigationDrawerActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private NAU university;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        university = new NAU();
        university.init();

        getDrawer();

        setUpMapIfNeeded();
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

                .setIcon(BitmapDescriptorFactory.fromResource(icon));
    }

    private void setUpMap() {
        LatLng nau = new LatLng(50.437476, 30.428322);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nau, 15));

        //.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); // Default icons
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
    }

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


}
