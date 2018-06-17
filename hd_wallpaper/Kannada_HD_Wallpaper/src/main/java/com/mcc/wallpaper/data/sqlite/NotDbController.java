package com.mcc.wallpaper.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mcc.wallpaper.model.others.NotificationModel;

import java.util.ArrayList;

/**
 * Created by Ashiq on 7/26/16.
 */
public class NotDbController {

    private SQLiteDatabase db;

    private static final String READ = "read", UNREAD = "unread";

    public NotDbController(Context context) {
        db = DbHelper.getInstance(context).getWritableDatabase();
    }

    public int insertData(String title, String message, String contentId, String contentType) {

        ContentValues values = new ContentValues();
        values.put(DbConstants.COLUMN_NOT_TITLE, title);
        values.put(DbConstants.COLUMN_NOT_MESSAGE, message);
        values.put(DbConstants.COLUMN_NOT_READ_STATUS, UNREAD);
        values.put(DbConstants.COLUMN_NOT_CONTENT_ID, contentId);
        values.put(DbConstants.COLUMN_NOT_CONTENT_TYPE, contentType);

        // Insert the new row, returning the primary key value of the new row
        return (int) db.insert(
                DbConstants.NOTIFICATION_TABLE_NAME,
                DbConstants.COLUMN_NAME_NULLABLE,
                values);
    }

    public ArrayList<NotificationModel> getAllData() {

        String[] projection = {
                DbConstants._ID,
                DbConstants.COLUMN_NOT_TITLE,
                DbConstants.COLUMN_NOT_MESSAGE,
                DbConstants.COLUMN_NOT_READ_STATUS,
                DbConstants.COLUMN_NOT_CONTENT_ID,
                DbConstants.COLUMN_NOT_CONTENT_URL,
                DbConstants.COLUMN_NOT_CONTENT_TYPE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";

        Cursor c = db.query(
                DbConstants.NOTIFICATION_TABLE_NAME,  // The table name to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(c);
    }

    public ArrayList<NotificationModel> getUnreadData() {

        String[] projection = {
                DbConstants._ID,
                DbConstants.COLUMN_NOT_TITLE,
                DbConstants.COLUMN_NOT_MESSAGE,
                DbConstants.COLUMN_NOT_READ_STATUS,
                DbConstants.COLUMN_NOT_CONTENT_ID,
                DbConstants.COLUMN_NOT_CONTENT_URL,
                DbConstants.COLUMN_NOT_CONTENT_TYPE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";
        String selection = DbConstants.COLUMN_NOT_READ_STATUS + "=?";
        String[] selectionArgs = {UNREAD};

        Cursor c = db.query(
                DbConstants.NOTIFICATION_TABLE_NAME,  // The table name to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(c);
    }

    public int unreadNotificationCount() {
        ArrayList<NotificationModel> arrayList = getUnreadData();
        if(arrayList != null && !arrayList.isEmpty()) {
            return arrayList.size();
        }
        return 0;
    }

    private ArrayList<NotificationModel> fetchData(Cursor c) {
        ArrayList<NotificationModel> ntyDataArray = new ArrayList<>();

        if(c != null) {
            if (c.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable
                    int rowId = c.getInt(c.getColumnIndexOrThrow(DbConstants._ID));
                    String title = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOT_TITLE));
                    String message = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOT_MESSAGE));
                    String status = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOT_READ_STATUS));
                    String contentUrl = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOT_CONTENT_URL));
                    String contentId = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOT_CONTENT_ID));
                    String contentType = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOT_CONTENT_TYPE));

                    boolean isUnread = !status.equals(READ);

                    // wrap up data list and return
                    ntyDataArray.add(new NotificationModel(title, message, contentUrl, contentId, contentType, rowId, isUnread));
                } while (c.moveToNext());
            }
            c.close();
        }
        return ntyDataArray;
    }

    public void updateStatus(int itemId, boolean read) {

        String readStatus = UNREAD;
        if (read) {
            readStatus = READ;
        }

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DbConstants.COLUMN_NOT_READ_STATUS, readStatus);

        // Which row to update, based on the ID
        String selection = DbConstants._ID + "=?";
        String[] selectionArgs = {String.valueOf(itemId)};

        db.update(
                DbConstants.NOTIFICATION_TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

}
