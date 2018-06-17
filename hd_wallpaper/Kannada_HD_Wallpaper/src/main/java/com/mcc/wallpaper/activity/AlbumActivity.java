package com.mcc.wallpaper.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.mcc.wallpaper.R;
import com.mcc.wallpaper.adapter.AlbumAdapter;
import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.listener.OnItemClickListener;
import com.mcc.wallpaper.model.others.Album;
import com.mcc.wallpaper.utils.ActivityUtils;

import java.util.ArrayList;

public class AlbumActivity extends BaseActivity {

    // variable
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Album> albumList = new ArrayList<>();
    private AlbumAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    // constant
    private static final int COLUMN_SPAN_COUNT = 2;

    // ui declaration
    private RecyclerView rvAlbums;
    private LinearLayout noDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        loadAlbums();
        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = AlbumActivity.this;
        gridLayoutManager = new GridLayoutManager(mContext, COLUMN_SPAN_COUNT);
        adapter = new AlbumAdapter(mContext, albumList);
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_album);

        // from base activity
        initToolbar();
        enableBackButton();

        // reference ui
        rvAlbums = findViewById(R.id.rvAlbums);
        noDataView = findViewById(R.id.noDataView);

        // init recyclerview
        rvAlbums.setHasFixedSize(true);
        rvAlbums.setLayoutManager(gridLayoutManager);
        rvAlbums.setAdapter(adapter);

    }

    // load album list
    private void loadAlbums() {
        if (getIntent().getParcelableArrayListExtra(AppConstants.ALBUM) != null) {
            ArrayList<Album> albums = getIntent().getParcelableArrayListExtra(AppConstants.ALBUM);
            albumList.addAll(albums);
            adapter.notifyDataSetChanged();

            if (albumList.isEmpty()) {
                noDataView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initListener() {
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ActivityUtils.getInstance().invokePhotoDetailsActivity(mActivity, albumList.get(position));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        // clear recyclerview
        if (adapter != null && rvAlbums != null) {
            rvAlbums.setAdapter(null);
            rvAlbums = null;
        }
        super.onDestroy();
    }
}
