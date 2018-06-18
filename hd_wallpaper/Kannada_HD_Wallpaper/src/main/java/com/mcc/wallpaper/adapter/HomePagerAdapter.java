package com.mcc.wallpaper.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mcc.wallpaper.R;
import com.mcc.wallpaper.fragment.CategoryFragment;
import com.mcc.wallpaper.fragment.GifsFragment;
import com.mcc.wallpaper.fragment.FeaturedFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private static final int ITEM_COUNT = 2;

    public HomePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CategoryFragment();
            case 1:
                return new FeaturedFragment();
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
                return context.getString(R.string.title_category);
            case 1:
                return context.getString(R.string.title_featured);
            default:
                return null;
        }
    }
}
