package ua.nau.edu.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.HashMap;

import ua.nau.edu.NAU_Guide.R;

import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class AccountHeaderBgPicker extends DialogFragment {

    private static final String TAG = "AccountHeaderBgPicker";
    private HashMap<Integer, Integer> file_maps = new HashMap<>();

    private SharedPrefUtils sharedPrefUtils;

    private OnBackgroundChangedListener onBackgroundChangedListener;

    private AlertDialog.Builder dialogBuilder;

    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        sharedPrefUtils = new SharedPrefUtils(getActivity());

        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mainLayout = inflater.inflate(R.layout.accountheader_bg_picker, null);
        final SliderLayout sliderLayout = (SliderLayout) mainLayout.findViewById(R.id.slider);

        /**
         * SliderLayout setup
         */
        file_maps.put(0, R.drawable.material_bg_1);
        file_maps.put(1, R.drawable.material_bg_2);
        file_maps.put(2, R.drawable.material_bg_3);
        file_maps.put(3, R.drawable.material_bg_4);
        file_maps.put(4, R.drawable.material_bg_5);
        file_maps.put(5, R.drawable.material_bg_6);
        file_maps.put(6, R.drawable.material_bg_7);
        file_maps.put(7, R.drawable.material_bg_8);
        file_maps.put(8, R.drawable.material_bg_9);

        /*file_maps.put(2, R.drawable.material_bg_10);
        file_maps.put(3, R.drawable.material_bg_11);
        file_maps.put(4, R.drawable.material_bg_12);
        file_maps.put(5, R.drawable.material_bg_13);
        file_maps.put(6, R.drawable.material_bg_14);
        file_maps.put(4, R.drawable.material_bg_15);
        file_maps.put(5, R.drawable.material_bg_16);
        file_maps.put(6, R.drawable.material_bg_17);
        file_maps.put(2, R.drawable.material_bg_18);
        file_maps.put(3, R.drawable.material_bg_19);

        file_maps.put(4, R.drawable.material_bg_20);
        file_maps.put(5, R.drawable.material_bg_21);
        file_maps.put(6, R.drawable.material_bg_22);
        file_maps.put(4, R.drawable.material_bg_23);
        file_maps.put(5, R.drawable.material_bg_24);
        file_maps.put(6, R.drawable.material_bg_25);
        file_maps.put(5, R.drawable.material_bg_26);
        file_maps.put(6, R.drawable.material_bg_27);
        file_maps.put(4, R.drawable.material_bg_28);
        file_maps.put(5, R.drawable.material_bg_29);

        file_maps.put(6, R.drawable.material_bg_30);
        file_maps.put(6, R.drawable.material_bg_31);
        file_maps.put(6, R.drawable.material_bg_32);
        file_maps.put(6, R.drawable.material_bg_33);
        file_maps.put(6, R.drawable.material_bg_34);
        file_maps.put(6, R.drawable.material_bg_35);
        file_maps.put(6, R.drawable.material_bg_36);
        file_maps.put(6, R.drawable.material_bg_37);
        file_maps.put(6, R.drawable.material_bg_38);
        file_maps.put(6, R.drawable.material_bg_39);

        file_maps.put(6, R.drawable.material_bg_40);
        file_maps.put(6, R.drawable.material_bg_41);
        file_maps.put(6, R.drawable.material_bg_42);*/

        for (int i = 0; i < file_maps.size(); i++) {
            DefaultSliderView sliderView = new DefaultSliderView(getActivity());

            // initialize a SliderLayout
            sliderView
                    .image(file_maps.get(i))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            //.setOnSliderClickListener(getActivity());

            sliderLayout.addSlider(sliderView);
        }

        sliderLayout.stopAutoCycle();

        /**
         * AlertDialog.Builder setup
         */
        dialogBuilder
                //.setTitle("Выберите фон")
                .setView(mainLayout)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int currentImageId = getImageFromId(sliderLayout.getCurrentPosition());

                            Log.d(TAG, "sliderLayout current position: " + Integer.toString(sliderLayout.getCurrentPosition())
                                    + "\nsliderLayout current image id: " + Integer.toString(currentImageId));

                            // Saving current Background id in SharedPreferences
                            sharedPrefUtils.setAccountheaderBgImage(getImageFromId(sliderLayout.getCurrentPosition()));

                            if (onBackgroundChangedListener != null) {
                                onBackgroundChangedListener.onBackgroundChanged(currentImageId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        // Call method setCancelable() from AppCompatDialogFragment instead of AlertDialog.Builder
        setCancelable(false);

        // Creating AlertDialog from AlertDialog.Builder
        final AlertDialog dialog = dialogBuilder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogArg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAppPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            }
        });

        Log.d(TAG, "Dialog created");
        return dialog;
    }

    private int getImageFromId(int hashMapItemId) {
        return file_maps.get(hashMapItemId);
    }

    public void setOnBackgroundChangedListener(OnBackgroundChangedListener onBackgroundChangedListener) {
        this.onBackgroundChangedListener = onBackgroundChangedListener;
    }

    public interface OnBackgroundChangedListener {
        /**
         * @param imageId Id of selected image
         */
        void onBackgroundChanged(int imageId);
    }

}
