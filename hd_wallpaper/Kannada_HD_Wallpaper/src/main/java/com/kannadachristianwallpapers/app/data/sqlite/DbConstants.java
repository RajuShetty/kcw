package com.kannadachristianwallpapers.app.data.sqlite;

import android.provider.BaseColumns;

/**
 * Created by Ashiq on 7/26/16.
 */
public class DbConstants implements BaseColumns {

    // commons
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String COLUMN_NAME_NULLABLE = null;

    // notification
    public static final String NOTIFICATION_TABLE_NAME = "notifications";

    public static final String COLUMN_NOT_TITLE = "not_title";
    public static final String COLUMN_NOT_MESSAGE = "not_message";
    public static final String COLUMN_NOT_READ_STATUS = "not_status";
    public static final String COLUMN_NOT_CONTENT_URL = "content_url";
    public static final String COLUMN_NOT_CONTENT_ID = "content_id";
    public static final String COLUMN_NOT_CONTENT_TYPE = "content_type";

    public static final String SQL_CREATE_NOTIFICATION_ENTRIES =
            "CREATE TABLE " + NOTIFICATION_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NOT_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOT_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOT_CONTENT_URL + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOT_CONTENT_ID + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOT_CONTENT_TYPE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOT_READ_STATUS + TEXT_TYPE +
                    " )";


    public static final String SQL_DELETE_NOTIFICATION_ENTRIES =
            "DROP TABLE IF EXISTS " + NOTIFICATION_TABLE_NAME;


    public static final String TABLE_FAVORITE = "table_favorite";

    public static final String KEY_WALLPAPER_ID = "wallpaper_id";
    public static final String KEY_WALLPAPER_TYPE = "wallpaper_type";
    public static final String KEY_SOURCE_URL = "source_url";
    public static final String KEY_VIEW_COUNT = "view_count";

    public static final String SQL_CREATE_FAVORITE_ENTRIES =
            "CREATE TABLE " + TABLE_FAVORITE + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    KEY_WALLPAPER_ID + TEXT_TYPE + COMMA_SEP +
                    KEY_WALLPAPER_TYPE + TEXT_TYPE + COMMA_SEP +
                    KEY_SOURCE_URL + TEXT_TYPE + COMMA_SEP +
                    KEY_VIEW_COUNT + TEXT_TYPE +
                    " )";


    public static final String SQL_DELETE_FAVORITE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_FAVORITE;


}
