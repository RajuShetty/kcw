package com.mcc.wallpaper.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.adapter.WallpaperSliderAdapter;
import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.data.sqlite.FavDbController;
import com.mcc.wallpaper.fragment.GifSliderFragment;
import com.mcc.wallpaper.fragment.WallpaperSliderFragment;
import com.mcc.wallpaper.model.wallpaper.Wallpaper;
import com.mcc.wallpaper.utils.StringUtil;

import java.util.ArrayList;


public class FullscreenActivity extends BaseActivity {

    // variable
    private Context mContext;
    private Activity mActivity;
    private WallpaperSliderAdapter sliderAdapter;
    private ArrayList<Wallpaper> wallpapers;
    private String wallpaperType;
    private int currentPosition;

    // constant
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1001;

    // ui elements
    private ViewPager vpWallpaperSlider;
    private TextView tvWallpaperCount;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabShareWallpaper;
    private FloatingActionButton fabFavoriteWallpaper;
    private FloatingActionButton fabDownloadWallpaper;
    private FloatingActionButton fabSetWallpaper;

    private FavDbController favDbController;

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
        mContext = FullscreenActivity.this;
        mActivity = FullscreenActivity.this;

        if (getIntent().hasExtra(AppConstants.KEY_WALLPAPERS)) {
            wallpapers = getIntent().getParcelableArrayListExtra(AppConstants.KEY_WALLPAPERS);
        }

        if (getIntent().hasExtra(AppConstants.KEY_POSITION)) {
            currentPosition = getIntent().getIntExtra(AppConstants.KEY_POSITION, 0);
        }

        if (getIntent().hasExtra(AppConstants.KEY_WALLPAPER_TYPE)) {
            wallpaperType = getIntent().getStringExtra(AppConstants.KEY_WALLPAPER_TYPE);
        }

        sliderAdapter = new WallpaperSliderAdapter(getSupportFragmentManager(), wallpapers, wallpaperType);

        favDbController = new FavDbController(mContext);
    }

    private void initView() {
        setContentView(R.layout.activity_fullscreen);

        initToolbar();
        enableBackButton();

        vpWallpaperSlider = findViewById(R.id.vpWallpaperSlider);
        tvWallpaperCount = findViewById(R.id.tvWallpaperCount);
        fabMenu = findViewById(R.id.fabMenu);
        fabShareWallpaper = findViewById(R.id.fab_share_wallpaper);
        fabFavoriteWallpaper = findViewById(R.id.fab_favorite_wallpaper);
        fabDownloadWallpaper = findViewById(R.id.fab_download_wallpaper);
        fabSetWallpaper = findViewById(R.id.fab_set_wallpaper);

        vpWallpaperSlider.setAdapter(sliderAdapter);
        vpWallpaperSlider.setCurrentItem(currentPosition);

        updateInfo(currentPosition);
    }

    private void initListener() {
        vpWallpaperSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                updateInfo(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fabShareWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWallpaper();
                fabMenu.close(true);
            }
        });

        fabFavoriteWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTofavoriteWallpaper();
                setFabFavoriteIcon();
                fabMenu.close(true);
            }
        });

        fabDownloadWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                } else {
                    saveWallpaper();
                    fabMenu.close(true);
                }
            }
        });

        fabSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWallpaper();
                fabMenu.close(true);
            }
        });
    }

    private void updateInfo(int position) {
        tvWallpaperCount.setText(StringUtil.addLeadingZero((position + 1)) + "/" + StringUtil.addLeadingZero(wallpapers.size()));
        setFabFavoriteIcon();
    }

    private void setFabFavoriteIcon() {
        if (favDbController.isFavorite(wallpapers.get(currentPosition).getId())) {
            fabFavoriteWallpaper.setImageResource(R.drawable.ic_favorite_orange_solid);
        } else {
            fabFavoriteWallpaper.setImageResource(R.drawable.ic_favorite_orange_border);
        }
    }

    private void shareWallpaper() {
        Fragment fragment = sliderAdapter.getFragment(vpWallpaperSlider.getCurrentItem());
        if (fragment != null) {
            if (wallpaperType.equals(AppConstants.TYPE_WALLPAPER)) {
                ((WallpaperSliderFragment) fragment).shareWallpaper();
            } else {
                ((GifSliderFragment) fragment).shareWallpaper();
            }
        }
    }

    private void addTofavoriteWallpaper() {
        Fragment fragment = sliderAdapter.getFragment(vpWallpaperSlider.getCurrentItem());
        if (fragment != null) {
            if (wallpaperType.equals(AppConstants.TYPE_WALLPAPER)) {
                ((WallpaperSliderFragment) fragment).addToFavoriteWallpaper();
            } else {
                ((GifSliderFragment) fragment).addToFavoriteWallpaper();
            }
        }
    }

    private void saveWallpaper() {
        Fragment fragment = sliderAdapter.getFragment(vpWallpaperSlider.getCurrentItem());
        if (fragment != null) {
            if (wallpaperType.equals(AppConstants.TYPE_WALLPAPER)) {
                ((WallpaperSliderFragment) fragment).saveWallpaper();
            } else {
                ((GifSliderFragment) fragment).saveWallpaper();
            }

        }
    }

    private void setWallpaper() {
        Fragment fragment = sliderAdapter.getFragment(vpWallpaperSlider.getCurrentItem());
        if (fragment != null) {
            if (wallpaperType.equals(AppConstants.TYPE_WALLPAPER)) {
                ((WallpaperSliderFragment) fragment).setWallpaper();
            } else {
                ((GifSliderFragment) fragment).setWallpaper();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    saveWallpaper();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(getString(R.string.storage_permission_required_to_download));
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
