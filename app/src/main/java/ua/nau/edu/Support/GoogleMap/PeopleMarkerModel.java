package ua.nau.edu.Support.GoogleMap;

import android.graphics.Bitmap;

public class PeopleMarkerModel {
    private int markerId;
    private String userId;
    private Bitmap photoSmall;

    public PeopleMarkerModel(int markerId, String userId, Bitmap photoSmall) {
        this.markerId = markerId;
        this.userId = userId;
        this.photoSmall = photoSmall;
    }

    public int getMarkerId() {
        return markerId;
    }

    public String getUserId() {
        return userId;
    }

    public Bitmap getPhotoSmall() {
        return photoSmall;
    }
}
