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

    private void setUpMap() {
        LatLng nau = new LatLng(50.437476, 30.428322);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nau, 15));

        for (Integer i : university.getCorps().keySet()) {
            mMap.addMarker(new MarkerOptions()
                    .position(university.getCorps().get(i))
                    .title(i + " " + getString(R.string.corp)))

                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker)); // Custom icon
                    //.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); // Default icons
        }
    }

    public boolean onMarkerClick (Marker marker) {
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
