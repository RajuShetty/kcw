package com.kannadachristianwallpapers.app.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.adapter.PhotoSliderAdapter;
import com.kannadachristianwallpapers.app.data.constant.AppConstants;
import com.kannadachristianwallpapers.app.fragment.PhotoSliderFragment;

import java.util.ArrayList;
import java.util.Locale;


public class PhotoFullscreenActivity extends BaseActivity {

    // variable
    private PhotoSliderAdapter sliderAdapter;
    private ArrayList<String> photoList;
    private int currentPosition;

    // ui elements
    private ViewPager vpWallpaperSlider;
    private TextView tvWallpaperCount;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabShareWallpaper;
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
        if (getIntent().hasExtra(AppConstants.KEY_PHOTOS)) {
            photoList = getIntent().getStringArrayListExtra(AppConstants.KEY_PHOTOS);
        }

        if (getIntent().hasExtra(AppConstants.KEY_POSITION)) {
            currentPosition = getIntent().getIntExtra(AppConstants.KEY_POSITION, 0);
        }

        sliderAdapter = new PhotoSliderAdapter(getSupportFragmentManager(), photoList);
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_photo_fullscreen);

        initToolbar();
        enableBackButton();

        vpWallpaperSlider = findViewById(R.id.vpWallpaperSlider);
        tvWallpaperCount = findViewById(R.id.tvWallpaperCount);
        fabMenu = findViewById(R.id.fabMenu);
        fabShareWallpaper = findViewById(R.id.fab_share_wallpaper);
        fabSetWallpaper = findViewById(R.id.fab_set_wallpaper);

        vpWallpaperSlider.setAdapter(sliderAdapter);
        vpWallpaperSlider.setCurrentItem(currentPosition);

        // show current app number
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
        fabSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWallpaper();
                fabMenu.close(true);
            }
        });
    }

    private void updateInfo(int position) {
        tvWallpaperCount.setText(addLeadingZero((position + 1)) + "/" + addLeadingZero(photoList.size()));
    }

    private void shareWallpaper() {
        Fragment fragment = sliderAdapter.getFragment(vpWallpaperSlider.getCurrentItem());
        if (fragment != null) {
            ((PhotoSliderFragment) fragment).shareWallpaper();
        }
    }

    private void setWallpaper() {
        Fragment fragment = sliderAdapter.getFragment(vpWallpaperSlider.getCurrentItem());
        if (fragment != null) {
            ((PhotoSliderFragment) fragment).setWallpaper();
        }
    }

    private String addLeadingZero(int integer) {
        return String.format(new Locale("en", "US"), "%02d", integer);
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
