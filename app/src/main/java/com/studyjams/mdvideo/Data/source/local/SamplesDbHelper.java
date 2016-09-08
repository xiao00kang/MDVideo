package com.studyjams.mdvideo.Data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by syamiadmin on 2016/7/18.
 */
public class SamplesDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    /**数据库名**/
    public static final String DATA_BASE_NAME = "Samples";
    /**数据库版本**/
    public static final int DATA_BASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String BOOLEAN_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_VIDEO_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + SamplesPersistenceContract.VideoEntry.TABLE_VIDEO_NAME + " (" +
                    SamplesPersistenceContract.VideoEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_TITLE + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_ALBUM + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_ARTIST + BOOLEAN_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_MIME_TYPE + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PATH + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_DATE + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_SUBTITLE_PATH + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_SCREEN_ORIENTATION + BOOLEAN_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_BITRATE + INTEGER_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_SIZE + INTEGER_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_DURATION + INTEGER_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PLAY_DURATION + INTEGER_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_CREATED_DATE + INTEGER_TYPE +
            " )";

    private static final String SQL_CREATE_SUBTITLE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + SamplesPersistenceContract.SubtitleEntry.TABLE_SUBTITLE_NAME + " (" +
                    SamplesPersistenceContract.SubtitleEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_NAME + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_MIME_TYPE + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_PATH + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_DATE + TEXT_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_SIZE + INTEGER_TYPE + COMMA_SEP +
                    SamplesPersistenceContract.SubtitleEntry.COLUMN_SUBTITLE_CREATED_DATE + INTEGER_TYPE +
                    " )";

    private static final String SQL_UPDATE_SUBTITLE_ENTRIES = "DROP TABLE IF EXISTS " +
            SamplesPersistenceContract.SubtitleEntry.TABLE_SUBTITLE_NAME;
    private static final String SQL_UPDATE_VIDEO_ENTRIES = "DROP TABLE IF EXISTS " +
            SamplesPersistenceContract.VideoEntry.TABLE_VIDEO_NAME;

    public SamplesDbHelper(Context context) {
        /**CursorFactory设置为null,使用默认值**/
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    /**
     * 初次使用数据库时初始化数据库表
     * integer primary key autoincrement
     * 使用自增长字段作为主键
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_VIDEO_ENTRIES);
        db.execSQL(SQL_CREATE_SUBTITLE_ENTRIES);
    }

    /**如果VERSION值增加,系统发现现有数据库版本不同,即会调用onUpgrade**/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //升级时增加一列，onCreate建表时需要更改代码添加，这样旧表的数据会保留
//        db.execSQL("alter table " + Common.TABLE_VIDEO_NAME + " add "
//                + Common.Video_duration + "integer");

        db.execSQL(SQL_UPDATE_SUBTITLE_ENTRIES);
        db.execSQL(SQL_UPDATE_VIDEO_ENTRIES);
        onCreate(db);
    }

    //在第一次调用getReadableDatabase()或getWritableDatabase()时调用
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //开启WAL模式
        setWriteAheadLoggingEnabled(true);
    }
}
