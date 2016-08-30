package com.studyjams.mdvideo.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class DataSourceProvider extends ContentProvider {
    private static final String TAG = "DataSourceProvider";
    private static final UriMatcher matcher;
    private DBHelper helper;
    private SQLiteDatabase db;

    /**
     * com.your-company.VideoProvider 授权信息
     */
    public static final String AUTHORITY = "com.studyjams.mdvideo.DataSourceProvider";
    private static final int VIDEO_ALL = 0;
    private static final int VIDEO_ONE = 1;
    private static final int SUBTITLE_ALL = 2;
    private static final int SUBTITLE_ONE = 3;

    /**
     * MIME类型vnd.android.cursor.item/content-type
     */
    public static final String CONTENT_TYPE_VIDEO = "vnd.android.cursor.dir/" + Tables.TABLE_VIDEO_NAME;
    public static final String CONTENT_ITEM_TYPE_VIDEO = "vnd.android.cursor.item/" + Tables.TABLE_VIDEO_NAME;
    public static final String CONTENT_TYPE_SUBTITLE = "vnd.android.cursor.dir/" + Tables.TABLE_SUBTITLE;
    public static final String CONTENT_ITEM_TYPE_SUBTITLE = "vnd.android.cursor.item/" + Tables.TABLE_SUBTITLE;

    static {
        /**常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码**/
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        /**如果match方法匹配路径，返回匹配码为0**/
        matcher.addURI(AUTHORITY, Tables.TABLE_VIDEO_NAME, VIDEO_ALL);   //匹配记录集合
        /**如果match方法匹配路径，返回匹配码为1**/
        matcher.addURI(AUTHORITY, Tables.TABLE_VIDEO_NAME + "/#", VIDEO_ONE); //匹配单条记录
        /**如果match方法匹配路径，返回匹配码为0**/
        matcher.addURI(AUTHORITY, Tables.TABLE_SUBTITLE, SUBTITLE_ALL);   //匹配记录集合
        /**如果match方法匹配路径，返回匹配码为1**/
        matcher.addURI(AUTHORITY, Tables.TABLE_SUBTITLE + "/#", SUBTITLE_ONE); //匹配单条记录
    }

    /**
     * 操作使用的uri
     */
    public static final Uri VIDEO_PLAY_HISTORY_URI = Uri.parse("content://" + AUTHORITY + "/" + Tables.TABLE_VIDEO_NAME);
    public static final Uri SUBTITLE_URI = Uri.parse("content://" + AUTHORITY + "/" + Tables.TABLE_SUBTITLE);
    /**注册监听使用的uri**/
    public static final Uri VIDEO_CHANGE_URI = Uri.parse("content://" + AUTHORITY);

    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType: " + uri);
        switch (matcher.match(uri)) {
            case VIDEO_ALL:
                return CONTENT_TYPE_VIDEO;
            case VIDEO_ONE:
                return CONTENT_ITEM_TYPE_VIDEO;
            case SUBTITLE_ALL:
                return CONTENT_TYPE_SUBTITLE;
            case SUBTITLE_ONE:
                return CONTENT_ITEM_TYPE_SUBTITLE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete: " + uri);
        db = helper.getWritableDatabase();
        switch (matcher.match(uri)) {
            case VIDEO_ALL:
                //doesn't need any code in my provider.
                return db.delete(Tables.TABLE_VIDEO_NAME, selection, selectionArgs);
            case VIDEO_ONE:
                //删除某一条数据
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(Tables.TABLE_VIDEO_NAME, selection, selectionArgs);
            case SUBTITLE_ALL:
                //doesn't need any code in my provider.
                return db.delete(Tables.TABLE_SUBTITLE, selection, selectionArgs);
            case SUBTITLE_ONE:
                //删除某一条数据
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(Tables.TABLE_SUBTITLE, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "insert: " + uri);
        db = helper.getWritableDatabase();
        switch (matcher.match(uri)){
            case VIDEO_ALL:
                return ContentUris.withAppendedId(uri, db.insert(Tables.TABLE_VIDEO_NAME, null, values));
            case SUBTITLE_ALL:
                return ContentUris.withAppendedId(uri, db.insert(Tables.TABLE_SUBTITLE, null, values));
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query: " + uri);
        db = helper.getReadableDatabase();
        switch (matcher.match(uri)) {
            case VIDEO_ALL:
                //doesn't need any code in my provider.
                return db.query(Tables.TABLE_VIDEO_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            case VIDEO_ONE:
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.query(Tables.TABLE_VIDEO_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            case SUBTITLE_ALL:
                //doesn't need any code in my provider.
                return db.query(Tables.TABLE_SUBTITLE, projection, selection, selectionArgs, null, null, sortOrder);
            case SUBTITLE_ONE:
                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.query(Tables.TABLE_SUBTITLE, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update: " + uri);
        db = helper.getWritableDatabase();
        switch (matcher.match(uri)) {
            case VIDEO_ALL:
                //doesn't need any code in my provider.
                return db.update(Tables.TABLE_VIDEO_NAME, values, selection, selectionArgs);
            case VIDEO_ONE:

                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.update(Tables.TABLE_VIDEO_NAME, values, selection, selectionArgs);
            case SUBTITLE_ALL:
                //doesn't need any code in my provider.
                return db.update(Tables.TABLE_SUBTITLE, values, selection, selectionArgs);
            case SUBTITLE_ONE:

                selection = "_id = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.update(Tables.TABLE_SUBTITLE, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }
}
