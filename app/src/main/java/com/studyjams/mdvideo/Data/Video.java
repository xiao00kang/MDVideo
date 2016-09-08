package com.studyjams.mdvideo.Data;

import android.database.Cursor;

import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract.VideoEntry;

/**
 * Created by zwx on 2016/6/22.
 */

public class Video {

    private int mId;
    private String mTitle;
    private String mAlbum;
    private String mArtist;
    private String mDisplayName;
    private String mPath;
    private long mSize;
    private long mDuration;
    private String mMimeType;
    private long mPlayDuration;
    private String mCreatedDate;

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
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

    public long getPlayDuration() {
        return mPlayDuration;
    }

    public void setPlayDuration(long playDuration) {
        mPlayDuration = playDuration;
    }

    /**
     * create a new videoBean
     */
    public Video(int id, String title, String album, String artist, String displayName,
                 String mimeType, String path, long playDuration, long duration, long size,
                 String createdDate){
        mId = id;
        mTitle = title;
        mAlbum = album;
        mArtist = artist;
        mDisplayName = displayName;
        mMimeType = mimeType;
        mPath = path;
        mPlayDuration = playDuration;
        mDuration = duration;
        mSize = size;
        mCreatedDate = createdDate;
    }

    /**
     * provide a videoBean
     * @param cursor
     * @return
     */
    public static Video from(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(VideoEntry._ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_TITLE));
        String album = cursor.getString(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_ALBUM));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_ARTIST));
        String displayName = cursor.getString(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_DISPLAY_NAME));
        String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_MIME_TYPE));
        String path = cursor.getString(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_PATH));
        long playDuration = cursor.getLong(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_PLAY_DURATION));
        long duration = cursor.getInt(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_DURATION));
        long size = cursor.getLong(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_SIZE));
        String createdDate = cursor.getString(cursor.getColumnIndexOrThrow(VideoEntry.COLUMN_VIDEO_CREATED_DATE));

        return new Video(id,title,album,artist,displayName,mimeType,path,playDuration,duration,size,createdDate);
    }

    /**
     * return a playback status
     * @return
     */
    public boolean isPlayCompleted(){
        return mPlayDuration == mDuration;
    }

    @Override
    public String toString() {
        return "video name : " + mTitle;
    }
}