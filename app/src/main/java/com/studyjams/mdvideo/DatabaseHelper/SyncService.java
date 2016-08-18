package com.studyjams.mdvideo.DatabaseHelper;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;

import com.studyjams.mdvideo.DatabaseHelper.FileTraversal.FileItem;
import com.studyjams.mdvideo.DatabaseHelper.FileTraversal.VideoFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SyncService extends IntentService {

    private static final String ACTION_CHECK = "com.studyjams.mdvideo.action.CHECK";
    private static final String ACTION_UPDATE = "com.studyjams.mdvideo.action.UPDATE";
    private static final String ACTION_TRAVERSAL = "com.studyjams.mdvideo.action.TRAVERSAL";

    private static final String EXTRA_ID = "com.studyjams.mdvideo.ID";
    private static final String EXTRA_PLAYDURATION = "com.studyjams.mdvideo.PLAYDURATION";
    private static final String EXTRA_CREATEDDATE = "com.studyjams.mdvideo.CREATEDDATE";

    public SyncService() {
        super("SyncService");
    }

    /**
     * 遍历SD卡文件
     * @param context
     */
    public static void startActionTraversal(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_TRAVERSAL);
        context.startService(intent);
    }

    /**
     * 校验列表中的文件是否还存在
     * @param context
     */
    public static void startActionCheck(Context context) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_CHECK);
        context.startService(intent);
    }

    /**
     * 更新播放历史纪录
     */
    public static void startActionUpdate(Context context, String id, String playDuration, String createdDate) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_UPDATE);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_PLAYDURATION, playDuration);
        intent.putExtra(EXTRA_CREATEDDATE, createdDate);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK.equals(action)) {
                handleActionCheck();
            } else if (ACTION_UPDATE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_ID);
                final String param2 = intent.getStringExtra(EXTRA_PLAYDURATION);
                final String param3 = intent.getStringExtra(EXTRA_CREATEDDATE);
                handleActionUpdate(param1, param2,param3);
            }else if(ACTION_TRAVERSAL.equals(action)){
                handleActionTraversal();
            }
        }
    }

    /**
     * 遍历文件，拿到基本的文件信息
     * 然后插入到数据库中保存
     */
    private void handleActionTraversal(){
        File storage = Environment.getExternalStorageDirectory();
        List<FileItem> list = fileTraversal(storage,new VideoFileFilter());
        for (FileItem file:list){

            Cursor cursor = getContentResolver().query(VideoProvider.VIDEO_PLAY_HISTORY_URI,null,
                    Tables.Video_path + " like '" + file.getPath() + "'",
                    null,
                    null);
            if (cursor != null) {
                if (!cursor.moveToPosition(0)) {

                    getContentResolver().insert(VideoProvider.VIDEO_PLAY_HISTORY_URI, getContentValues(file));
                }
                cursor.close();
            }

        }
        getContentResolver().notifyChange(VideoProvider.VIDEO_CHANGE_URI,null);
    }

    /**
     * 检查数据库中保存的视频文件信息是否还存在
     * 若不存在则删除记录
     */
    private void handleActionCheck() {
        Cursor cursor = getContentResolver().query(VideoProvider.VIDEO_PLAY_HISTORY_URI,
                new String[]{Tables.Video_path},
                null,null,null);
        if(cursor != null) {
            while (!cursor.moveToPosition(0) && cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(Tables.Video_path));
                File file = new File(path);
                if (!file.exists()) {
                    getContentResolver().delete(VideoProvider.VIDEO_PLAY_HISTORY_URI,
                            Tables.Video_path + " like '" + path + "'",
                            null);
                }
            }
            cursor.close();
            getContentResolver().notifyChange(VideoProvider.VIDEO_CHANGE_URI,null);
        }

    }

    /**
     * 更新播放历史
     */
    private void handleActionUpdate(String id, String playDuration, String createdDate) {
        ContentValues values = new ContentValues();
        values.put(Tables.Video_playDuration, playDuration);
        values.put(Tables.Video_createdDate, createdDate);
        Uri updateUri = ContentUris.withAppendedId(VideoProvider.VIDEO_PLAY_HISTORY_URI, Long.valueOf(id));

        getContentResolver().update(updateUri, values, null,
                new String[]{Tables.Video_playDuration, Tables.Video_createdDate});
        getContentResolver().notifyChange(VideoProvider.VIDEO_CHANGE_URI,null);
    }

    /**
     * 遍历SD卡
     * @param root
     * @param filter
     * @return
     */
    private List<FileItem> fileTraversal(File root, VideoFileFilter filter) {

        Queue<File> mQueue = new LinkedList<>();
        List<FileItem> list = new ArrayList<>();
        boolean status = mQueue.offer(root);
        if (status) {
            while (!mQueue.isEmpty()) {
                File file = mQueue.poll();
                for (File name : file.listFiles(filter)) {
                    //If file/directory can be read by the Application
                    if (name.isDirectory()) {
                        //过滤掉缓存的隐藏文件夹，缓存文件有很多不能播放的文件
                        if (!name.getPath().contains("/.")) {
                            mQueue.offer(name);
                        }
                    } else {
                        FileItem item = new FileItem();
                        item.setName(name.getName());
                        item.setPath(name.getAbsolutePath());
                        item.setDate(name.lastModified());
                        long length = name.length();
                        //过滤掉长度为0的文件
                        if (length != 0) {
                            item.setSize(length);
                            list.add(item);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 读取视频文件信息
     * @param fileItem
     * @return
     */
    private ContentValues getContentValues(FileItem fileItem){
        ContentValues values = new ContentValues();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(fileItem.getPath());
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);//艺术家
        String displayName = "";
        String mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);//类型
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
        String date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);//日期
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);//平均比特率


        values.put(Tables.Video_title, fileItem.getName());
        values.put(Tables.Video_album, album);
        values.put(Tables.Video_artist, artist);
        values.put(Tables.Video_displayName, displayName);
        values.put(Tables.Video_mimeType, mimeType);
        values.put(Tables.Video_path, fileItem.getPath());
        values.put(Tables.Video_size, fileItem.getSize());
        values.put(Tables.Video_duration, duration);
        values.put(Tables.Video_playDuration, -1);
        values.put(Tables.Video_createdDate, date);
        return values;
    }
}
