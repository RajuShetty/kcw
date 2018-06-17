package com.mcc.wallpaper.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.utils.ActivityUtils;
import com.mcc.wallpaper.utils.AppUtility;

public class SplashActivity extends AppCompatActivity {

    // init variables
    private Context mContext;
    private Activity mActivity;
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initVariables();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
    }

    private void initFunctionality() {
        if (AppUtility.isNetworkAvailable(mContext)) {
            findViewById(R.id.ivLogo).postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.getInstance().invokeActivity(mActivity, MainActivity.class, true);
                }
            }, SPLASH_DURATION);

        } else {
            AppUtility.noInternetWarning(findViewById(R.id.ivLogo), mContext);
        }
    }

    private void initVariables() {
        mActivity = SplashActivity.this;
        mContext = getApplicationContext();
    }

    private void initView() {
        setContentView(R.layout.activity_splash);
    }
}