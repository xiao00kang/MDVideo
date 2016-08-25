package com.studyjams.mdvideo.DatabaseHelper;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.studyjams.mdvideo.Util.Tools;

import java.io.File;

/**
 * Created by syamiadmin on 2016/7/26.
 */
public class SyncSqlHandler extends AsyncQueryHandler{

    private static final String TAG = "SyncSqlHandler";

    //本地媒体库查询后进行插入操作
    public static final int MEDIA_QUERY_INSERT = 0;

    //自建应用内媒体库查询后进行插入操作
    public static final int LOCAL_QUERY_INSERT = 1;

    //自建应用内媒体库查询后校验URI地址是否可用并进行清理操作
    public static final int LOCAL_QUERY_DELETE = 2;

    //自建应用内媒体库查询后校验URI地址是否可用并进行清理操作
    public static final int LOCAL_UPDATE = 3;

    private ContentResolver mContentResolver;
    public SyncSqlHandler(ContentResolver cr) {
        super(cr);
        mContentResolver = cr;
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        Log.d(TAG, "onDeleteComplete: " + result);
        mContentResolver.notifyChange(DataSourceProvider.VIDEO_PLAY_HISTORY_URI, null);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        Log.d(TAG, "onInsertComplete: ");
        mContentResolver.notifyChange(uri, null);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);

        switch (token){
            case MEDIA_QUERY_INSERT:
                while (cursor.moveToNext()) {
//                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

                    ContentValues values = new ContentValues();
                    values.put(Tables.Video_title, title);
                    values.put(Tables.Video_album, album);
                    values.put(Tables.Video_artist, artist);
                    values.put(Tables.Video_displayName, displayName);
                    values.put(Tables.Video_mimeType, mimeType);
                    values.put(Tables.Video_path, path);
                    values.put(Tables.Video_size, size);
                    values.put(Tables.Video_duration, duration);
                    values.put(Tables.Video_playDuration, -1);
                    values.put(Tables.Video_createdDate, Tools.getCurrentTimeMillis());

                    startQuery(LOCAL_QUERY_INSERT,
                            values,
                            DataSourceProvider.VIDEO_PLAY_HISTORY_URI,
                            null,
                            Tables.Video_path + " like '" + path + "'",
                            null,
                            null);
                    Log.d(TAG, "onQueryComplete: MEDIA_QUERY_INSERT" + path);
                }


                break;
            case LOCAL_QUERY_INSERT:

                if(!cursor.moveToPosition(0)){
                    startInsert(LOCAL_QUERY_INSERT,null, DataSourceProvider.VIDEO_PLAY_HISTORY_URI,(ContentValues)cookie);

                }else{
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_path));
                    Log.d(TAG, "onQueryComplete: LOCAL_QUERY_INSERT" + path);
                    mContentResolver.notifyChange(DataSourceProvider.VIDEO_PLAY_HISTORY_URI, null);
                }

                break;
            case LOCAL_QUERY_DELETE:
                while (!cursor.moveToPosition(0) && cursor.moveToNext()){
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_path));
                    File file = new File(path);
                    if(!file.exists()){
                        startDelete(LOCAL_QUERY_DELETE,null,
                                DataSourceProvider.VIDEO_PLAY_HISTORY_URI,
                                Tables.Video_path + " like '" + path + "'",
                                null);
                    }

                }

                break;

            default:

                break;
        }
        if(cursor != null){
            cursor.close();
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        Log.d(TAG, "======================onUpdateComplete: ");
        mContentResolver.notifyChange(DataSourceProvider.VIDEO_CHANGE_URI, null);
    }
}
