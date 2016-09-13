package com.studyjams.mdvideo.Data.source.remote;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.studyjams.mdvideo.Data.source.local.SamplesLocalDataSource;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SyncService extends IntentService {

    private static final String TAG = "SyncService";
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
     * 这个地方数据的分离只是判断了字幕文件的后缀，因现阶段只支持srt字幕
     * 这个肯定是需要一个通用的算法来实现的（2016.8.25）
     */
    private void handleActionTraversal(){
        File storage = Environment.getExternalStorageDirectory();
        List<FileItem> list = fileTraversal(storage,new VideoFileFilter());
        for (FileItem file:list){
            String filePath = file.getPath();
            if(!filePath.endsWith("srt")) {
                SamplesLocalDataSource.getInstance(getContentResolver()).saveVideo(file);
            }else{
                SamplesLocalDataSource.getInstance(getContentResolver()).saveSubtitle(file);
            }
        }
    }

    /**
     * 检查数据库中保存的视频文件信息是否还存在
     * 若不存在则删除记录
     */
    private void handleActionCheck() {

        SamplesLocalDataSource.getInstance(getContentResolver()).clearNotExistsVideos();
        SamplesLocalDataSource.getInstance(getContentResolver()).clearNotExistsSubtitles();
    }

    /**
     * 更新播放历史
     */
    private void handleActionUpdate(String id, String playDuration, String createdDate) {

        SamplesLocalDataSource.getInstance(getContentResolver()).updateVideo(id,playDuration,createdDate);
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
}
