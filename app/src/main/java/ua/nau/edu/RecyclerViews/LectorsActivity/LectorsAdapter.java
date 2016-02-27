package ua.nau.edu.RecyclerViews.LectorsActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.NAU_Guide.UserProfileActivity;
import ua.nau.edu.Systems.CircleTransform;

public class LectorsAdapter extends RecyclerView.Adapter<LectorsAdapter.BaseViewHolder> {

    private static final String TAG = "LectorsAdapter";

    private ArrayList<LectorsDataModel> dataSet;
    private Context context;

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewId;
        ImageView imageView;
        ImageView clickLayout;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewId = (TextView) itemView.findViewById(R.id.textViewId);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.clickLayout = (ImageView) itemView.findViewById(R.id.click_view);
        }
    }

    public LectorsAdapter(ArrayList<LectorsDataModel> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lectors, parent, false);

        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        TextView textViewId = holder.textViewId;
        ImageView imageView = holder.imageView;
        ImageView clickLayout = holder.clickLayout;

        textViewName.setText(dataSet.get(listPosition).getName());
        textViewId.setText(dataSet.get(listPosition).getInstitute());
        Picasso.with(context)
                .load(Uri.parse(dataSet.get(listPosition).getPhotoUrl()))
                .transform(new CircleTransform()).into(imageView);

        clickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clickLayout clicked");
                context.startActivity(new Intent(context, UserProfileActivity.class)
                        .putExtra("action", "getPage")
                        .putExtra("uniqueId", dataSet.get(listPosition).getUniqueId()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setDataSet(ArrayList<LectorsDataModel> newDataSet) {
        this.dataSet = newDataSet;
    }
}