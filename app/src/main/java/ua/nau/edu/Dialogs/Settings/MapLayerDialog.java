package ua.nau.edu.Dialogs.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class MapLayerDialog extends AppCompatDialogFragment {
    private static final String TAG = "MapLayerDialog";

    private SharedPrefUtils sharedPrefUtils;
    private Context context;
    private AlertDialog.Builder dialogBuilder;

    private LinearLayout parentRootView;
    private View rootView;
    private AlertDialog dialog;

    private String[] listItems = {"Без карты", "Схема", "Спутник", "Ландшафт", "Гибридная"};

    public MapLayerDialog init(Context context, LinearLayout parentRootView) {
        this.context = context;
        this.parentRootView = parentRootView;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPrefUtils = new SharedPrefUtils(context);
        dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.settings_item_map_layer, null);

        dialogBuilder
                .setTitle("Слой карты")
                .setView(rootView)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        setCancelable(false);

        dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAppPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        });

        final RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        try {
            ((AppCompatRadioButton) radioGroup.getChildAt(sharedPrefUtils.getMapLayer())).setChecked(true);
        } catch (Exception ex) {
            Log.e(TAG, "onCreateDialog() -> ", ex);
            ((AppCompatRadioButton) radioGroup.getChildAt(1)).setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppCompatRadioButton radioButton = (AppCompatRadioButton) group.findViewById(checkedId);
                int index = group.indexOfChild(radioButton);

                Log.d(TAG, "onCheckedChanged) -> checkedId = " + index);
                sharedPrefUtils.setMapLayer(index);
            }
        });

        return dialog;
    }
}
