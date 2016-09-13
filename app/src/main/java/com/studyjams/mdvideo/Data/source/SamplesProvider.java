package com.studyjams.mdvideo.Data.source;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.studyjams.mdvideo.Data.source.local.SamplesDbHelper;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract.SubtitleEntry;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract.VideoEntry;

public class SamplesProvider extends ContentProvider {
    private static final String TAG = "SamplesProvider";

    private static final int VIDEO_ALL = 0;
    private static final int VIDEO_ONE = 1;
    private static final int SUBTITLE_ALL = 2;
    private static final int SUBTITLE_ONE = 3;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SamplesDbHelper mSamplesDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SamplesPersistenceContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, VideoEntry.TABLE_VIDEO_NAME, VIDEO_ALL);
        matcher.addURI(authority, VideoEntry.TABLE_VIDEO_NAME + "/*", VIDEO_ONE);
        matcher.addURI(authority,SubtitleEntry.TABLE_SUBTITLE_NAME, SUBTITLE_ALL);
        matcher.addURI(authority,SubtitleEntry.TABLE_SUBTITLE_NAME + "/*", SUBTITLE_ONE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mSamplesDbHelper = new SamplesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType: " + uri);
        switch (sUriMatcher.match(uri)) {
            case VIDEO_ALL:
                return SamplesPersistenceContract.CONTENT_VIDEO_TYPE;
            case VIDEO_ONE:
                return SamplesPersistenceContract.CONTENT_VIDEO_ITEM_TYPE;
            case SUBTITLE_ALL:
                return SamplesPersistenceContract.CONTENT_SUBTITLE_TYPE;
            case SUBTITLE_ONE:
                return SamplesPersistenceContract.CONTENT_SUBTITLE_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete: " + uri);
        final SQLiteDatabase db = mSamplesDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case VIDEO_ALL:
                rowsDeleted = db.delete(VideoEntry.TABLE_VIDEO_NAME, selection, selectionArgs);
                break;
            case SUBTITLE_ALL:
                rowsDeleted = db.delete(SubtitleEntry.TABLE_SUBTITLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Wrong URI: " + uri);
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "insert: " + uri);
        final SQLiteDatabase db = mSamplesDbHelper.getWritableDatabase();
        Uri returnUri;
        long _id;
        switch (sUriMatcher.match(uri)) {
            case VIDEO_ALL:
                _id = db.insert(VideoEntry.TABLE_VIDEO_NAME, null, values);
                if (_id > 0) {
                    returnUri = VideoEntry.buildVideosUriWith(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SUBTITLE_ALL:

                _id = db.insert(SubtitleEntry.TABLE_SUBTITLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = SubtitleEntry.buildSubtitlesUriWith(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query: " + uri);
        final SQLiteDatabase db = mSamplesDbHelper.getWritableDatabase();
        Cursor retCursor;
        String[] where = {uri.getLastPathSegment()};
        switch (sUriMatcher.match(uri)) {
            case VIDEO_ALL:
                retCursor = db.query(
                        VideoEntry.TABLE_VIDEO_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case VIDEO_ONE:

                retCursor = db.query(
                        VideoEntry.TABLE_VIDEO_NAME,
                        projection,
                        VideoEntry.COLUMN_VIDEO_ENTRY_ID + " = ?",
                        where,
                        null,
                        null,
                        sortOrder
                );
                break;

            case SUBTITLE_ALL:
                retCursor = db.query(
                        SubtitleEntry.TABLE_SUBTITLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SUBTITLE_ONE:
                retCursor = db.query(
                        SubtitleEntry.TABLE_SUBTITLE_NAME,
                        projection,
                        SubtitleEntry.COLUMN_SUBTITLE_ENTRY_ID + " = ?",
                        where,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update: " + uri);
        final SQLiteDatabase db = mSamplesDbHelper.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case VIDEO_ALL:

                rowsUpdated = db.update(VideoEntry.TABLE_VIDEO_NAME, values, selection, selectionArgs);
                break;
            case VIDEO_ONE:
                rowsUpdated = db.update(VideoEntry.TABLE_VIDEO_NAME, values, selection, selectionArgs);
                break;
            case SUBTITLE_ALL:
                rowsUpdated = db.update(SubtitleEntry.TABLE_SUBTITLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
