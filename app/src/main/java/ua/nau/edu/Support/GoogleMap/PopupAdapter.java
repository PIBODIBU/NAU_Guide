package ua.nau.edu.Support.GoogleMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import ua.nau.edu.NAU_Guide.R;

public class PopupAdapter implements InfoWindowAdapter {
    private static final String TAG = "PopupAdapter";

    private View popup = null;
    private LayoutInflater inflater = null;
    private Activity activity;
    private ImageView avatarSmall;
    private String snippet;

    public PopupAdapter(Activity activity, LayoutInflater inflater) {
        this.activity = activity;
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return (null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(final Marker marker) {
        if (popup == null) {
            popup = inflater.inflate(R.layout.map_popup, null);
        }

        String title = marker.getTitle();
        snippet = marker.getSnippet();

        Log.d(TAG, "getInfoContents() -> \ntitle: " + title + "\nsnippet:" + snippet);

        TextView titleTV = (TextView) popup.findViewById(R.id.title);
        TextView snippetTV = (TextView) popup.findViewById(R.id.snippet);
        avatarSmall = (ImageView) popup.findViewById(R.id.avatar_small);

        titleTV.setText(title);


        return popup;
    }
}