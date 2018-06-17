package com.mcc.wallpaper.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mcc.wallpaper.R;
import com.mcc.wallpaper.adapter.CategoryListAdapter;
import com.mcc.wallpaper.api.http.ApiUtils;
import com.mcc.wallpaper.listener.OnItemClickListener;
import com.mcc.wallpaper.listener.OnLoadMoreListener;
import com.mcc.wallpaper.model.others.Album;
import com.mcc.wallpaper.model.others.Category;
import com.mcc.wallpaper.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends BaseFragment {

    // variable
    private Context mContext;
    private Activity mActivity;
    private CategoryListAdapter categoryListAdapter;
    private ArrayList<Category> categoryList = new ArrayList<>();
    private ArrayList<Album> albumList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private boolean isClicked;

    // constants
    private static final int PERMISSIONS_REQUEST_STORAGE = 4;

    // ui declaration
    private RecyclerView rvCategory;
    private TextView tvMyPhotoHeading;
    private RelativeLayout lytMyPhotos;
    private ImageView coverImageView;

    // for pagination
    private int lastPage;
    private int currentPage = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        initVariable();
        initView(view);

        // load categories
        loadData();

        initListener();

        return view;
    }

    private void initVariable() {
        mContext = getContext();
        mActivity = getActivity();
        categoryListAdapter = new CategoryListAdapter(mContext, categoryList);
        linearLayoutManager = new LinearLayoutManager(mContext);
    }

    private void initView(View view) {
        initProgressBar(view);

        rvCategory = view.findViewById(R.id.rvCategory);
        lytMyPhotos = view.findViewById(R.id.lytMyPhotos);
        coverImageView = view.findViewById(R.id.coverImageView);
        tvMyPhotoHeading = view.findViewById(R.id.tvCategoryName);
        tvMyPhotoHeading.setText(getString(R.string.text_my_photos));

        // init recyclerview
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(linearLayoutManager);
        rvCategory.setNestedScrollingEnabled(false);
        rvCategory.setAdapter(categoryListAdapter);
    }

    private void loadData() {
        ApiUtils.getApiInterface()
                .getCategories(currentPage)
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        hideProgressBar();

                        if (response.isSuccessful()) {
                            if (currentPage == 1) {
                                if (!categoryList.isEmpty()) {
                                    categoryList.clear();
                                }

                                lastPage = Integer.parseInt(response.headers().get("X-WP-TotalPages"));

                                // load my photos
                                loadMyPhotos();
                            } else {
                                categoryList.remove(categoryList.size() - 1);
                                categoryListAdapter.notifyItemRemoved(categoryList.size());

                                categoryListAdapter.loadingComplete();
                            }

                            categoryList.addAll(response.body());
                            categoryListAdapter.notifyDataSetChanged();

                        } else {
                            showServerError(rvCategory);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        hideProgressBar();
                        noInternetWarning(rvCategory, mContext);
                    }
                });
    }

    private void initListener() {
        lytMyPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClicked = true;

                if (isPermissionGranted()) {
                    if (albumList.isEmpty()) {
                        loadMyPhotos();
                    } else {
                        ActivityUtils.getInstance().invokeAlbumActivity(mActivity, albumList);
                    }

                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_STORAGE);
                }
            }
        });

        categoryListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!categoryListAdapter.isLoading() && currentPage != lastPage) {
                    categoryList.add(null);
                    categoryListAdapter.notifyItemInserted(categoryList.size() - 1);

                    currentPage = currentPage + 1;

                    loadData();
                    categoryListAdapter.setLoading();
                }
            }
        });

        categoryListAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ActivityUtils.getInstance().invokeCategoryDetails(mActivity, categoryList.get(position));
            }
        });

        rvCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    categoryListAdapter.loadMore();
                }
            }
        });
    }

    private void loadMyPhotos() {
        if (isPermissionGranted()) {
            if (albumList.isEmpty()) {
                new GetAlbumsTask().execute();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_STORAGE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAlbumsTask extends AsyncTask<Void, Void, ArrayList<Album>> {
        @Override
        protected ArrayList<Album> doInBackground(Void... voids) {
            return getAllShownAlbums();
        }

        @Override
        protected void onPostExecute(ArrayList<Album> albums) {
            albumList.addAll(albums);

            for (Album album : albums) {
                if (album.getImagePath() != null) {

                    if (!mActivity.isFinishing()) {
                        Glide.with(mContext)
                                .asBitmap()
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                                .load(album.getImagePath())
                                .into(coverImageView);
                    }
                    break;
                }
            }

            if (isClicked) {
                ActivityUtils.getInstance().invokeAlbumActivity(mActivity, albumList);
            }
        }
    }

    private ArrayList<Album> getAllShownAlbums() {
        Uri uri;
        Cursor cursor;
        Cursor cursorBucket = null;
        int column_index_data;
        int column_index_folder_name;
        String absolutePathOfImage;
        ArrayList<Album> albumList = new ArrayList<>();

        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA};

        cursor = mActivity.getContentResolver().query(uri, projection, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                String[] selectionArgs = {"%" + cursor.getString(column_index_folder_name) + "%"};
                String selection = MediaStore.Images.Media.DATA + " like ? ";
                String[] projectionOnlyBucket = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

                cursorBucket = mActivity.getContentResolver().query(uri, projectionOnlyBucket, selection, selectionArgs, null);

                if (absolutePathOfImage != null && !absolutePathOfImage.equals("")) {
                    albumList.add(new Album(cursor.getString(column_index_folder_name), absolutePathOfImage, cursorBucket.getCount()));
                }
            }

            cursor.close();
            if (cursorBucket != null) {
                cursorBucket.close();
            }
        }
        return albumList;
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    loadMyPhotos();

                } else {
                    showExplanation();
                }
            }
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.storage_permission_required_for_albums));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
