package com.studyjams.mdvideo.Data.source.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.studyjams.mdvideo.Data.bean.Video;
import com.studyjams.mdvideo.Data.source.SamplesValues;
import com.studyjams.mdvideo.Data.source.SubtitleDataSource;
import com.studyjams.mdvideo.Data.source.VideoDataSource;
import com.studyjams.mdvideo.Data.source.remote.FileItem;

import java.io.File;

/**
 * Concrete implementation of a data source as a db.
 */
public class SamplesLocalDataSource implements VideoDataSource,SubtitleDataSource {

    private static final String TAG = "SamplesLocalDataSource";

    private static SamplesLocalDataSource INSTANCE;
    private ContentResolver mContentResolver;
    // Prevent direct instantiation.
    private SamplesLocalDataSource(@NonNull ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    public static SamplesLocalDataSource getInstance(@NonNull ContentResolver contentResolver) {
        if (INSTANCE == null) {
            INSTANCE = new SamplesLocalDataSource(contentResolver);
        }
        return INSTANCE;
    }

    @Override
    public void clearNotExistsVideos() {

        Cursor cursor = mContentResolver.query(SamplesPersistenceContract.VideoEntry.buildVideosUri(),
                new String[]{SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PATH},
                null,
                null,
                null);
        if(cursor != null) {
            while (!cursor.moveToPosition(0) && cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PATH));
                File file = new File(path);
                if (!file.exists()) {
                    String selection = SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PATH + " LIKE ?";
                    String[] selectionArgs = {path};
                    mContentResolver.delete(SamplesPersistenceContract.VideoEntry.buildVideosUri(), selection, selectionArgs);
                }
            }
            cursor.close();
        }
    }

    @Override
    public void deleteVideo(@NonNull String videoId) {
        String selection = SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {videoId};
        mContentResolver.delete(SamplesPersistenceContract.VideoEntry.buildVideosUri(),selection,selectionArgs);
    }

    @Override
    public void getVideo(@NonNull String videoId, @NonNull GetVideoCallback callback) {

    }

    @Override
    public void getVideos(@NonNull GetVideosCallback callback) {

    }

    @Override
    public void saveVideo(@NonNull FileItem fileItem) {
        ContentValues values = SamplesValues.videoFrom(fileItem);
        String[] projection = new String[]{SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PATH};
        String selection = SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PATH + " LIKE ?";
        String[] selectionArgs = new String[]{values.getAsString(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PATH)};

        Cursor cursor = mContentResolver.query(SamplesPersistenceContract.VideoEntry.buildVideosUri(),
                projection,
                selection,
                selectionArgs,
                null);

        if (cursor != null) {
            if (!cursor.moveToPosition(0)) {

                mContentResolver.insert(SamplesPersistenceContract.VideoEntry.buildVideosUri(), values);
            }
            cursor.close();
        }
        mContentResolver.notifyChange(SamplesPersistenceContract.VideoEntry.buildVideosUri(), null);
    }

    @Override
    public void saveVideo(@NonNull Video video) {
        ContentValues values = SamplesValues.videoFrom(video);
        mContentResolver.insert(SamplesPersistenceContract.VideoEntry.buildVideosUri(), values);
    }

    @Override
    public void updateVideo(@NonNull Video video) {
        ContentValues values = new ContentValues();
        values.put(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PLAY_DURATION, video.getPlayDuration());
        values.put(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_CREATED_DATE, video.getCreatedDate());

        String selection = SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(video.getId())};

        mContentResolver.update(SamplesPersistenceContract.VideoEntry.buildVideosUri(), values, selection, selectionArgs);
    }

    @Override
    public void updateVideo(@NonNull String videoId, String playDuration, String createdDate,String subtitlePath) {

        ContentValues values = new ContentValues();
        values.put(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PLAY_DURATION, playDuration);
        values.put(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_CREATED_DATE, createdDate);
        values.put(SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_SUBTITLE_PATH, subtitlePath);

        String selection = SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {videoId};
        Log.d(TAG, "=========updateVideo: " + videoId);
        mContentResolver.update(SamplesPersistenceContract.VideoEntry.buildVideosUri(), values, selection, selectionArgs);
    }

    @Override
    public void clearAllVideos() {
        mContentResolver.delete(SamplesPersistenceContract.VideoEntry.buildVideosUri(),null,null);
    }

    @Override
    public void clearNotExistsSubtitles() {
        Cursor cursor = mContentResolver.query(SamplesPersistenceContract.SubtitleEntry.buildSubtitlesUri(),
                new String[]{SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_PATH},
                null,
                null,
                null);
        if(cursor != null) {
            while (!cursor.moveToPosition(0) && cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_PATH));
                File file = new File(path);
                if (!file.exists()) {
                    String selection = SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_PATH + " LIKE ?";
                    String[] selectionArgs = {path};
                    mContentResolver.delete(SamplesPersistenceContract.SubtitleEntry.buildSubtitlesUri(), selection, selectionArgs);
                }
            }
            cursor.close();
        }
    }

    @Override
    public void getSubtitle(@NonNull String subtitleId, @NonNull GetSubtitleCallback callback) {

    }

    @Override
    public void getSubtitles(@NonNull GetSubtitlesCallback callback) {

    }

    @Override
    public void saveSubtitle(@NonNull FileItem fileItem) {
        ContentValues values = SamplesValues.subtitleFrom(fileItem);

        String[] projection = new String[]{SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_PATH};
        String selection = SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_PATH + " LIKE ?";
        String[] selectionArgs = new String[]{values.getAsString(SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_PATH)};

        Cursor cursor = mContentResolver.query(SamplesPersistenceContract.SubtitleEntry.buildSubtitlesUri(),
                projection,
                selection,
                selectionArgs,
                null);

        if (cursor != null) {
            if (!cursor.moveToPosition(0)) {

                mContentResolver.insert(SamplesPersistenceContract.SubtitleEntry.buildSubtitlesUri(), values);
            }
            cursor.close();
        }
        mContentResolver.notifyChange(SamplesPersistenceContract.SubtitleEntry.buildSubtitlesUri(), null);
    }

    @Override
    public void clearAllSubtitles() {
        mContentResolver.delete(SamplesPersistenceContract.SubtitleEntry.buildSubtitlesUri(),null,null);
    }
}
