package com.mcc.wallpaper.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcc.wallpaper.R;
import com.mcc.wallpaper.adapter.GridAdapter;
import com.mcc.wallpaper.api.http.ApiUtils;
import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.listener.OnItemClickListener;
import com.mcc.wallpaper.listener.OnLoadMoreListener;
import com.mcc.wallpaper.model.others.FilterId;
import com.mcc.wallpaper.model.wallpaper.Wallpaper;
import com.mcc.wallpaper.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeaturedFragment extends BaseFragment {

    // variable
    private Context mContext;
    private Activity mActivity;
    private GridAdapter gridAdapter;
    private ArrayList<Wallpaper> wallpaperList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private static final int COLUMN_SPAN_COUNT = 2;
    private int featuredId;
    private boolean isDataLoaded;
    private boolean isDataLoading;

    // ui declaration
    private RecyclerView rvFeatured;

    // for pagination
    private int lastPage;
    private int currentPage = 1;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    public FeaturedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_featured, container, false);

        initVariable();
        initView(view);
        initListener();

        return view;
    }

    private void initVariable() {
        mContext = getContext();
        mActivity = getActivity();
        gridAdapter = new GridAdapter(mContext, wallpaperList);
        gridLayoutManager = new GridLayoutManager(mContext, COLUMN_SPAN_COUNT);

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

    private void initView(View view) {
        initProgressBar(view);

        rvFeatured = view.findViewById(R.id.rvFeatured);

        // init recyclerview
        rvFeatured.setHasFixedSize(true);
        rvFeatured.setLayoutManager(gridLayoutManager);
        rvFeatured.setNestedScrollingEnabled(false);
        rvFeatured.setAdapter(gridAdapter);
    }

    private void getFeaturedId() {
        showProgressBar();

        ApiUtils.getApiInterface()
                .getFeaturedItemsId(AppConstants.KEY_FEATURED)
                .enqueue(new Callback<List<FilterId>>() {
                    @Override
                    public void onResponse(Call<List<FilterId>> call, Response<List<FilterId>> response) {
                        if (response.isSuccessful()) {
                            featuredId = response.body().get(0).getId();
                            loadData(currentPage);
                        } else {
                            hideProgressBar();
                            showServerError(rvFeatured);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FilterId>> call, Throwable t) {
                        hideProgressBar();
                        noInternetWarning(rvFeatured, mContext);
                    }
                });
    }

    private void loadData(int page) {
        isDataLoading = true;

        ApiUtils.getApiInterface()
                .getFeaturedWallpapers(featuredId, page, true)
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
                            isDataLoaded = true;
                        } else {
                            showServerError(rvFeatured);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Wallpaper>> call, Throwable t) {
                        hideProgressBar();
                        noInternetWarning(rvFeatured, mContext);
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

        rvFeatured.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private String getType(int position) {
        if (wallpaperList.get(position).getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl().contains(".gif")) {
            return AppConstants.TYPE_GIF;
        }
        return AppConstants.TYPE_WALLPAPER;
    }

    @Override
    public void onResume() {
        gridAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !isDataLoaded && !isDataLoading) {
            // load featured items
            getFeaturedId();
        }
    }
}
