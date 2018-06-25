package com.kannadachristianwallpapers.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.listener.OnItemClickListener;
import com.kannadachristianwallpapers.app.model.others.Album;

import java.util.ArrayList;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Album> albums;

    // Listener
    private OnItemClickListener mListener;

    public AlbumAdapter(Context context, ArrayList<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.ViewHolder holder, int position) {
        holder.tvAlbumName.setText(albums.get(position).getFolderName());
        Glide.with(context)
                .load(albums.get(position).getImagePath())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true))
                .thumbnail(.5f)
                .into(holder.ivAlbum);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAlbumName;
        private ImageView ivAlbum;

        public ViewHolder(View itemView) {
            super(itemView);

            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
            ivAlbum = itemView.findViewById(R.id.ivAlbum);

            // set listener
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

    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }


}
