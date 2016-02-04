package ua.nau.edu.RecyclerViews.LectorsActivity;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.Systems.CircleTransform;

public class LectorsAdapter extends RecyclerView.Adapter<LectorsAdapter.MyViewHolder> {

    private ArrayList<LectorsDataModel> dataSet;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewId;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewId = (TextView) itemView.findViewById(R.id.textViewId);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public LectorsAdapter(ArrayList<LectorsDataModel> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lectors, parent, false);

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
        TextView textViewId = holder.textViewId;
        ImageView imageView = holder.imageView;

        textViewName.setText(dataSet.get(listPosition).getName());
        textViewId.setText(dataSet.get(listPosition).getInstitute());
        Picasso.with(context).load(Uri.parse(dataSet.get(listPosition).getPhotoUrl())).transform(new CircleTransform()).into(imageView);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}