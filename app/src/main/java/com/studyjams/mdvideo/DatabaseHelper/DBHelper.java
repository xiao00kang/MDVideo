package com.studyjams.mdvideo.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by syamiadmin on 2016/7/18.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    public DBHelper(Context context) {
        /**CursorFactory设置为null,使用默认值**/
        super(context, Tables.DATA_TABLE_NAME, null, Tables.VERSION);
    }

    /**
     * 初次使用数据库时初始化数据库表
     * integer primary key autoincrement
     * 使用自增长字段作为主键
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建Video数据表
        String table_video = "create table if not exists " + Tables.TABLE_VIDEO_NAME + " ( "
                + Tables.Video_id + " integer primary key autoincrement,"
                + Tables.Video_title + " text not null, "
                + Tables.Video_album + " text, "
                + Tables.Video_artist + " text,"
                + Tables.Video_displayName + " text, "
                + Tables.Video_mimeType + " text, "
                + Tables.Video_path + " text, "
                + Tables.Video_date + " text, "
                + Tables.Video_subtitlePath + " text, "
                + Tables.Video_screenOrientation + " integer, "
                + Tables.Video_bitrate + " integer, "
                + Tables.Video_size + " integer, "
                + Tables.Video_duration + " integer, "
                + Tables.Video_playDuration + " integer,"
                + Tables.Video_createdDate + " timestamp default (datetime('now', 'localtime')));";

        String table_subtitle = "create table if not exists " + Tables.TABLE_SUBTITLE + " ( "
                + Tables.Subtitle_id + " integer primary key autoincrement,"
                + Tables.Subtitle_name + " text not null, "
                + Tables.Subtitle_path + " text, "
                + Tables.Subtitle_date + " text,"
                + Tables.Subtitle_size + " text, "
                + Tables.Subtitle_mimeType + " text, "
                + Tables.Subtitle_createdDate + " timestamp default (datetime('now', 'localtime')));";
        Log.d(TAG, "onCreate() called with: " + "table_video = [" + table_video + "]" + "table_subtitle = [" + table_subtitle + "]");
        db.execSQL(table_video);
        db.execSQL(table_subtitle);
    }

    /**如果VERSION值增加,系统发现现有数据库版本不同,即会调用onUpgrade**/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //升级时增加一列，onCreate建表时需要更改代码添加，这样旧表的数据会保留
//        db.execSQL("alter table " + Common.TABLE_VIDEO_NAME + " add "
//                + Common.Video_duration + "integer");

        //完全删除已存数据重新建表
        db.execSQL("drop table if exists " + Tables.TABLE_VIDEO_NAME);
        db.execSQL("drop table if exists " + Tables.TABLE_SUBTITLE);
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
