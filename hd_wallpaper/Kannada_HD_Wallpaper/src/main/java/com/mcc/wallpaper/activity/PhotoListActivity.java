package com.mcc.wallpaper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.adapter.PhotoAdapter;
import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.listener.OnItemClickListener;
import com.mcc.wallpaper.model.others.Album;
import com.mcc.wallpaper.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.Collections;

public class PhotoListActivity extends BaseActivity {

    // init variables
    private Context mContext;
    private Activity mActivity;
    private Album album;
    private PhotoAdapter photoAdapter;
    private ArrayList<String> photoList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private static final int COLUMN_SPAN_COUNT = 2;

    // ui declaration
    private RecyclerView rvPhotos;
    private ImageView ivCategoryCover;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();

        // get photo list
        loadPhotos();

        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = PhotoListActivity.this;
        photoAdapter = new PhotoAdapter(Glide.with(mContext), photoList);
        gridLayoutManager = new GridLayoutManager(mContext, COLUMN_SPAN_COUNT);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(AppConstants.ALBUM)) {
            album = bundle.getParcelable(AppConstants.ALBUM);
        }
    }

    private void initView() {
        // set parent view
        setContentView(R.layout.activity_category_details);

        initToolbar();
        enableBackButton();
        setToolbarTitle(album.getFolderName());

        // reference ui
        rvPhotos = findViewById(R.id.rvWallpapers);
        ivCategoryCover = findViewById(R.id.ivCategoryCover);
        progressBar = findViewById(R.id.progressBar);

        // init recyclerview
        rvPhotos.setHasFixedSize(true);
        rvPhotos.setLayoutManager(gridLayoutManager);
        rvPhotos.setAdapter(photoAdapter);

        // load cover image
        Glide.with(mActivity)
                .load(album.getImagePath())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true))
                .into(ivCategoryCover);
    }

    private void loadPhotos() {
        new GetPhotosTask().execute();
    }

    private void initListener() {
        photoAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ActivityUtils.getInstance().invokePhotoFullscreenActivity(mActivity, photoList, position);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPhotosTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            return getAllShownImagesPath();
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            Collections.reverse(strings);
            photoList.addAll(strings);
            photoAdapter.notifyDataSetChanged();

            if (progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private ArrayList<String> getAllShownImagesPath() {
        Uri uri;
        Cursor cursorBucket;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;

        String[] selectionArgs = {"%" + album.getFolderName() + "%"};

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DATA + " like ? ";

        String[] projectionOnlyBucket = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursorBucket = getContentResolver().query(uri, projectionOnlyBucket, selection, selectionArgs, null);

        column_index_data = cursorBucket.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursorBucket.moveToNext()) {
            absolutePathOfImage = cursorBucket.getString(column_index_data);
            if (absolutePathOfImage != null && !absolutePathOfImage.equals(""))
                listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if (photoAdapter != null && rvPhotos != null) {
            rvPhotos.setAdapter(null);
            rvPhotos = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
