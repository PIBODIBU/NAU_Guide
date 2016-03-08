package ua.nau.edu.Dialogs.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import ua.nau.edu.NAU_Guide.Debug.AppUpdateActivity;
import ua.nau.edu.NAU_Guide.Debug.MapsTestActivity;
import ua.nau.edu.NAU_Guide.R;

public class DebugListDialog extends DialogFragment implements AdapterView.OnItemClickListener {
    private Context context;
    private AlertDialog.Builder dialogBuilder;
    private LinearLayout parentRootView;
    private View rootView;
    private AlertDialog dialog;

    private String[] listItems = {"MapsTestActivity", "AppUpdateActivity", "Test item 2", "Test item 3"};
    private ListView listView;

    public DebugListDialog init(Context context, LinearLayout parentRootView) {
        this.context = context;
        this.parentRootView = parentRootView;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.settings_item_debug, null);

        listView = (ListView) rootView.findViewById(R.id.listView);

        dialogBuilder
                .setCancelable(true)
                .setView(rootView);

        dialog = dialogBuilder.create();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.settings_item_debug_list_item, listItems);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return dialog;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                dialog.dismiss();
                startActivity(new Intent(context, MapsTestActivity.class));
                break;
            }
            case 1: {
                dialog.dismiss();
                startActivity(new Intent(context, AppUpdateActivity.class));
                break;
            }
            default: {
                Snackbar.make(parentRootView, "Position: " + Integer.toString(position) + " Id: " + Long.toString(id), Snackbar.LENGTH_LONG).show();
                break;
            }
        }
    }

}