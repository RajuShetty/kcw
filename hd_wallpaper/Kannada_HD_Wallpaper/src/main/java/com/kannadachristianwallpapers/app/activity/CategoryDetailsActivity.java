package com.kannadachristianwallpapers.app.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.adapter.GridAdapter;
import com.kannadachristianwallpapers.app.api.http.ApiUtils;
import com.kannadachristianwallpapers.app.data.constant.AppConstants;
import com.kannadachristianwallpapers.app.listener.OnItemClickListener;
import com.kannadachristianwallpapers.app.listener.OnLoadMoreListener;
import com.kannadachristianwallpapers.app.model.others.Category;
import com.kannadachristianwallpapers.app.model.wallpaper.Wallpaper;
import com.kannadachristianwallpapers.app.utils.ActivityUtils;
import com.kannadachristianwallpapers.app.utils.AdUtils;
import com.kannadachristianwallpapers.app.utils.AppUtility;
import com.kannadachristianwallpapers.app.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailsActivity extends BaseActivity {

    // init variables
    private Context mContext;
    private Activity mActivity;
    private GridAdapter gridAdapter;
    private ArrayList<Wallpaper> wallpaperList = new ArrayList<>();
    private Category category;
    private GridLayoutManager gridLayoutManager;

    // constant
    private static final int COLUMN_SPAN_COUNT = 2;

    // ui declaration
    private RecyclerView rvWallpapers;
    private ImageView ivCategoryCover;
    private ProgressBar progressBar;

    // for pagination
    private int lastPage;
    private int currentPage = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();

        // load wallpapers
        loadData(currentPage);

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
        mActivity = CategoryDetailsActivity.this;
        gridAdapter = new GridAdapter(mContext, wallpaperList);
        gridLayoutManager = new GridLayoutManager(mContext, COLUMN_SPAN_COUNT);

        // set grid size 1 for loading view
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (gridAdapter.getItemViewType(position) == gridAdapter.VIEW_TYPE_ITEM) {
                    return 1;
                }
                return 2;
            }
        });

        if (getIntent().hasExtra(AppConstants.CATEGORY)) {
            category = getIntent().getParcelableExtra(AppConstants.CATEGORY);
        }
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_category_details);

        // set the title
        setTitle(category.getName());

        initToolbar();
        enableBackButton();

        // Reference ui
        rvWallpapers = findViewById(R.id.rvWallpapers);
        ivCategoryCover = findViewById(R.id.ivCategoryCover);
        progressBar = findViewById(R.id.progressBar);

        // init recyclerview
        rvWallpapers.setHasFixedSize(true);
        rvWallpapers.setLayoutManager(gridLayoutManager);
        rvWallpapers.setAdapter(gridAdapter);

        // load category cover
        String coverUrl = StringUtil.removeAmp(category.getDescription());
        Glide.with(mContext)
                .load(coverUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(ivCategoryCover);
    }

    private void loadData(int page) {
        ApiUtils.getApiInterface()
                .getWallpapers(category.getId(), page, true)
                .enqueue(new Callback<List<Wallpaper>>() {
                    @Override
                    public void onResponse(Call<List<Wallpaper>> call, Response<List<Wallpaper>> response) {
                        hideProgressBar();

                        if (response.isSuccessful()) {
                            if (currentPage == 1) {
                                if (!wallpaperList.isEmpty()) {
                                    wallpaperList.clear();
                                }

                                lastPage = Integer.parseInt(response.headers().get("X-WP-TotalPages"));
                            } else {
                                wallpaperList.remove(wallpaperList.size() - 1);
                                gridAdapter.notifyItemRemoved(wallpaperList.size());

                                gridAdapter.loadingComplete();
                            }

                            wallpaperList.addAll(response.body());
                            gridAdapter.notifyDataSetChanged();
                        } else {
                            AppUtility.showSnackBar(rvWallpapers, getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Wallpaper>> call, Throwable t) {
                        hideProgressBar();
                        AppUtility.noInternetWarning(rvWallpapers, mContext);
                    }
                });
    }

    private void initListener() {
        gridAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!gridAdapter.isLoading() && currentPage != lastPage) {
                    wallpaperList.add(null);
                    gridAdapter.notifyItemInserted(wallpaperList.size() - 1);

                    currentPage = currentPage + 1;

                    loadData(currentPage);
                    gridAdapter.setLoading();
                }
            }
        });

        gridAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String wallpaperType = getType(position);
                ActivityUtils.getInstance().invokeFullscreenActivity(mActivity, wallpaperList, position, wallpaperType);
            }
        });

        rvWallpapers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                totalItemCount = gridLayoutManager.getItemCount();
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    gridAdapter.loadMore();
                }
            }
        });
    }

    // get app type
    private String getType(int position) {
        if (wallpaperList.get(position).getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl().contains(".gif")) {
            return AppConstants.TYPE_GIF;
        }
        return AppConstants.TYPE_WALLPAPER;
    }

    private void hideProgressBar() {
        if (progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        gridAdapter.notifyDataSetChanged();
        super.onResume();
        //load banner ads
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
    }

    @Override
    protected void onDestroy() {
        // clear recyclerview
        if (gridAdapter != null && rvWallpapers != null) {
            rvWallpapers.setAdapter(null);
            rvWallpapers = null;
        }
        super.onDestroy();
    }




}

