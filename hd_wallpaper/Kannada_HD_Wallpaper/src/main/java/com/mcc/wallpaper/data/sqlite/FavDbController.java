package com.mcc.wallpaper.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.mcc.wallpaper.model.others.FavoriteModel;

import java.util.ArrayList;

/**
 * Created by Ashiq on 7/26/16.
 */
public class FavDbController {

    private SQLiteDatabase db;

    public FavDbController(Context context) {
        db = DbHelper.getInstance(context).getWritableDatabase();
    }

    public long addFavorite(int wallpaperId, String wallpaperType, String sourceUrl, int viewCount) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DbConstants.KEY_WALLPAPER_ID, wallpaperId);
        contentValue.put(DbConstants.KEY_WALLPAPER_TYPE, wallpaperType);
        contentValue.put(DbConstants.KEY_SOURCE_URL, sourceUrl);
        contentValue.put(DbConstants.KEY_VIEW_COUNT, viewCount);

        // Insert the new row, returning the primary key value of the new row
        return (int) db.insert(
                DbConstants.TABLE_FAVORITE,
                DbConstants.COLUMN_NAME_NULLABLE,
                contentValue);
    }

    public void removeFavorite(int wallpaperId) {
        // Which row to update, based on the ID
        String selection = DbConstants.KEY_WALLPAPER_ID + "=?";
        String[] selectionArgs = {String.valueOf(wallpaperId)};

        db.delete(
                DbConstants.TABLE_FAVORITE,
                selection,
                selectionArgs);
    }

    public boolean isFavorite(int wallpaperId) {

        String[] projection = {
                DbConstants._ID
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";
        String selection = DbConstants.KEY_WALLPAPER_ID + "=?";
        String[] selectionArgs = {String.valueOf(wallpaperId)};

        Cursor cursor = db.query(
                DbConstants.TABLE_FAVORITE,  // The table name to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }

        return false;
    }

    public ArrayList<FavoriteModel> getFavoriteData(String wallpaperType) {
        String[] projection = {
                DbConstants.KEY_WALLPAPER_ID,
                DbConstants.KEY_WALLPAPER_TYPE,
                DbConstants.KEY_SOURCE_URL,
                DbConstants.KEY_VIEW_COUNT,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";

        Cursor c = db.query(
                DbConstants.TABLE_FAVORITE,                                   // The table name to query
                projection,                                                         // The columns to return
                DbConstants.KEY_WALLPAPER_TYPE + " = '" + wallpaperType + "'",     // The columns for the WHERE clause
                null,                                                   // The values for the WHERE clause
                null,                                                       // don't group the rows
                null,                                                        // don't filter by row groups
                sortOrder                                                           // The sort order
        );

        return fetchData(c);
    }

    private ArrayList<FavoriteModel> fetchData(Cursor c) {
        ArrayList<FavoriteModel> favDataArray = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable
                    int wallpaperId = c.getInt(c.getColumnIndexOrThrow(DbConstants.KEY_WALLPAPER_ID));
                    String wallpaperType = c.getString(c.getColumnIndexOrThrow(DbConstants.KEY_WALLPAPER_TYPE));
                    String sourceUrl = c.getString(c.getColumnIndexOrThrow(DbConstants.KEY_SOURCE_URL));
                    int viewCount = c.getInt(c.getColumnIndexOrThrow(DbConstants.KEY_VIEW_COUNT));


                    // wrap up data list and return
                    favDataArray.add(new FavoriteModel(wallpaperId, wallpaperType, sourceUrl, viewCount));
                } while (c.moveToNext());
            }
            c.close();
        }
        return favDataArray;
    }

    public void deleteAllFav() {
        db.delete(
                DbConstants.TABLE_FAVORITE,
                null,
                null);
    }


}
