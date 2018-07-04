package com.kannadachristianwallpapers.app.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.adapter.HomePagerAdapter;
import com.kannadachristianwallpapers.app.data.sqlite.NotDbController;
import com.kannadachristianwallpapers.app.utils.ActivityUtils;
import com.kannadachristianwallpapers.app.utils.AdUtils;
import com.kannadachristianwallpapers.app.utils.AppUtility;


public class MainActivity extends BaseActivity {

    // variables
    private Context mContext;
    private Activity mActivity;
    private HomePagerAdapter vpAdapter;

    // view pager image slider
    private ViewPager mPager;

    // ui declaration
    private TabLayout tabHome;
    private SearchView mSearchView;
    private TextView tvNotificationCounter;
    private ImageView imgNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_main);

        mSearchView = findViewById(R.id.ivSearchIcon);
        tvNotificationCounter = findViewById(R.id.tvNotificationCounter);
        imgNotification = findViewById(R.id.imgNotification);

        // initiate drawer and toolbar
        initToolbar();
        initDrawer();

        // setup viewpager
        initViewPager();
        setTabFont();

        AppUtility.noInternetWarning(findViewById(R.id.parentPanel), mContext);
        if (!AppUtility.isNetworkAvailable(mContext)) {
            showEmptyView();
        }

    }

    private void initViewPager() {
        tabHome = findViewById(R.id.tab_layout);
        mPager = findViewById(R.id.view_pager);
        vpAdapter = new HomePagerAdapter(getSupportFragmentManager(), mContext);
        mPager.setAdapter(vpAdapter);
        mPager.setOffscreenPageLimit(3);
        tabHome.setupWithViewPager(mPager);
    }

    private void setTabFont() {
        ViewGroup vg = (ViewGroup) tabHome.getChildAt(0);
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

    private void initListener() {
        // search view on query submit
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // go to search activity
                ActivityUtils.getInstance().invokeSearchActivity(mActivity, query);

                // hide keyboard and close searchview
                mSearchView.clearFocus();
                mSearchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // toolbar notification action listener
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAdThenActivity(NotificationActivity.class);
                //showAdThenActivity(CategoryDetailsActivity.class);

                /**
                 * if you don't want to show notification then disable
                 * disable previous line and use line given bellow
                 **/

                // ActivityUtils.getInstance().invokeActivity(mActivity, NotificationActivity.class, false);

            }
        });
    }

    private void loadNotificationCounter() {
        try {
            int unreadCount = 0;

            try {
                unreadCount = new NotDbController(getApplicationContext()).unreadNotificationCount();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (unreadCount == 0) {
                tvNotificationCounter.setVisibility(View.GONE);
            } else {
                tvNotificationCounter.setVisibility(View.VISIBLE);
                tvNotificationCounter.setText(String.valueOf(unreadCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvNotificationCounter.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // populate notification counter
        loadNotificationCounter();

        // load full screen ad
        AdUtils.getInstance(mContext).loadFullScreenAd(mActivity);

        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
    }
}
