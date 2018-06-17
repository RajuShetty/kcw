package com.mcc.wallpaper.adapter;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcc.wallpaper.R;
import com.mcc.wallpaper.listener.OnItemClickListener;
import com.mcc.wallpaper.model.others.NotificationModel;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<NotificationModel> dataList;

    // handle interface for item listener
    private OnItemClickListener itemClickListener;

    public NotificationAdapter(ArrayList<NotificationModel> dataList) {
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle, tvSubTitle;

        // handle interface for item listener
        private OnItemClickListener itemClickListener;

        public ViewHolder(View itemView, int viewType, OnItemClickListener itemClickListener) {
            super(itemView);

            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view, viewType, itemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = dataList.get(position).getTitle();
        String message = dataList.get(position).getMessage();

        if (title != null) {
            if (dataList.get(position).isRead()) {
                holder.tvTitle.setTypeface(null, Typeface.BOLD);
            } else {
                holder.tvTitle.setTypeface(null, Typeface.NORMAL);
            }
            holder.tvTitle.setText(title);
            holder.tvSubTitle.setText(message);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
