package ua.nau.edu.Support.System;

import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;

import java.io.IOException;

/**
 * Class is used for checking different Android hardware components (e.g. GPS, Wifi state, etc)
 */
public class HardwareChecks {
    /**
     * Method used for checking connection to the Internet
     *
     * @return true - if Internet connection access to the Internet is available
     * false - if Internet connection access to the Internet is not available
     */
    public static boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Used for checking GPS state
     *
     * @param context Activity context
     * @return true - if GPS is enabled
     * false - if GPS is disabled
     */
    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Used for checking Wifi state
     *
     * @param context Activity context
     * @return true - if Wifi is enabled
     * false - if Wifi is disabled
     */
    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            return wifiManager.isWifiEnabled();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
