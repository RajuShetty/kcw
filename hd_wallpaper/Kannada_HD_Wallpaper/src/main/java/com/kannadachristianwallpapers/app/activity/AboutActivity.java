package com.kannadachristianwallpapers.app.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.utils.AdUtils;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends BaseActivity {

    // variables
    private Context mContext;
    private Activity mActivity;

    private RelativeLayout contentAboutBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initListener();

        // auto load video ad
        AdUtils.getInstance(mContext).loadRewardedVideoAd(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                AdUtils.getInstance(mContext).showRewardedVideoAd();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });

        // show add on action
        //AdUtils.getInstance(mContext).loadRewardedVideoAd(null);
        //AdUtils.getInstance(mContext).showRewardedVideoAd();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = AboutActivity.this;
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_about);

        initToolbar();
        enableBackButton();

        contentAboutBody = findViewById(R.id.contentAboutBody);

        // set about content

        View aboutView = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.menu_about)
                .setDescription(getString(R.string.info_us))
                .addGroup("Connect with us")
                .addEmail(getString(R.string.contact_email))
                //.addWebsite() // set your website link here
                .addFacebook(getString(R.string.contact_facebook))
                .addYoutube(getString(R.string.contact_youtube))
                //.addPlayStore("")
                .create();


        contentAboutBody.addView(aboutView);
    }

    private void initListener(){
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        AdUtils.getInstance(mContext).getmRewardedVideoAd().resume(mContext);
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
        super.onResume();
    }

    @Override
    public void onPause() {
        AdUtils.getInstance(mContext).getmRewardedVideoAd().pause(mContext);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        AdUtils.getInstance(mContext).getmRewardedVideoAd().destroy(mContext);
        super.onDestroy();
    }

}
