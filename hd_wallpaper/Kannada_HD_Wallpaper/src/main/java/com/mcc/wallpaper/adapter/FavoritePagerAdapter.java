package com.mcc.wallpaper.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.fragment.FavoriteListFragment;

public class FavoritePagerAdapter extends FragmentPagerAdapter {

    private static final int ITEM_COUNT = 1;

    public FavoritePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FavoriteListFragment.getInstance(AppConstants.TYPE_WALLPAPER);

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return AppConstants.TITLE_WALLPAPER;

            default:
                return null;
        }
    }
}
