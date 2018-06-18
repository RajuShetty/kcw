package com.mcc.wallpaper.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.data.constant.AppConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoSliderFragment extends BaseFragment {

    private Context mContext;
    private Activity mActivity;
    private ImageView largeWallpaperView;
    private String imageUrl;

    public static PhotoSliderFragment getInstance(String photoUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_PHOTOS, photoUrl);

        PhotoSliderFragment fragment = new PhotoSliderFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_slider, container, false);

        initVariable();
        initView(view);

        // load photo
        loadWallpaper();

        return view;
    }

    private void initVariable() {
        mContext = getContext();
        mActivity = getActivity();

        if (getArguments() != null) {
            if (getArguments().getString(AppConstants.KEY_PHOTOS) != null) {
                imageUrl = getArguments().getString(AppConstants.KEY_PHOTOS);
            }
        }
    }

    private void initView(View view) {
        // from parent class
        initProgressBar(view);

        largeWallpaperView = view.findViewById(R.id.largeWallpaperView);
    }

    private void loadWallpaper() {
        Glide.with(mContext)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        hideProgressBar();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        hideProgressBar();
                        return false;
                    }
                })
                .into(largeWallpaperView);
    }

    public void shareWallpaper() {
        showProgressBar();

        Glide.with(this)
                .as(byte[].class)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .skipMemoryCache(true)
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .into(new SimpleTarget<byte[]>() {
                    @Override
                    public void onResourceReady(@NonNull byte[] resource, @Nullable Transition<? super byte[]> transition) {
                        new ShareTask().execute(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toast.makeText(mContext, getString(R.string.loading_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setWallpaper() {
        showProgressBar();

        Glide.with(this)
                .as(byte[].class)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .skipMemoryCache(true)
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .into(new SimpleTarget<byte[]>() {
                    @Override
                    public void onResourceReady(@NonNull byte[] resource, @Nullable Transition<? super byte[]> transition) {
                        new SetWallpaperTask().execute(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toast.makeText(mContext, getString(R.string.loading_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    private class SetWallpaperTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... params) {
            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(mContext);
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
                myWallpaperManager.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgressBar();
//            Toast.makeText(mContext, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ShareTask extends AsyncTask<byte[], Void, File> {
        @Override
        protected File doInBackground(byte[]... params) {
            try {
                File cachePath = new File(mContext.getCacheDir(), AppConstants.TEMP_PATH);
                if (!cachePath.exists()) {
                    cachePath.mkdirs();
                }
                FileOutputStream stream = new FileOutputStream(cachePath + "/" + AppConstants.TEMP_PNG_NAME);
                stream.write(params[0]);
                stream.close();
                return cachePath;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable File result) {
            File newFile = new File(result, AppConstants.TEMP_PNG_NAME);
            Uri uri = FileProvider.getUriForFile(mContext, getString(R.string.fileprovider), newFile);
            launchShareIntent(uri);
        }
    }

    private void launchShareIntent(Uri uri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setDataAndType(uri, mActivity.getContentResolver().getType(uri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType(mActivity.getContentResolver().getType(uri));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_wallpaper_title)));

        hideProgressBar();
    }
}
