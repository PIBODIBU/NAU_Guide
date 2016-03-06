package ua.nau.edu.RecyclerViews.NewsActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import ua.nau.edu.API.APIValues;
import ua.nau.edu.NAU_Guide.R;
import ua.nau.edu.NAU_Guide.UserProfileActivity;
import ua.nau.edu.Support.Picasso.CircleTransform;
import ua.nau.edu.Support.SharedPrefUtils.SharedPrefUtils;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NewsDataModel> dataSet;
    private Context context;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private SharedPrefUtils sharedPrefUtils;

    private OnLoadMoreListener onLoadMoreListener;
    private OnDeleteMessageAction onDeleteMessageAction;
    private OnUpdateMessageAction onUpdateMessageAction;

    public NewsAdapter(ArrayList<NewsDataModel> data, Context context, RecyclerView recyclerView) {
        this.dataSet = data;
        this.context = context;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                Log.i("NewsAdapterTest", "totalItemCount = "
                        + Integer.toString(totalItemCount)
                        + "    lastVisibleItem= " + Integer.toString(lastVisibleItem)
                        + "  loading: " + loading
                );

                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loading = true;

                    // End has been reached
                    Log.i("NewsAdapterTest", "onScrolled: End reached");

                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public NewsAdapter(ArrayList<NewsDataModel> data, Context context, RecyclerView recyclerView, SharedPrefUtils sharedPrefUtils) {
        this.dataSet = data;
        this.context = context;
        this.sharedPrefUtils = sharedPrefUtils;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                Log.i("NewsAdapterTest", "totalItemCount = "
                        + Integer.toString(totalItemCount)
                        + "    lastVisibleItem= " + Integer.toString(lastVisibleItem)
                        + "  loading: " + loading
                );

                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loading = true;

                    // End has been reached
                    Log.i("NewsAdapterTest", "onScrolled: End reached");

                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public void setData(ArrayList<NewsDataModel> data) {
        this.dataSet = data;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSet.get(position) != null)
            return VIEW_ITEM;
        else
            return VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_news, parent, false);

            vh = new BaseViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_progress, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int listPosition) {
        if (holder instanceof BaseViewHolder) {
            final TextView postMessage = ((BaseViewHolder) holder).postMessage;
            TextView postTitle = ((BaseViewHolder) holder).postTitle;
            TextView postSubTitle = ((BaseViewHolder) holder).postSubTitle;
            ImageView authorImage = ((BaseViewHolder) holder).authorImage;
            final ImageButton popUpmenu = ((BaseViewHolder) holder).popUpMenu;
            final Button expandMessage = ((BaseViewHolder) holder).expandMessage;
            final ImageButton headClick = ((BaseViewHolder) holder).headClick;

            postTitle.setText(dataSet.get(listPosition).getAuthor().trim());
            postSubTitle.setText(dataSet.get(listPosition).getCreateTime().trim());
            postMessage.setText(dataSet.get(listPosition).getMessage());
            Picasso
                    .with(context)
                    .load(Uri.parse(dataSet.get(listPosition).getAuthorPhotoUrl()))
                    .transform(new CircleTransform())
                    .into(authorImage);

            String title = postTitle.getText().toString();
            if (title.length() > 20) {
                String titleSmall = title.substring(0, 20);
                titleSmall += "...";
                postTitle.setText(titleSmall);
            }

            headClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, UserProfileActivity.class)
                            .putExtra("action", "getPage")
                            .putExtra("uniqueId", dataSet.get(listPosition).getAuthorUniqueId()));
                }
            });

            if (sharedPrefUtils != null) {
                if (!sharedPrefUtils.getToken().equals("")) {
                    if (dataSet.get(listPosition).getAuthorUniqueId().equals(sharedPrefUtils.getUniqueId())) {
                        popUpmenu.setVisibility(View.VISIBLE);
                        popUpmenu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Creating the instance of PopupMenu
                                PopupMenu popup = new PopupMenu(context, popUpmenu);
                                //Inflating the Popup using xml file
                                popup.getMenuInflater().inflate(R.menu.menu_popup_news, popup.getMenu());

                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.item_delete: {
                                                onDeleteMessageAction.onDeleteCalled(dataSet.get(listPosition).getId(), listPosition);
                                                break;
                                            }
                                            case R.id.item_update: {
                                                onUpdateMessageAction.onUpdateCalled(dataSet.get(listPosition).getId(), dataSet.get(listPosition).getMessage());
                                            }
                                        }
                                        return true;
                                    }
                                });

                                popup.show();//showing popup menu
                            }
                        });
                    } else {
                        popUpmenu.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("NewsAdapter", "Token == \"\"");
                }
            } else {
                Log.e("NewsAdapter", "sharedPrefUtils == null");
            }

            postMessage.post(new Runnable() {
                @Override
                public void run() {
                    if (postMessage.getLineCount() > APIValues.maxLinesBeforeExpand) {
                        // Item has more, than APIValues.maxLinesBeforeExpand lines

                        if (!dataSet.get(listPosition).getExpandedState()) {
                            // Item isn't expanded

                            postMessage.setMaxLines(APIValues.maxLinesBeforeExpand);
                            expandMessage.setVisibility(View.VISIBLE);
                            expandMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    postMessage.setMaxLines(9999);
                                    expandMessage.setVisibility(View.GONE);
                                    dataSet.get(listPosition).setExpandedState(true);
                                }
                            });

                        } else {
                            // Item is expanded

                            postMessage.setMaxLines(9999);
                            expandMessage.setVisibility(View.GONE);
                        }
                    } else {
                        // Item has less, than APIValues.maxLinesBeforeExpand lines

                        expandMessage.setVisibility(View.GONE);
                    }

                }
            });

        } else {
            //((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        this.loading = false;
    }

    public void setLoading() {
        this.loading = true;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public MaterialProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            this.progressBar = (MaterialProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        TextView postMessage;
        TextView postTitle;
        TextView postSubTitle;
        ImageView authorImage;
        ImageButton popUpMenu;
        Button expandMessage;
        ImageButton headClick;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.authorImage = (ImageView) itemView.findViewById(R.id.header_image);
            this.postTitle = (TextView) itemView.findViewById(R.id.header_title);
            this.postSubTitle = (TextView) itemView.findViewById(R.id.header_subtitle);
            this.postMessage = (TextView) itemView.findViewById(R.id.post_text_expand);
            this.popUpMenu = (ImageButton) itemView.findViewById(R.id.popup_menu);
            this.expandMessage = (Button) itemView.findViewById(R.id.expand_message);
            this.headClick = (ImageButton) itemView.findViewById(R.id.header_click);
        }
    }

    private void rotateImgBtn(ImageButton imageButton, int degree) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        imageButton.startAnimation(rotateAnim);
    }

    /**
     * Setting new onLoadMoreListener
     *
     * @param onLoadMoreListener new OnLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        /**
         * Called when user reached end of RecyclerView. Is used for loading new data to RecyclerView.Adapter
         **/
        void onLoadMore();
    }

    /**
     * Setting new onDeleteMessageAction
     *
     * @param onDeleteMessageAction new OnDeleteMessageAction
     */
    public void setOnDeleteMessageAction(OnDeleteMessageAction onDeleteMessageAction) {
        this.onDeleteMessageAction = onDeleteMessageAction;
    }

    public interface OnDeleteMessageAction {
        /**
         * Called when user pushes delete button.
         *
         * @param postId         id of post to delete.
         * @param deletePosition position of RecyclerView.ViewHolder to delete.
         */
        void onDeleteCalled(int postId, int deletePosition);
    }

    /**
     * Setting new onUpdateMessageAction
     *
     * @param onUpdateMessageAction new OnUpdateMessageAction
     */
    public void setOnUpdateMessageAction(OnUpdateMessageAction onUpdateMessageAction) {
        this.onUpdateMessageAction = onUpdateMessageAction;
    }

    public interface OnUpdateMessageAction {
        /**
         * Called when user pushes update button.
         *
         * @param postId  - id of post to update.
         * @param message - text message to update.
         */
        void onUpdateCalled(int postId, String message);
    }

}