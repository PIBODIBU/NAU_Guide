package ua.nau.edu.Dialogs.Settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class MapLayerDialog extends DialogFragment implements AdapterView.OnItemClickListener {
    private SharedPrefUtils sharedPrefUtils;
    private Context context;
    private AlertDialog.Builder dialogBuilder;

    private LinearLayout parentRootView;
    private View rootView;
    private AlertDialog dialog;

    private String[] listItems = {"Без карты", "Схема", "Спутник", "Ландшафт", "Гибридная"};
    private ListView listView;

    public MapLayerDialog init(Context context, LinearLayout parentRootView) {
        this.context = context;
        this.parentRootView = parentRootView;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPrefUtils = new SharedPrefUtils(context);
        dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.settings_item_map_layer, null);

        listView = (ListView) rootView.findViewById(R.id.listView);

        dialogBuilder
                .setCancelable(true)
                .setView(rootView);

        dialog = dialogBuilder.create();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.settings_item_map_layer_list_item, listItems);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return dialog;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dialog.dismiss();
        sharedPrefUtils.setMapLayer(position);
    }
}
