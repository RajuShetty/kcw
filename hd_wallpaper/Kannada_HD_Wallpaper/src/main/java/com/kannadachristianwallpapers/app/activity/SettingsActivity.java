package com.kannadachristianwallpapers.app.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.google.android.gms.ads.AdView;
import com.kannadachristianwallpapers.app.R;
import com.kannadachristianwallpapers.app.utils.AdUtils;

/**
 * Created by Nasir on 9/7/17.
 */

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_settings);

        initToolbar();
        enableBackButton();

        // replace linear layout by preference screen
        getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preference);
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
        AdUtils.getInstance(getApplicationContext()).showBannerAd((AdView) findViewById(R.id.adView));
    }
}
