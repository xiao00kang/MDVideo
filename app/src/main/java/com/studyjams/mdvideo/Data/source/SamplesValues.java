package com.studyjams.mdvideo.Data.source;

import android.content.ContentValues;
import android.media.MediaMetadataRetriever;

import com.studyjams.mdvideo.Data.Video;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract.SubtitleEntry;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract.VideoEntry;
import com.studyjams.mdvideo.Data.source.remote.FileItem;
import com.studyjams.mdvideo.Util.Tools;

import java.util.UUID;

/**
 * Created by syamiadmin on 2016/9/9.
 */
public class SamplesValues {

    public static ContentValues videoFrom(FileItem fileItem) {

        ContentValues values = new ContentValues();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(fileItem.getPath());
//        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        //艺术家
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String displayName = "";
        //类型
        String mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        //时长(毫秒)
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        //日期
        String date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        //宽
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        //高
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        //平均比特率
        String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);

        values.put(VideoEntry.COLUMN_VIDEO_ENTRY_ID, UUID.randomUUID().toString());
        values.put(VideoEntry.COLUMN_VIDEO_TITLE, fileItem.getName());
        values.put(VideoEntry.COLUMN_VIDEO_ALBUM, album);
        values.put(VideoEntry.COLUMN_VIDEO_ARTIST, artist);
        values.put(VideoEntry.COLUMN_VIDEO_DISPLAY_NAME, displayName);
        values.put(VideoEntry.COLUMN_VIDEO_MIME_TYPE, mimeType);
        values.put(VideoEntry.COLUMN_VIDEO_PATH, fileItem.getPath());
        values.put(VideoEntry.COLUMN_VIDEO_SIZE, fileItem.getSize());
        values.put(VideoEntry.COLUMN_VIDEO_DURATION, duration);
        values.put(VideoEntry.COLUMN_VIDEO_PLAY_DURATION, -1);
        values.put(VideoEntry.COLUMN_VIDEO_CREATED_DATE, date);
        values.put(VideoEntry.COLUMN_VIDEO_DATE, fileItem.getDate());
        values.put(VideoEntry.COLUMN_VIDEO_SCREEN_WIDTH, width);
        values.put(VideoEntry.COLUMN_VIDEO_SCREEN_HEIGHT, height);
        values.put(VideoEntry.COLUMN_VIDEO_BITRATE, bitrate);
        values.put(VideoEntry.COLUMN_VIDEO_SUBTITLE_PATH,"");
        return values;
    }

    public static ContentValues videoFrom(Video video) {

        ContentValues values = new ContentValues();

        values.put(VideoEntry.COLUMN_VIDEO_ENTRY_ID, video.getId());
        values.put(VideoEntry.COLUMN_VIDEO_TITLE, video.getTitle());
        values.put(VideoEntry.COLUMN_VIDEO_ALBUM, video.getAlbum());
        values.put(VideoEntry.COLUMN_VIDEO_ARTIST, video.getArtist());
        values.put(VideoEntry.COLUMN_VIDEO_DISPLAY_NAME, video.getDisplayName());
        values.put(VideoEntry.COLUMN_VIDEO_MIME_TYPE, video.getMimeType());
        values.put(VideoEntry.COLUMN_VIDEO_PATH, video.getPath());
        values.put(VideoEntry.COLUMN_VIDEO_SIZE, video.getSize());
        values.put(VideoEntry.COLUMN_VIDEO_DURATION, video.getDuration());
        values.put(VideoEntry.COLUMN_VIDEO_PLAY_DURATION, video.getPlayDuration());
        values.put(VideoEntry.COLUMN_VIDEO_CREATED_DATE, video.getCreatedDate());
        values.put(VideoEntry.COLUMN_VIDEO_DATE, video.getDate());
        values.put(VideoEntry.COLUMN_VIDEO_SCREEN_WIDTH, video.getWidth());
        values.put(VideoEntry.COLUMN_VIDEO_SCREEN_HEIGHT, video.getHeight());
        values.put(VideoEntry.COLUMN_VIDEO_BITRATE, video.getBitrate());
        values.put(VideoEntry.COLUMN_VIDEO_SUBTITLE_PATH,video.getSubtitlePath());
        return values;
    }

    public static ContentValues subtitleFrom(FileItem fileItem) {
        ContentValues values = new ContentValues();
        values.put(SubtitleEntry.COLUMN_SUBTITLE_ENTRY_ID, UUID.randomUUID().toString());
        values.put(SubtitleEntry.COLUMN_SUBTITLE_NAME,fileItem.getName());
        values.put(SubtitleEntry.COLUMN_SUBTITLE_PATH,fileItem.getPath());
        values.put(SubtitleEntry.COLUMN_SUBTITLE_DATE,fileItem.getDate());
        values.put(SubtitleEntry.COLUMN_SUBTITLE_SIZE,fileItem.getSize());
        values.put(SubtitleEntry.COLUMN_SUBTITLE_CREATED_DATE, Tools.getCurrentTimeMillis());
        return values;
    }
}
