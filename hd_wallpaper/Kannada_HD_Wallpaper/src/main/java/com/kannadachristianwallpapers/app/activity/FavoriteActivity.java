package com.kannadachristianwallpapers.app.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.adapter.FavoritePagerAdapter;
import com.kannadachristianwallpapers.app.utils.AdUtils;
import com.kannadachristianwallpapers.app.utils.AppUtility;

public class FavoriteActivity extends BaseActivity {

    // variables
    private Context mContext;
    private FavoritePagerAdapter vpAdapter;

    // ui declaration
    private TabLayout tabFavorite;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
    }

    private void initVariable() {
        mContext = getApplicationContext();
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_favorite);

        initToolbar();
        initViewPager();

        // change tab layout font
        setTabFont();
        enableBackButton();

        // show no internet when not connected
        if (!AppUtility.isNetworkAvailable(mContext)) {
            AppUtility.noInternetWarning(findViewById(R.id.parentPanel), mContext);
        }

    }

    private void initViewPager() {
        tabFavorite = findViewById(R.id.tab_layout);
        mPager = findViewById(R.id.view_pager);
        vpAdapter = new FavoritePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(vpAdapter);
        tabFavorite.setupWithViewPager(mPager);
    }

    private void setTabFont() {
        ViewGroup vg = (ViewGroup) tabFavorite.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    Typeface typeface = Typeface.createFromAsset(getAssets(),
                            "fonts/Montserrat-Bold.ttf");
                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
    }
}
