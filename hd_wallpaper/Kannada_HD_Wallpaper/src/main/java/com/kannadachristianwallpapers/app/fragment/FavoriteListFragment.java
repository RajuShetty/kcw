package com.kannadachristianwallpapers.app.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.adapter.FavoriteAdapter;
import com.kannadachristianwallpapers.app.data.constant.AppConstants;
import com.kannadachristianwallpapers.app.data.sqlite.FavDbController;
import com.kannadachristianwallpapers.app.listener.OnItemClickListener;
import com.kannadachristianwallpapers.app.model.others.FavoriteModel;
import com.kannadachristianwallpapers.app.model.wallpaper.Embedded;
import com.kannadachristianwallpapers.app.model.wallpaper.Wallpaper;
import com.kannadachristianwallpapers.app.model.wallpaper.WpFeaturedmedium_;
import com.kannadachristianwallpapers.app.utils.ActivityUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteListFragment extends Fragment {

    // variable
    private Context mContext;
    private Activity mActivity;
    private FavoriteAdapter favoriteAdapter;
    private String wallpaperType;
    private ArrayList<Wallpaper> wallpapers = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;

    // constants
    private static final int COLUMN_SPAN_COUNT = 2;

    // ui declaration
    private RecyclerView rvFavorite;
    private LinearLayout noDataView;

    public static FavoriteListFragment getInstance(String wallpaperType) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_WALLPAPER_TYPE, wallpaperType);
        FavoriteListFragment fragment = new FavoriteListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);

        initVariable();
        initView(view);
        loadFavorite();
        initListener();

        return view;
    }

    private void initVariable() {
        mContext = getContext();
        mActivity = getActivity();
        favoriteAdapter = new FavoriteAdapter(mContext, wallpapers);
        gridLayoutManager = new GridLayoutManager(mContext, COLUMN_SPAN_COUNT);

        if (getArguments() != null) {
            if (getArguments().getString(AppConstants.KEY_WALLPAPER_TYPE) != null) {
                wallpaperType = getArguments().getString(AppConstants.KEY_WALLPAPER_TYPE);
            }
        }
    }

    private void initView(View view) {
        rvFavorite = view.findViewById(R.id.rvFavorite);
        noDataView = view.findViewById(R.id.noDataView);

        // init recyclerview
        rvFavorite.setHasFixedSize(true);
        rvFavorite.setLayoutManager(gridLayoutManager);
        rvFavorite.setNestedScrollingEnabled(false);
        rvFavorite.setAdapter(favoriteAdapter);
    }

    private void loadFavorite() {
        try {
            ArrayList<FavoriteModel> favorites = new ArrayList<>();

            FavDbController favDbController = new FavDbController(mContext);
            favorites.addAll(favDbController.getFavoriteData(wallpaperType));

            for (FavoriteModel favorite : favorites) {
                Wallpaper wallpaper = new Wallpaper();
                wallpaper.setId(favorite.getWallpaperId());
                wallpaper.setPostViews(favorite.getViewCount());

                ArrayList<WpFeaturedmedium_> wpFeaturedmedium_s = new ArrayList<>();
                WpFeaturedmedium_ wpFeaturedmedium_ = new WpFeaturedmedium_();
                wpFeaturedmedium_.setSourceUrl(favorite.getSourceUrl());

                wpFeaturedmedium_s.add(wpFeaturedmedium_);

                Embedded embedded = new Embedded();
                embedded.setWpFeaturedmedia(wpFeaturedmedium_s);
                wallpaper.setEmbedded(embedded);

                wallpapers.add(wallpaper);
            }

            favoriteAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (wallpapers.isEmpty()) {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        favoriteAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ActivityUtils.getInstance().invokeFullscreenActivity(mActivity, wallpapers, position, wallpaperType);
            }
        });
    }

    @Override
    public void onResume() {
        favoriteAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
