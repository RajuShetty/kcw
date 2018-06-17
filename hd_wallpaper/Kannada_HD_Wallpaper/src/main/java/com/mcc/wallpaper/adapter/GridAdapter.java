package com.mcc.wallpaper.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.data.sqlite.FavDbController;
import com.mcc.wallpaper.listener.OnItemClickListener;
import com.mcc.wallpaper.listener.OnLoadMoreListener;
import com.mcc.wallpaper.model.wallpaper.Wallpaper;
import com.mcc.wallpaper.utils.WallpaperUtils;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Wallpaper> wallpaperList;

    // Listener
    private OnItemClickListener mListener;
    private OnLoadMoreListener loadMoreListener;

    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;

    private boolean isLoading;

    private FavDbController favDbController;

    public GridAdapter(Context context, ArrayList<Wallpaper> wallpaperList) {
        this.context = context;
        this.wallpaperList = wallpaperList;
        favDbController = new FavDbController(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
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

            viewHolder.tvViewCount.setText(String.valueOf(wallpaperList.get(position).getPostViews()));

            if (favDbController.isFavorite(wallpaperList.get(position).getId())) {
                viewHolder.ivFavorite.setImageResource(R.drawable.ic_favorite_red);
            } else {
                viewHolder.ivFavorite.setImageResource(R.drawable.ic_favorite_white);
            }

            String imageUrl = wallpaperList.get(position).getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl();

            // thumbnail builder
            RequestBuilder<Drawable> thumbnailRequest = Glide
                    .with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().override(300));

            // load popular image
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.DATA))
                    .thumbnail(thumbnailRequest)
                    .into(viewHolder.ivCover);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return wallpaperList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvViewCount;
        private ImageView ivCover;
        private ImageView ivFavorite;
        private LinearLayout lytFavorite;

        public MyViewHolder(View itemView, final OnItemClickListener mListener) {
            super(itemView);

            tvViewCount = itemView.findViewById(R.id.tvViewCount);
            ivCover = itemView.findViewById(R.id.coverImageView);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            lytFavorite = itemView.findViewById(R.id.lytFavorite);

            // listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(view, getLayoutPosition());
                    }
                }
            });

            lytFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Wallpaper wallpaper = wallpaperList.get(getAdapterPosition());
                    String wallpaperType = WallpaperUtils.getType(wallpaper.getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl());

                    if (!favDbController.isFavorite(wallpaper.getId())) {
                        favDbController.addFavorite(wallpaper.getId(),
                                wallpaperType,
                                wallpaper.getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl(),
                                wallpaper.getPostViews());

                    } else {
                        favDbController.removeFavorite(wallpaper.getId());
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
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
