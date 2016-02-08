package ua.nau.edu.RecyclerViews.NewsActivity;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.Systems.CircleTransform;

public class NewsAdapter_OLD extends RecyclerView.Adapter<NewsAdapter_OLD.MyViewHolder> {

    private ArrayList<NewsDataModel> dataSet;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ExpandableTextView postMessage;
        TextView postTitle;
        TextView postSubTitle;
        ImageView authorImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.authorImage = (ImageView) itemView.findViewById(R.id.header_image);
            this.postTitle = (TextView) itemView.findViewById(R.id.header_title);
            this.postSubTitle = (TextView) itemView.findViewById(R.id.header_subtitle);
            this.postMessage = (ExpandableTextView) itemView.findViewById(R.id.post_text_expand);
        }
    }

    public NewsAdapter_OLD(ArrayList<NewsDataModel> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_news, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        ExpandableTextView postMessage = holder.postMessage;
        TextView postTitle = holder.postTitle;
        TextView postSubTitle = holder.postSubTitle;
        ImageView authorImage = holder.authorImage;

        postTitle.setText(dataSet.get(listPosition).getAuthor());
        postSubTitle.setText(dataSet.get(listPosition).getCreateTime());
        postMessage.setText(dataSet.get(listPosition).getMessage());
        Picasso.with(context).load(Uri.parse(dataSet.get(listPosition).getAuthorPhotoUrl())).transform(new CircleTransform()).into(authorImage);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}