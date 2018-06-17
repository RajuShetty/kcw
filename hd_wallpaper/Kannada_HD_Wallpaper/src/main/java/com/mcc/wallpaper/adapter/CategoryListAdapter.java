package com.mcc.wallpaper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.listener.OnItemClickListener;
import com.mcc.wallpaper.listener.OnLoadMoreListener;
import com.mcc.wallpaper.model.others.Category;
import com.mcc.wallpaper.utils.StringUtil;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Category> categoryList;

    // Listener
    private OnItemClickListener mListener;
    private OnLoadMoreListener loadMoreListener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private boolean isLoading;

    public CategoryListAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new MyViewHolder(view, mListener);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.tvCategoryName.setText(categoryList.get(position).getName());

            String coverUrl = StringUtil.removeAmp(categoryList.get(position).getDescription());
            // load category image
            Glide.with(context)
                    .load(coverUrl)
                    .into(viewHolder.ivCover);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return categoryList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCategoryName;
        private ImageView ivCover;

        public MyViewHolder(View itemView, final OnItemClickListener mListener) {
            super(itemView);

            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            ivCover = itemView.findViewById(R.id.coverImageView);

            // listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(view, getLayoutPosition());
                    }
                }
            });
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void loadMore() {
        loadMoreListener.onLoadMore();
    }

    public void loadingComplete() {
        this.isLoading = false;
    }

    public void setLoading() {
        this.isLoading = true;
    }

    public boolean isLoading() {
        return isLoading;
    }
}
