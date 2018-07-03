package com.kannadachristianwallpapers.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.kannadachristianwallpapers.app.R;

/**
 * Created by Ashiq on 4/13/2017.
 */

public class AdUtils {

    private static AdUtils adUtils;
    private Context mContext;

    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    private boolean disableBannerAd = false, disableInterstitialAd = false;

    private AdUtils(Context context) {
        this.mContext = context;
        MobileAds.initialize(context, context.getResources().getString(R.string.app_ad_id));
    }

    public static AdUtils getInstance(Context context) {
        if (adUtils == null) {
            adUtils = new AdUtils(context);
        }
        return adUtils;
    }

    public void showBannerAd(final AdView mAdView) {
        if (disableBannerAd) {
            mAdView.setVisibility(View.GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    mAdView.setVisibility(View.GONE);
                }
            });
        }
    }

    public void loadFullScreenAd(Activity activity) {
        mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(activity.getResources().getString(R.string.interstitial_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    public boolean showFullScreenAd() {
        if (!disableInterstitialAd) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                return true;
            }
        }
        return false;
    }

    public void loadRewardedVideoAd(RewardedVideoAdListener rewardedVideoAdListener) {

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mContext);
        mRewardedVideoAd.loadAd(mContext.getResources().getString(R.string.rewarded_video_ad_unit_id), new AdRequest.Builder().build());
        if(rewardedVideoAdListener != null) {
            mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        }

    }

    public void showRewardedVideoAd() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    public InterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    public void disableBannerAd() {
        this.disableBannerAd = true;
    }

    public void disableInterstitialAd() {
        this.disableInterstitialAd = true;
    }

    public RewardedVideoAd getmRewardedVideoAd() {
        return mRewardedVideoAd;
    }

}
