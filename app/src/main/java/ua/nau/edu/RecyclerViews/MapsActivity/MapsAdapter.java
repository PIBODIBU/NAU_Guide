package ua.nau.edu.RecyclerViews.MapsActivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ua.nau.edu.NAU_Guide.R;


public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.BaseViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private ArrayList<MapsDataModel> dataSet;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
        }
    }

    public MapsAdapter(ArrayList<MapsDataModel> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_maps, parent, false);

        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int listPosition) {
        TextView textViewName = holder.textViewName;

        textViewName.setText(dataSet.get(listPosition).getName());

        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clickLayout clicked");
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(dataSet.get(listPosition).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setDataSet(ArrayList<MapsDataModel> newDataSet) {
        this.dataSet = newDataSet;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(int itemId);
    }
}
