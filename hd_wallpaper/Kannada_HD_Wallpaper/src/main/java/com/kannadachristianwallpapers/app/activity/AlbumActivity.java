package com.kannadachristianwallpapers.app.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.adapter.AlbumAdapter;
import com.kannadachristianwallpapers.app.data.constant.AppConstants;
import com.kannadachristianwallpapers.app.listener.OnItemClickListener;
import com.kannadachristianwallpapers.app.model.others.Album;
import com.kannadachristianwallpapers.app.utils.ActivityUtils;
import com.kannadachristianwallpapers.app.utils.AdUtils;

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

        // auto load video ad
        AdUtils.getInstance(mContext).loadRewardedVideoAd(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                AdUtils.getInstance(mContext).showRewardedVideoAd();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });

        // show add on action
        //AdUtils.getInstance(mContext).loadRewardedVideoAd(null);
        //AdUtils.getInstance(mContext).showRewardedVideoAd();

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

    @Override
    protected void onResume() {
        super.onResume();
        // load banner ad
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));

    }
}

