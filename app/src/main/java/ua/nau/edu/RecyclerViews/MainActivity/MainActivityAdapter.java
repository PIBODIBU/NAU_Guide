package ua.nau.edu.RecyclerViews.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.CustomView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ua.nau.edu.NAU_Guide.MapsActivity;
import ua.nau.edu.NAU_Guide.R;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MyViewHolder> {

    private ArrayList<MainActivityDataModel> dataSet;
    private Context context;

    View.OnClickListener mOnClickListener;

    public void setClickListener(View.OnClickListener l) {
        mOnClickListener = l;
    }

    View.OnClickListener mClicks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != mOnClickListener) {
                mOnClickListener.onClick(v);
            }
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewVersion;
        ImageView imageViewIcon;
        CustomView buttonToMap;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.buttonToMap = (CustomView) itemView.findViewById(R.id.button_map_1);
        }
    }

    public MainActivityAdapter(ArrayList<MainActivityDataModel> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;
        CustomView buttonToMap = holder.buttonToMap;

        textViewName.setText(dataSet.get(listPosition).getName());
        textViewVersion.setText(dataSet.get(listPosition).getVersion());
        Picasso.with(context).load(dataSet.get(listPosition).getImage()).into(imageView);

        buttonToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MapsActivity.class).putExtra("MAINACTIVITY_CORP_ID", listPosition + 1));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}