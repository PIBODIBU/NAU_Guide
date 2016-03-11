package ua.nau.edu.Support.Picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PicassoMarker implements Target {
    private static final String TAG = "PicassoMarker";
    private Marker marker;
    private LoadingCallBacks loadingCallBacks;

    public PicassoMarker(Marker marker) {
        this.marker = marker;
    }

    public PicassoMarker(Marker marker, LoadingCallBacks loadingCallBacks) {
        this.marker = marker;
        this.loadingCallBacks = loadingCallBacks;
    }

    @Override
    public int hashCode() {
        return marker.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PicassoMarker) {
            Marker marker = ((PicassoMarker) o).marker;
            return this.marker.equals(marker);
        } else {
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        //Draw bitmap under user avatar

        /*
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap resultBitmap = Bitmap.createBitmap((int) Utils.convertDpToPixel(100f, context), (int) Utils.convertDpToPixel(100f, context), conf);
        Canvas canvas = new Canvas(resultBitmap);

        // paint defines the text color,
        // stroke width, size
        Paint paint = new Paint();
        paint.setTextSize(35);
        paint.setColor(Color.BLACK);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawText("It's me!", 30, 40, paint);
        */

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        Log.d(TAG, "onBitmapLoaded() -> " +
                "\nBitmap:" +
                "\n\twidth: " + bitmap.getWidth() +
                "\n\theight: " + bitmap.getHeight() /*+
                "\nCanvas:" +
                "\n\twidth: " + resultBitmap.getWidth() +
                "\n\theight: " + resultBitmap.getHeight()*/
        );

        if (loadingCallBacks != null) {
            loadingCallBacks.onLoaded(bitmap);
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.e(TAG, "onBitmapFailed()");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.d(TAG, "onPrepareLoad()");
    }

    public void setLoadingCallBacks(LoadingCallBacks loadingCallBacks) {
        this.loadingCallBacks = loadingCallBacks;
    }

    public interface LoadingCallBacks {
        void onLoaded(Bitmap bitmap);
    }
}