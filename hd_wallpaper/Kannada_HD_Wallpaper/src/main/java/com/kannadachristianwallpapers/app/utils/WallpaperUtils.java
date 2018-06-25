package com.kannadachristianwallpapers.app.utils;

import com.kannadachristianwallpapers.app.data.constant.AppConstants;

public class WallpaperUtils {

    public static String getType(String sourceUrl) {
        if (sourceUrl.contains(AppConstants.EXT_GIF)) {
            return AppConstants.TYPE_GIF;
        }
        return AppConstants.TYPE_WALLPAPER;
    }
}
