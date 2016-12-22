package com.studyjams.mdvideo.Data.bean;

import android.database.Cursor;
import android.util.Log;

import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;

/**
 * Created by syamiadmin on 2016/9/9.
 */
public class Subtitle {
    private static final String TAG = "Subtitle";
    private String mId;
    private String mTitle;
    private String mPath;
    private long mSize;
    private String mCreatedDate;
    private String mMimeType;
    private String mDate;

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * create a new videoBean
     */
    public Subtitle(String id, String title, String mimeType, String path, long size, String createdDate,String date){
        mId = id;
        mTitle = title;
        mMimeType = mimeType;
        mPath = path;
        mSize = size;
        mCreatedDate = createdDate;
        mDate = date;
    }

    /**
     * provide a videoBean
     * @param cursor
     * @return
     */
    public static Subtitle from(Cursor cursor){
        String id = cursor.getString(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_ENTRY_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_NAME));
        String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_MIME_TYPE));
        String path = cursor.getString(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_PATH));
        long size = cursor.getLong(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_SIZE));
        String createdDate = cursor.getString(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_CREATED_DATE));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_DATE));

        Log.d(TAG, "========from: " + id);
        return new Subtitle(id,title,mimeType, path,size,createdDate,date);
    }
}
