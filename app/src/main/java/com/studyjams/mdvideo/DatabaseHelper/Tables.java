package com.studyjams.mdvideo.DatabaseHelper;

/**
 * Created by syamiadmin on 2016/7/19.
 */
public class Tables {

    /**数据库名**/
    public static final String DATA_TABLE_NAME = "VideoPlay";
    /**数据库版本**/
    public static final int VERSION = 1;

    /**视频文件数据表的定义**/
    public static final String TABLE_VIDEO_NAME = "VideoPlayHistory";//数据表名
    public static final String Video_id = "_id";
    public static final String Video_title="title";
    public static final String Video_album="album";
    public static final String Video_artist="artist";
    public static final String Video_displayName="displayName";
    public static final String Video_mimeType="mimeType";
    public static final String Video_path="path";
    public static final String Video_size="size";
    public static final String Video_duration="duration";
    public static final String Video_playDuration="playDuration";
    public static final String Video_createdDate="createdDate";//文件存入或在数据库更新的日期
    public static final String Video_date="last_date";//文件最后修改日期
    public static final String Video_screenOrientation="screenOrientation";//判断视频是横屏还是竖屏
    public static final String Video_bitrate="bitrate";//平均比特率
    public static final String Video_subtitlePath="subtitle";//关联的字幕地址

    /**字幕文件数据表的定义**/
    public static final String TABLE_SUBTITLE = "TableSubtitle";
    public static final String Subtitle_id = "_id";
    public static final String Subtitle_name = "name";
    public static final String Subtitle_path = "path";
    public static final String Subtitle_date = "last_date";
    public static final String Subtitle_size = "size";
    public static final String Subtitle_mimeType="mimeType";
    public static final String Subtitle_createdDate="createdDate";
}
