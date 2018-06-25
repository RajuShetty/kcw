package com.kannadachristianwallpapers.app.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.kannadachristianwallpapers.app.data.constant.AppConstants;
import com.kannadachristianwallpapers.app.fragment.GifSliderFragment;
import com.kannadachristianwallpapers.app.fragment.WallpaperSliderFragment;
import com.kannadachristianwallpapers.app.model.wallpaper.Wallpaper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class WallpaperSliderAdapter extends FragmentStatePagerAdapter {
    private final SparseArray<WeakReference<Fragment>> instantiatedFragments = new SparseArray<>();
    private ArrayList<Wallpaper> wallpaperList;
    private String wallpaperType;

    public WallpaperSliderAdapter(FragmentManager fm, ArrayList<Wallpaper> wallpaperList, String wallpaperType) {
        super(fm);
        this.wallpaperList = wallpaperList;
        this.wallpaperType = wallpaperType;
    }

    @Override
    public Fragment getItem(int position) {
        if (wallpaperType.equals(AppConstants.TYPE_WALLPAPER)) {
            return WallpaperSliderFragment.getInstance(wallpaperList.get(position));
        }
        return GifSliderFragment.getInstance(wallpaperList.get(position));
    }

    @Override
    public int getCount() {
        return wallpaperList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        instantiatedFragments.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        instantiatedFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    public Fragment getFragment(final int position) {
        final WeakReference<Fragment> wr = instantiatedFragments.get(position);
        if (wr != null) {
            return wr.get();
        } else {
            return null;
        }
    }
}
