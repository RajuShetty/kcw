package com.mcc.wallpaper.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class GifsFragment extends BaseFragment {

    // variable
    private Context mContext;
    private Activity mActivity;
    private GridAdapter gridAdapter;
    private ArrayList<Wallpaper> wallpaperList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private static final int COLUMN_SPAN_COUNT = 2;
    private int gifId;
    private boolean isDataLoaded;
    private boolean isDataLoading;

    // ui declaration
    private RecyclerView rvGif;

    // for pagination
    private int lastPage;
    private int currentPage = 1;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    public GifsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gifs, container, false);

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

        rvGif = view.findViewById(R.id.rvGifs);

        // init recyclerview
        rvGif.setHasFixedSize(true);
        rvGif.setLayoutManager(gridLayoutManager);
        rvGif.setNestedScrollingEnabled(false);
        rvGif.setAdapter(gridAdapter);
    }

    private void getGifId() {
        isDataLoading = true;

        ApiUtils.getApiInterface()
                .getGifItemsId(AppConstants.KEY_GIFS)
                .enqueue(new Callback<List<FilterId>>() {
                    @Override
                    public void onResponse(Call<List<FilterId>> call, Response<List<FilterId>> response) {
                        if (response.isSuccessful()) {
                            gifId = response.body().get(0).getId();
                            loadData();
                        } else {
                            hideProgressBar();
                            showServerError(rvGif);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FilterId>> call, Throwable t) {
                        hideProgressBar();
                        noInternetWarning(rvGif, mContext);
                    }
                });
    }

    private void loadData() {
        ApiUtils.getApiInterface()
                .getWallpapers(gifId, currentPage, true)
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
                            showServerError(rvGif);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Wallpaper>> call, Throwable t) {
                        hideProgressBar();
                        noInternetWarning(rvGif, mContext);
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

                    loadData();
                    gridAdapter.setLoading();
                }
            }
        });

        gridAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ActivityUtils.getInstance().invokeFullscreenActivity(mActivity, wallpaperList, position, AppConstants.TYPE_GIF);
            }
        });

        rvGif.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                totalItemCount = gridLayoutManager.getItemCount();
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    Log.d("loggg", totalItemCount + " <= " + lastVisibleItem + " + " + visibleThreshold);
                    gridAdapter.loadMore();
                }
            }
        });
    }

    @Override
    public void onResume() {
        gridAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !isDataLoaded && !isDataLoading) {
            // load gifs
            getGifId();
        }
    }

}
