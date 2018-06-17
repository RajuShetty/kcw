package com.mcc.wallpaper.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.adapter.NotificationAdapter;
import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.data.sqlite.NotDbController;
import com.mcc.wallpaper.listener.OnItemClickListener;
import com.mcc.wallpaper.model.others.NotificationModel;
import com.mcc.wallpaper.utils.ActivityUtils;
import com.mcc.wallpaper.utils.AdUtils;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    // variable
    private Context mContext;
    private Activity mActivity;
    private NotificationAdapter mAdapter;
    private ArrayList<NotificationModel> dataList = new ArrayList<>();

    // ui declaration
    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private NotDbController notDbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVars();
        initialView();
        initFunctionality();
        initialListener();
    }

    private void initVars() {
        mContext = getApplicationContext();
        mActivity = NotificationActivity.this;

        mAdapter = new NotificationAdapter(dataList);
    }

    private void initialView() {
        // set parent view
        setContentView(R.layout.activity_notification);

        // init toolbar
        mToolbar = findViewById(R.id.toolbar);
        emptyView = findViewById(R.id.emptyView);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.notifications));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // init recyclerview
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(mAdapter);
    }

    private void initFunctionality() {

        notDbController = new NotDbController(getApplicationContext());

        dataList.addAll(notDbController.getAllData());

        if (dataList != null && !dataList.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void initialListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String notificationType = dataList.get(position).getType();

                if (notificationType.equals(AppConstants.NOTIFY_TYPE_MESSAGE)) {
                    ActivityUtils.getInstance().invokeNotifyContentActivity(mActivity, dataList.get(position).getTitle(), dataList.get(position).getMessage());
                }

                notDbController.updateStatus(dataList.get(position).getRowId(), true);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // load banner ad
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
    }
}
