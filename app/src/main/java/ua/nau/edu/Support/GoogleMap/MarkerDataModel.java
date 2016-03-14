package ua.nau.edu.Support.GoogleMap;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerDataModel {

    public MarkerDataModel() {
    }

    private final String TAG = "MarkerDataModel";

    public static final int TYPE_PRIMARY = 1;
    public static final int TYPE_PEOPLE = 2;

    private int type;
    private int id;
    private double lat;
    private double lng;
    private Marker marker;

    /**
     * TYPE_PRIMARY
     **/
    private String icon;
    private String label;
    private String nameShort;
    private String nameFull;
    private String phone;
    private String information;
    private String website;
    private String sliderImages;

    /**
     * TYPE_PEOPLE
     **/
    private String photoUrl;
    private String uniqueId;
    private String registerTime;

    public MarkerDataModel(int type,
                           int id,
                           double lat,
                           double lng,
                           String icon,
                           String label,
                           String nameShort,
                           String nameFull,
                           String phone,
                           String information,
                           String website,
                           String sliderImages) {

        this.type = type;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.icon = icon;
        this.label = label;
        this.nameShort = nameShort;
        this.nameFull = nameFull;
        this.phone = phone;
        this.information = information;
        this.website = website;
        this.sliderImages = sliderImages;
    }

    public MarkerDataModel(int type,
                           int id,
                           double lat,
                           double lng,
                           String photoUrl,
                           String uniqueId,
                           String registerTime) {
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.photoUrl = photoUrl;
        this.uniqueId = uniqueId;
        this.registerTime = registerTime;
    }

    /**
     * Getters
     */

    public int getType() {
        return type;
    }

    @Nullable
    public Marker getMarker() {
        return marker;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    /**
     * TYPE_PRIMARY
     **/

    public String getIcon() {
        return icon;
    }

    public String getInformation() {
        return information;
    }

    public String getLabel() {
        return label;
    }

    public String getNameFull() {
        return nameFull;
    }

    public String getNameShort() {
        return nameShort;
    }

    public String getPhone() {
        return phone;
    }

    public String getSliderImages() {
        return sliderImages;
    }

    public String getWebsite() {
        return website;
    }


    /**
     * TYPE_PEOPLE
     **/
    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getRegisterTime() {
        return registerTime;
    }


    @Nullable
    public LatLng getLatLng() {
        try {
            return new LatLng(lat, lng);
        } catch (Exception ex) {
            Log.e(TAG, "getLatLng() -> ", ex);
            return null;
        }
    }

    /**
     * Setters
     */

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
