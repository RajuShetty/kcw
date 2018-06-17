package com.mcc.wallpaper.model.others;

public class NotificationModel {

    private String type;
    private String title;
    private String message;
    private String contentUrl;
    private String contentId;

    private int rowId;
    private boolean isRead;

    public NotificationModel(String title, String message, String contentUrl, String contentId, String type, int rowId, boolean isRead) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.contentUrl = contentUrl;
        this.contentId = contentId;
        this.rowId = rowId;
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public String getContentId() {
        return contentId;
    }

    public int getRowId() {
        return rowId;
    }

    public boolean isRead() {
        return isRead;
    }
}
