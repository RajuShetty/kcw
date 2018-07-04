package com.kannadachristianwallpapers.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.adapter.GridAdapter;
import com.kannadachristianwallpapers.app.api.http.ApiUtils;
import com.kannadachristianwallpapers.app.data.constant.AppConstants;
import com.kannadachristianwallpapers.app.listener.OnItemClickListener;
import com.kannadachristianwallpapers.app.listener.OnLoadMoreListener;
import com.kannadachristianwallpapers.app.model.wallpaper.Wallpaper;
import com.kannadachristianwallpapers.app.utils.ActivityUtils;
import com.kannadachristianwallpapers.app.utils.AdUtils;
import com.kannadachristianwallpapers.app.utils.AppUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {

    // initialize variables
    private Context mContext;
    private Activity mActivity;

    private RecyclerView rvWallpapers;
    private ArrayList<Wallpaper> wallpaperList = new ArrayList<>();
    private GridAdapter gridAdapter;
    private GridLayoutManager gridLayoutManager;
    private static final int COLUMN_SPAN_COUNT = 2;
    private String searchKey = AppConstants.EMPTY_STRING;

    // initialize View
    private SearchView mSearchView;

    // for pagination
    private int lastPage;
    private int currentPage = 1;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();

        // load search data
        loadData();

        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = SearchActivity.this;

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
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_search);

        // from base class
        initLoader();
        initToolbar();
        enableBackButton();

        rvWallpapers = findViewById(R.id.rvWallpapers);
        mSearchView = findViewById(R.id.searchView);
        mSearchView.setIconified(false);

        // init recyclerview
        rvWallpapers.setHasFixedSize(true);
        rvWallpapers.setLayoutManager(gridLayoutManager);
        rvWallpapers.setNestedScrollingEnabled(false);
        rvWallpapers.setAdapter(gridAdapter);
    }

    private void loadData() {
        Intent intent = this.getIntent();
        searchKey = intent.getStringExtra(AppConstants.SEARCH_KEY);

        mSearchView.setQuery(searchKey, false);
        mSearchView.clearFocus();
        loadWallpapers(currentPage);
    }

    private void initListener() {
        gridAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!gridAdapter.isLoading() && currentPage != lastPage) {
                    wallpaperList.add(null);
                    gridAdapter.notifyItemInserted(wallpaperList.size() - 1);

                    currentPage = currentPage + 1;

                    loadWallpapers(currentPage);
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

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentPage = 1;
                showLoader();
                loadWallpapers(currentPage);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String key) {
                return false;
            }
        });
    }

    // returns app type
    private String getType(int position) {
        if (wallpaperList.get(position).getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl().contains(".gif")) {
            return AppConstants.TYPE_GIF;
        }
        return AppConstants.TYPE_WALLPAPER;
    }

    private void loadWallpapers(int page) {
        searchKey = mSearchView.getQuery().toString();

        ApiUtils.getApiInterface()
                .searchWallpapers(searchKey, page, true)
                .enqueue(new Callback<List<Wallpaper>>() {
                    @Override
                    public void onResponse(Call<List<Wallpaper>> call, Response<List<Wallpaper>> response) {
                        hideLoader();

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

                            if (wallpaperList.isEmpty()) {
                                showEmptyView();
                            }
                        } else {
                            AppUtility.showSnackBar(rvWallpapers, getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Wallpaper>> call, Throwable t) {
                        hideLoader();
                        AppUtility.noInternetWarning(rvWallpapers, mContext);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
    }
}
