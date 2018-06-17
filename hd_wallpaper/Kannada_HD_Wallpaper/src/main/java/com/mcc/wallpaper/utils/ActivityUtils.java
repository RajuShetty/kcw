package com.mcc.wallpaper.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import com.mcc.wallpaper.activity.CategoryDetailsActivity;
import com.mcc.wallpaper.activity.FullscreenActivity;
import com.mcc.wallpaper.activity.NotificationContentActivity;
import com.mcc.wallpaper.activity.PhotoFullscreenActivity;
import com.mcc.wallpaper.activity.PhotoListActivity;
import com.mcc.wallpaper.activity.AlbumActivity;
import com.mcc.wallpaper.activity.SearchActivity;
import com.mcc.wallpaper.activity.SingleWallActivity;
import com.mcc.wallpaper.data.constant.AppConstants;
import com.mcc.wallpaper.model.others.Album;
import com.mcc.wallpaper.model.others.Category;
import com.mcc.wallpaper.model.wallpaper.Wallpaper;

import java.util.ArrayList;

public class ActivityUtils {

    private static ActivityUtils sActivityUtils = null;

    public static ActivityUtils getInstance() {
        if (sActivityUtils == null) {
            sActivityUtils = new ActivityUtils();
        }
        return sActivityUtils;
    }

    public void invokeActivity(Activity activity, Class<?> tClass, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public void invokeCategoryDetails(Activity activity, Category category) {
        Intent intent = new Intent(activity, CategoryDetailsActivity.class);
        intent.putExtra(AppConstants.CATEGORY, category);
        activity.startActivity(intent);
    }

    public void invokeAlbumActivity(Activity activity, ArrayList<Album> albums) {
        Intent intent = new Intent(activity, AlbumActivity.class);
        intent.putParcelableArrayListExtra(AppConstants.ALBUM, albums);
        activity.startActivity(intent);
    }

    public void invokeFullscreenActivity(Activity activity, ArrayList<Wallpaper> wallpapers, int position, String wallpaperType) {
        Intent intent = new Intent(activity, FullscreenActivity.class);
        intent.putParcelableArrayListExtra(AppConstants.KEY_WALLPAPERS, wallpapers);
        intent.putExtra(AppConstants.KEY_POSITION, position);
        intent.putExtra(AppConstants.KEY_WALLPAPER_TYPE, wallpaperType);
        activity.startActivity(intent);
    }

    public void invokePhotoFullscreenActivity(Activity activity, ArrayList<String> photoList, int position) {
        Intent intent = new Intent(activity, PhotoFullscreenActivity.class);
        intent.putStringArrayListExtra(AppConstants.KEY_PHOTOS, photoList);
        intent.putExtra(AppConstants.KEY_POSITION, position);
        activity.startActivity(intent);
    }

    public void invokeSearchActivity(Activity activity, String searchKey) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(AppConstants.SEARCH_KEY, searchKey);
        activity.startActivity(intent);
    }

    public void invokePhotoDetailsActivity(Activity activity, Album album) {
        Intent intent = new Intent(activity, PhotoListActivity.class);
        intent.putExtra(AppConstants.ALBUM, (Parcelable) album);
        activity.startActivity(intent);
    }

    public void invokeNotifyContentActivity(Activity activity, String title, String message) {
        Intent intent = new Intent(activity, NotificationContentActivity.class);
        intent.putExtra(AppConstants.BUNDLE_KEY_TITLE, title);
        intent.putExtra(AppConstants.BUNDLE_KEY_MESSAGE, message);
        activity.startActivity(intent);
    }

    public void invokeSingleWallActivity(Activity activity, String title, String id) {
        Intent intent = new Intent(activity, SingleWallActivity.class);
        intent.putExtra(AppConstants.BUNDLE_KEY_TITLE, title);
        intent.putExtra(AppConstants.BUNDLE_KEY_CONTENT_ID, id);
        activity.startActivity(intent);
    }

}