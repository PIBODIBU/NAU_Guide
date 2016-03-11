package ua.nau.edu.Support.GoogleMap;

public class MarkerDataModel {

    private int id;
    private double lat;
    private double lng;
    private String icon;
    private String label;
    private String nameShort;
    private String nameFull;
    private String phone;

    private String information;
    private String website;
    private String sliderImages;

    public MarkerDataModel(int id,
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

    /**
     * Getters
     */
    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

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
}
