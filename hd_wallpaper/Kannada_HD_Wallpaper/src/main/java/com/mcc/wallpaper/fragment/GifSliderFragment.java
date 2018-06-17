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
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.api.http.ApiUtils;
import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.data.sqlite.FavDbController;
import com.mcc.wallpaper.model.wallpaper.Wallpaper;
import com.mcc.wallpaper.utils.WallpaperUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GifSliderFragment extends BaseFragment {

    private Context mContext;
    private Activity mActivity;
    private ImageView largeWallpaperView;
    private Wallpaper wallpaper;
    private GifDrawable mGifDrawable;
    private String imageUrl;

    private boolean isCountIncreased;
    private boolean isFragmentVisible;

    private FavDbController favDbController;

    public static GifSliderFragment getInstance(Wallpaper wallpaper) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_WALLPAPERS, wallpaper);

        GifSliderFragment fragment = new GifSliderFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gif_slider, container, false);

        initVariable();
        initView(view);

        // load gif
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
                .asGif()
                .load(imageUrl)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        hideProgressBar();
                        mGifDrawable = resource;

                        // increase post view count
                        increaseViewCount();
                        return false;
                    }

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        hideProgressBar();
                        Toast.makeText(mContext, getString(R.string.loading_failed), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .into(largeWallpaperView);
    }

    public void shareWallpaper() {
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

    public void saveWallpaper() {
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
                        new SaveTask().execute(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toast.makeText(mContext, getString(R.string.loading_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setWallpaper() {
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
    class ShareTask extends AsyncTask<byte[], Void, File> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(byte[]... params) {
            try {
                File cachePath = new File(mContext.getCacheDir(), AppConstants.TEMP_PATH);
                if (!cachePath.exists()) {
                    cachePath.mkdirs();
                }
                FileOutputStream stream = new FileOutputStream(cachePath + "/" + AppConstants.TEMP_GIF_NAME);
                stream.write(params[0]);
                stream.close();
                return cachePath;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable File result) {
            // share wallpaper
            File newFile = new File(result, AppConstants.TEMP_GIF_NAME);
            Uri uri = FileProvider.getUriForFile(mContext, getString(R.string.fileprovider), newFile);
            launchShareIntent(uri);

            hideProgressBar();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SetWallpaperTask extends AsyncTask<byte[], Void, Void> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

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
            Toast.makeText(mContext, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: 2/27/2018 add loader
    @SuppressLint("StaticFieldLeak")
    private class SaveTask extends AsyncTask<byte[], Void, File> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(byte[]... params) {
            try {
                File directory = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), getString(R.string.app_name));

                File subDirectory = new File(directory, AppConstants.GIF_DIRECTORY);
                if (!subDirectory.exists()) {
                    subDirectory.mkdirs();
                }
                File filePath = new File(subDirectory, wallpaper.getTitle().getRendered() + AppConstants.EXT_GIF);

                FileOutputStream stream = new FileOutputStream(filePath);
                stream.write(params[0]);
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
            if (mGifDrawable != null) {
                increaseViewCount();
            }
        } else {
            isFragmentVisible = false;
            isCountIncreased = false;
        }
    }
}
