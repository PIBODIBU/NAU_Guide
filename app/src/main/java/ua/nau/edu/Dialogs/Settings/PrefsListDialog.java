package ua.nau.edu.Dialogs.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class PrefsListDialog extends DialogFragment {
    private Context context;
    private AlertDialog.Builder dialogBuilder;
    private LinearLayout parentRootView;
    private View rootView;
    private AlertDialog dialog;

    private PrefsListDialogAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<PrefsListDialogDataMode> dataSet;
    private RecyclerView recyclerView;

    public PrefsListDialog init(Context context, LinearLayout parentRootView) {
        this.context = context;
        this.parentRootView = parentRootView;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        rootView = inflater.inflate(R.layout.settings_item_prefs, null);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        dialogBuilder
                .setCancelable(true)
                .setView(rootView);

        dialog = dialogBuilder.create();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        SharedPreferences sharedPrefs = context.getSharedPreferences(SharedPrefUtils.APP_PREFERENCES, Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPrefs.getAll();

        dataSet = new ArrayList<>();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            dataSet.add(new PrefsListDialogDataMode(entry.getKey(), entry.getValue().toString()));
        }

        adapter = new PrefsListDialogAdapter(dataSet, context);
        recyclerView.setAdapter(adapter);

        return dialog;
    }

    public class PrefsListDialogAdapter extends RecyclerView.Adapter<PrefsListDialogAdapter.BaseViewHolder> {
        private ArrayList<PrefsListDialogDataMode> dataSet;
        private Context context;

        public PrefsListDialogAdapter(ArrayList<PrefsListDialogDataMode> data, Context context) {
            this.dataSet = data;
            this.context = context;
        }

        public class BaseViewHolder extends RecyclerView.ViewHolder {
            TextView key;
            TextView value;

            public BaseViewHolder(View itemView) {
                super(itemView);
                this.key = (TextView) itemView.findViewById(R.id.key);
                this.value = (TextView) itemView.findViewById(R.id.value);
            }
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item_prefs_recycler, parent, false);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return new BaseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BaseViewHolder holder, final int listPosition) {
            TextView key = holder.key;
            TextView value = holder.value;

            key.setText(dataSet.get(listPosition).getKey());
            value.setText(dataSet.get(listPosition).getValue());
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

    }

    public class PrefsListDialogDataMode {
        String key;
        String value;

        public PrefsListDialogDataMode(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}