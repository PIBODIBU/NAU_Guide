package ua.nau.edu.NAU_Guide;


import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class MapsActivity extends BaseNavigationDrawerActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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
        LatLng nau = new LatLng(50.437476,30.428322);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nau,18));
    }

    public boolean onMarkerClick(Marker marker) {

        //Manually open the window
        marker.showInfoWindow();

        //Animate to center
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        //Consume the method
        return true;
    }
}
