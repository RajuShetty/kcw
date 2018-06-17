package com.mcc.wallpaper.model.others;

public class FavoriteModel {
    private int wallpaperId;
    private String wallpaperType;
    private String sourceUrl;
    private int viewCount;

    public FavoriteModel(int wallpaperId, String wallpaperType, String sourceUrl, int viewCount) {
        this.wallpaperId = wallpaperId;
        this.wallpaperType = wallpaperType;
        this.sourceUrl = sourceUrl;
        this.viewCount = viewCount;
    }

    public int getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(int wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public String getWallpaperType() {
        return wallpaperType;
    }

    public void setWallpaperType(String wallpaperType) {
        this.wallpaperType = wallpaperType;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
