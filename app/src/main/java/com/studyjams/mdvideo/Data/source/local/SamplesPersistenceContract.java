package com.studyjams.mdvideo.Data.source.local;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.studyjams.mdvideo.BuildConfig;

/**
 * Created by syamiadmin on 2016/9/8.
 */
public final class SamplesPersistenceContract {

    private static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    private static final String CONTENT_SCHEME = "content://";
    private static final String SEPARATOR = "/";
    private static final String POINT = ".";
    private static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT_SCHEME + CONTENT_AUTHORITY);
    private static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/";
    private static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/";
    private static final String CONTENT_CURSOR_DIR_VND = "vnd.android.cursor.dir/vnd.";
    private static final String CONTENT_CURSOR_ITEM_VND = "vnd.android.cursor.item/vnd.";
    public static final String VND_ANDROID_CURSOR_ITEM_VND = CONTENT_CURSOR_ITEM_VND + CONTENT_AUTHORITY + POINT;
    public static final String VND_ANDROID_CURSOR_DIR_VND = CONTENT_CURSOR_DIR_VND + CONTENT_AUTHORITY + POINT;

    /**Video**/
    public static final String CONTENT_VIDEO_TYPE = CONTENT_TYPE_DIR + CONTENT_AUTHORITY + SEPARATOR + VideoEntry.TABLE_VIDEO_NAME;
    public static final String CONTENT_VIDEO_ITEM_TYPE = CONTENT_TYPE_ITEM + CONTENT_AUTHORITY + SEPARATOR + VideoEntry.TABLE_VIDEO_NAME;

    /**Subtitle**/
    public static final String CONTENT_SUBTITLE_TYPE = CONTENT_TYPE_DIR + CONTENT_AUTHORITY + SEPARATOR + SubtitleEntry.TABLE_SUBTITLE_NAME;
    public static final String CONTENT_SUBTITLE_ITEM_TYPE = CONTENT_TYPE_ITEM + CONTENT_AUTHORITY + SEPARATOR + SubtitleEntry.TABLE_SUBTITLE_NAME;

    public static Uri getBaseVideoUri(String taskId) {
        return Uri.parse(CONTENT_SCHEME + CONTENT_VIDEO_ITEM_TYPE + SEPARATOR + taskId);
    }

    public static abstract class VideoEntry implements BaseColumns {

        public static final String TABLE_VIDEO_NAME = "video";//数据表名
        public static final String COLUMN_VIDEO_ENTRY_ID = "entry_id";
        public static final String COLUMN_VIDEO_TITLE="title";
        public static final String COLUMN_VIDEO_ALBUM="album";
        public static final String COLUMN_VIDEO_ARTIST="artist";
        public static final String COLUMN_VIDEO_DISPLAY_NAME="displayName";
        public static final String COLUMN_VIDEO_MIME_TYPE="mimeType";
        public static final String COLUMN_VIDEO_PATH="path";
        public static final String COLUMN_VIDEO_SIZE="size";
        public static final String COLUMN_VIDEO_DURATION="duration";
        public static final String COLUMN_VIDEO_PLAY_DURATION="playDuration";
        public static final String COLUMN_VIDEO_CREATED_DATE="createdDate";//文件存入或在数据库更新的日期
        public static final String COLUMN_VIDEO_DATE="last_date";//文件最后修改日期
        public static final String COLUMN_VIDEO_SCREEN_ORIENTATION="screenOrientation";//判断视频是横屏还是竖屏
        public static final String COLUMN_VIDEO_BITRATE="bitrate";//平均比特率
        public static final String COLUMN_VIDEO_SUBTITLE_PATH="subtitle";//关联的字幕地址

        public static final Uri CONTENT_VIDEO_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_VIDEO_NAME).build();

        public static String[] VIDEOS_COLUMNS = new String[]{
                VideoEntry._ID,
                VideoEntry.COLUMN_VIDEO_ENTRY_ID,
                VideoEntry.COLUMN_VIDEO_TITLE,
                VideoEntry.COLUMN_VIDEO_ALBUM,
                VideoEntry.COLUMN_VIDEO_ARTIST,
                VideoEntry.COLUMN_VIDEO_DISPLAY_NAME,
                VideoEntry.COLUMN_VIDEO_MIME_TYPE,
                VideoEntry.COLUMN_VIDEO_PATH,
                VideoEntry.COLUMN_VIDEO_SIZE,
                VideoEntry.COLUMN_VIDEO_DURATION,
                VideoEntry.COLUMN_VIDEO_PLAY_DURATION,
                VideoEntry.COLUMN_VIDEO_CREATED_DATE,
                VideoEntry.COLUMN_VIDEO_DATE,
                VideoEntry.COLUMN_VIDEO_SCREEN_ORIENTATION,
                VideoEntry.COLUMN_VIDEO_BITRATE,
                VideoEntry.COLUMN_VIDEO_SUBTITLE_PATH
        };


        public static Uri buildVideosUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_VIDEO_URI, id);
        }

        public static Uri buildVideosUriWith(String id) {
            return CONTENT_VIDEO_URI.buildUpon().appendPath(id).build();
        }

        public static Uri buildVideosUri() {
            return CONTENT_VIDEO_URI.buildUpon().build();
        }

    }

    public static abstract class SubtitleEntry implements BaseColumns {
        public static final String TABLE_SUBTITLE_NAME = "subtitle";
        public static final String COLUMN_SUBTITLE_ENTRY_ID = "entry_id";
        public static final String COLUMN_SUBTITLE_NAME = "name";
        public static final String COLUMN_SUBTITLE_PATH = "path";
        public static final String COLUMN_SUBTITLE_DATE = "last_date";
        public static final String COLUMN_SUBTITLE_SIZE = "size";
        public static final String COLUMN_SUBTITLE_MIME_TYPE="mimeType";
        public static final String COLUMN_SUBTITLE_CREATED_DATE="createdDate";

        public static final Uri CONTENT_SUBTITLE_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_SUBTITLE_NAME).build();

        public static String[] SUBTITLES_COLUMNS = new String[]{
                SubtitleEntry._ID,
                SubtitleEntry.COLUMN_SUBTITLE_ENTRY_ID,
                SubtitleEntry.COLUMN_SUBTITLE_NAME,
                SubtitleEntry.COLUMN_SUBTITLE_PATH,
                SubtitleEntry.COLUMN_SUBTITLE_SIZE,
                SubtitleEntry.COLUMN_SUBTITLE_CREATED_DATE,
                SubtitleEntry.COLUMN_SUBTITLE_MIME_TYPE,
                SubtitleEntry.COLUMN_SUBTITLE_DATE

        };


        public static Uri buildSubtitlesUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_SUBTITLE_URI, id);
        }

        public static Uri buildSubtitlesUriWith(String id) {
            return CONTENT_SUBTITLE_URI.buildUpon().appendPath(id).build();
        }

        public static Uri buildSubtitlesUri() {
            return CONTENT_SUBTITLE_URI.buildUpon().build();
        }
    }

}
