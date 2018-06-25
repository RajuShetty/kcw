package com.kannadachristianwallpapers.app.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.api.http.ApiUtils;
import com.kannadachristianwallpapers.app.data.constant.AppConstants;
import com.kannadachristianwallpapers.app.model.wallpaper.Wallpaper;
import com.kannadachristianwallpapers.app.utils.AppUtility;
import com.kannadachristianwallpapers.app.utils.WallpaperUtils;
import com.kannadachristianwallpapers.app.view.TouchImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleWallActivity extends BaseActivity {

    // variable
    private Context mContext;
    private String wallpaperId;
    private String title;
    private Bitmap mBitmap;
    private GifDrawable mGifDrawable;

    // ui declaration
    private TouchImageView ivPng;
    private ImageView ivGif;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabShareWallpaper;
    private FloatingActionButton fabFavoriteWallpaper;
    private FloatingActionButton fabDownloadWallpaper;
    private FloatingActionButton fabSetWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initVariable();
        initView();
        initListener();
    }

    private void initVariable() {
        mContext = SingleWallActivity.this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppConstants.BUNDLE_KEY_CONTENT_ID)) {
                wallpaperId = bundle.getString(AppConstants.BUNDLE_KEY_CONTENT_ID);
            }

            if (bundle.containsKey(AppConstants.BUNDLE_KEY_TITLE)) {
                title = bundle.getString(AppConstants.BUNDLE_KEY_TITLE);
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_single_wall);

        initToolbar();
        enableBackButton();
        initLoader();

        // set the title
        if (title != null) {
            setToolbarTitle(title);
        } else {
            setToolbarTitle(getString(R.string.app_name));
        }

        // reference ui
        ivPng = findViewById(R.id.ivPng);
        ivGif = findViewById(R.id.ivGif);
        fabMenu = findViewById(R.id.fabMenu);
        fabShareWallpaper = findViewById(R.id.fab_share_wallpaper);
        fabFavoriteWallpaper = findViewById(R.id.fab_favorite_wallpaper);
        fabDownloadWallpaper = findViewById(R.id.fab_download_wallpaper);
        fabSetWallpaper = findViewById(R.id.fab_set_wallpaper);

        // get app object by id
        getWallpaperUrl();
    }

    private void initListener() {
        fabShareWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fabDownloadWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fabFavoriteWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fabSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void getWallpaperUrl() {
        ApiUtils.getApiInterface()
                .getWallpaper(wallpaperId, true)
                .enqueue(new Callback<Wallpaper>() {
                    @Override
                    public void onResponse(Call<Wallpaper> call, Response<Wallpaper> response) {
                        if (response.isSuccessful()) {
                            // load the app
                            loadWallpaper(response.body().getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl());
                        } else {
                            hideLoader();
                            AppUtility.showSnackBar(ivPng, getString(R.string.server_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<Wallpaper> call, Throwable t) {
                        hideLoader();
                        AppUtility.noInternetWarning(ivPng, mContext);
                    }
                });
    }

    private void loadWallpaper(String url) {
        if (WallpaperUtils.getType(url).equals(AppConstants.TYPE_WALLPAPER)) {
            loadPng(url);
        } else {
            loadGif(url);
        }
    }

    private void loadPng(String url) {
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        hideLoader();
                        ivPng.setVisibility(View.VISIBLE);

                        ivPng.setImageBitmap(resource);
                        mBitmap = resource;
                        ivPng.setZoom(1);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        hideLoader();
                        AppUtility.showSnackBar(ivPng, getString(R.string.loading_failed));
                    }
                });
    }

    private void loadGif(String url) {
        Glide.with(mContext)
                .asGif()
                .load(url)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        hideLoader();
                        ivGif.setVisibility(View.VISIBLE);

                        mGifDrawable = resource;
                        return false;
                    }

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        hideLoader();
                        AppUtility.showSnackBar(ivGif, getString(R.string.loading_failed));
                        return false;
                    }
                })
                .into(ivGif);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
