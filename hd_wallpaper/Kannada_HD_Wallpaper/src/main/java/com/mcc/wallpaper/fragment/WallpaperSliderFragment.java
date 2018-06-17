package com.mcc.wallpaper.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.api.http.ApiUtils;
import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.data.sqlite.FavDbController;
import com.mcc.wallpaper.model.wallpaper.Wallpaper;
import com.mcc.wallpaper.utils.WallpaperUtils;
import com.mcc.wallpaper.view.TouchImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class WallpaperSliderFragment extends BaseFragment {

    // variable
    private Context mContext;
    private Activity mActivity;
    private Wallpaper wallpaper;
    private String imageUrl;
    private Bitmap mBitmap;
    private boolean isCountIncreased;
    private boolean isFragmentVisible;

    // ui declaration
    private TouchImageView largeWallpaperView;

    private FavDbController favDbController;

    public static WallpaperSliderFragment getInstance(Wallpaper wallpaper) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_WALLPAPERS, wallpaper);

        WallpaperSliderFragment fragment = new WallpaperSliderFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallpaper_slider, container, false);

        initVariable();
        initView(view);

        // load wallpaper
        loadWallpaper();

        return view;
    }

    private void initVariable() {
        mContext = getContext();
        mActivity = getActivity();

        if (getArguments() != null) {
            if (getArguments().getParcelable(AppConstants.KEY_WALLPAPERS) != null) {
                wallpaper = getArguments().getParcelable(AppConstants.KEY_WALLPAPERS);
                imageUrl = wallpaper.getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl();
            }
        }

        favDbController = new FavDbController(mContext);
    }

    private void initView(View view) {
        // from parent class
        initProgressBar(view);

        // reference ui
        largeWallpaperView = view.findViewById(R.id.largeWallpaperView);
    }

    private void loadWallpaper() {
        showProgressBar();

        Glide.with(mContext)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        hideProgressBar();

                        largeWallpaperView.setImageBitmap(resource);
                        mBitmap = resource;
                        largeWallpaperView.setZoom(1);

                        // increase post view count
                        increaseViewCount();

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        hideProgressBar();
                        Toast.makeText(mContext, getString(R.string.loading_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void shareWallpaper() {
        if (mBitmap != null) {
            new ShareTask().execute(mBitmap);
        } else {
            Toast.makeText(mContext, getString(R.string.wallpaper_not_loaded), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveWallpaper() {
        if (mBitmap != null) {
            new SaveTask().execute(mBitmap);
        } else {
            Toast.makeText(mContext, getString(R.string.wallpaper_not_loaded), Toast.LENGTH_SHORT).show();
        }
    }

    public void setWallpaper() {
        if (mBitmap != null) {
            new SetWallpaperTask().execute(mBitmap);
        } else {
            Toast.makeText(mContext, getString(R.string.wallpaper_not_loaded), Toast.LENGTH_SHORT).show();
        }

    }

    public void addToFavoriteWallpaper() {
        if (!favDbController.isFavorite(wallpaper.getId())) {
            favDbController.addFavorite(wallpaper.getId(),
                    WallpaperUtils.getType(wallpaper.getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl()),
                    wallpaper.getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl(),
                    wallpaper.getPostViews());
            Toast.makeText(mContext, getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
        } else {
            favDbController.removeFavorite(wallpaper.getId());
            Toast.makeText(mContext, getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
        }
    }

    private void increaseViewCount() {
        if (isFragmentVisible && !isCountIncreased) {
            isCountIncreased = true;

            ApiUtils.getApiInterface()
                    .increaseViewCount(wallpaper.getId())
                    .enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            if (response.isSuccessful()) {

                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {

                        }
                    });
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SetWallpaperTask extends AsyncTask<Bitmap, Void, Void> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Bitmap... params) {
            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(mContext);
            try {
                myWallpaperManager.setBitmap(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgressBar();
            Toast.makeText(mContext, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: 2/27/2018 add loader
    @SuppressLint("StaticFieldLeak")
    private class ShareTask extends AsyncTask<Bitmap, Void, File> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(Bitmap... params) {
            try {
                File cachePath = new File(mContext.getCacheDir(), AppConstants.TEMP_PATH);
                if (!cachePath.exists()) {
                    cachePath.mkdirs();
                }
                FileOutputStream stream = new FileOutputStream(cachePath + "/" + AppConstants.TEMP_PNG_NAME);
                params[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                return cachePath;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable File result) {
            // share wallpaper
            File newFile = new File(result, AppConstants.TEMP_PNG_NAME);
            Uri uri = FileProvider.getUriForFile(mContext, getString(R.string.fileprovider), newFile);
            launchShareIntent(uri);

            hideProgressBar();
        }
    }

    // TODO: 2/27/2018 add loader
    @SuppressLint("StaticFieldLeak")
    private class SaveTask extends AsyncTask<Bitmap, Void, File> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(Bitmap... params) {
            try {
                File directory = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), getString(R.string.app_name));

                File subDirectory = new File(directory, AppConstants.WALLPAPER_DIRECTORY);
                if (!subDirectory.exists()) {
                    subDirectory.mkdirs();
                }

                File filePath = new File(subDirectory, wallpaper.getTitle().getRendered() + AppConstants.EXT_PNG);

                FileOutputStream stream = new FileOutputStream(filePath);
                params[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(filePath));
                mActivity.sendBroadcast(mediaScanIntent);

                return filePath;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable File result) {
            hideProgressBar();
            Toast.makeText(mContext, getString(R.string.wallpaper_saved), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            isFragmentVisible = true;
            if (mBitmap != null) {
                increaseViewCount();
            }
        } else {
            isFragmentVisible = false;
            isCountIncreased = false;
        }
    }
}
