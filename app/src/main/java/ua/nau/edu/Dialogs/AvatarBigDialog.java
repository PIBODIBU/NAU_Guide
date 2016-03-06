package ua.nau.edu.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import ua.nau.edu.NAU_Guide.R;

public class AvatarBigDialog extends DialogFragment {

    private static final String TAG = "AvatarBigDialog";

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private ImageView avatarBig;

    private Activity activity;
    private String PHOTO_URL = "";
    private Bitmap image;

    public void init(Activity activity, String PHOTO_URL) {
        this.activity = activity;
        this.PHOTO_URL = PHOTO_URL;
    }

    public void init(Activity activity, Bitmap image) {
        this.activity = activity;
        this.image = image;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            PHOTO_URL = savedInstanceState.getString("PHOTO_URL");
        }

        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mainLayout = inflater.inflate(R.layout.dialog_avatar, null);

        avatarBig = (ImageView) mainLayout.findViewById(R.id.reveal_image);

        dialogBuilder
                .setCancelable(true)
                .setView(mainLayout);

        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if (PHOTO_URL != null && !PHOTO_URL.equals("")) {
            Log.d(TAG, "Loading big avatar...");
            Picasso
                    .with(activity)
                    .load(Uri.parse(PHOTO_URL))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(avatarBig);
        } else {
            avatarBig.setImageBitmap(image);
        }

        Log.d(TAG, "Dialog created");
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("PHOTO_URL", PHOTO_URL);

        avatarBig.buildDrawingCache();
        Bitmap avatarBigBitmap = avatarBig.getDrawingCache();
        outState.putParcelable("BIG_IMAGE", avatarBigBitmap);

        super.onSaveInstanceState(outState);
    }
}