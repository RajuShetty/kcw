package com.mcc.wallpaper.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.mcc.wallpaper.fragment.PhotoSliderFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class PhotoSliderAdapter extends FragmentStatePagerAdapter {
    private final SparseArray<WeakReference<Fragment>> instantiatedFragments = new SparseArray<>();
    private ArrayList<String> photoList;

    public PhotoSliderAdapter(FragmentManager fm, ArrayList<String> photoList) {
        super(fm);
        this.photoList = photoList;
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoSliderFragment.getInstance(photoList.get(position));
    }

    @Override
    public int getCount() {
        return photoList.size();
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
