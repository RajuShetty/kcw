package com.mcc.wallpaper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.mcc.wallpaper.R;
import com.mcc.wallpaper.data.constant.AppConstants;


public class NotificationContentActivity extends AppCompatActivity {

    private Activity mActivity;
    private Toolbar mToolbar;
    private TextView titleView, messageView;
    private String title, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initVariable();
        initFunctionality();
    }

    private void initVariable() {
        mActivity = NotificationContentActivity.this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString(AppConstants.BUNDLE_KEY_TITLE);
            message = extras.getString(AppConstants.BUNDLE_KEY_MESSAGE);
        }
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_notification_details);

        // init toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.notification_details));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        titleView = findViewById(R.id.title);
        messageView = findViewById(R.id.message);
    }

    private void initFunctionality() {
        titleView.setText(title);
        messageView.setText(message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
