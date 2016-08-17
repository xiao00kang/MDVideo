package com.studyjams.mdvideo.DatabaseHelper.FileTraversal;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.studyjams.mdvideo.DatabaseHelper.Tables;
import com.studyjams.mdvideo.DatabaseHelper.VideoProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class SyncService extends IntentService {

    private File STORAGE = Environment.getExternalStorageDirectory();

    private static final String ACTION_FOO = "com.studyjams.mdvideo.DatabaseHelper.FileTraversal.action.FOO";
    private static final String ACTION_BAZ = "com.studyjams.mdvideo.DatabaseHelper.FileTraversal.action.BAZ";
    private static final String ACTION_TRAVERSAL = "com.studyjams.mdvideo.DatabaseHelper.FileTraversal.action.TRAVERSAL";

    private static final String EXTRA_PARAM1 = "com.studyjams.mdvideo.DatabaseHelper.FileTraversal.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.studyjams.mdvideo.DatabaseHelper.FileTraversal.extra.PARAM2";

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

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }else if(ACTION_TRAVERSAL.equals(action)){
                handleActionTraversal();
            }
        }
    }

    /**
     * 遍历文件，拿到基本的文件信息
     */
    private void handleActionTraversal(){

        List<FileItem> list = fileTraversal(STORAGE,new VideoFileFilter());
        for (FileItem file:list){
            Log.d("ZZY", "==============: " + file.getPath());

            Cursor cursor = getContentResolver().query(VideoProvider.VIDEO_CHANGE_URI,null,
                    Tables.Video_path + " like '" + file.getPath() + "'",
                    null,
                    null);

            if (cursor != null && !cursor.moveToPosition(0)) {

                getContentResolver().insert(VideoProvider.VIDEO_PLAY_HISTORY_URI, getContentValues(file));
                cursor.close();
            }

        }
        getContentResolver().notifyChange(VideoProvider.VIDEO_CHANGE_URI,null);
    }

    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

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
                        mQueue.offer(name);
                    } else {
                        FileItem item = new FileItem();
                        item.setName(name.getName());
                        item.setPath(name.getAbsolutePath());
                        item.setDate(name.lastModified());
                        item.setSize(name.length());
                        list.add(item);
                    }
                }
            }
        }

        return list;
    }

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


        values.put(Tables.Video_title, title);
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
