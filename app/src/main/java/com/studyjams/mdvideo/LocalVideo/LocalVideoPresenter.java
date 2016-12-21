package com.studyjams.mdvideo.LocalVideo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.studyjams.mdvideo.Data.bean.Video;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.Data.source.remote.SyncService;
import com.studyjams.mdvideo.PlayerModule.ExoPlayerV2.PlayerActivityV2;
import com.studyjams.mdvideo.Util.D;

/**
 * Created by syamiadmin on 2016/9/2.
 */
public class LocalVideoPresenter implements LocalVideoContract.Presenter, LoaderManager.LoaderCallbacks<Cursor>{

    private final LoaderManager mLoaderManager;
    private final LocalVideoContract.View mVideosView;
    private Context mContext;

    private static final int LOCAL_VIDEO_LOADER = 0;

    /**
     * 构造方法参数的含义
     * context 主要是跳转至播放activity的，在官方的实践中这部分放在activity(Fragment)里完成。(大概Activity的context不应该传到这一层？存疑)
     * loadManager 用来加载本地数据
     * videoView Fragment本身有实现这个接口，用于更新UI的一些操作
     * 在官方的实践中，远程数据是通过模拟来实现的，在这一层的程序逻辑走向是这样的，首先调用的是start(),然后是loadVideos。官方实践中与本项目有一个区别
     * 它的远程请求是有回调的，通过远程数据的请求并在远程请求那一层做缓存，判断要返回的状态（比如有数据、无数据）。然后在回调后去初始化loadManager加载数据。
     * 不得不说这一层绕的有点远。还有一点是，LoadManager不是应该放在onCreate中初始化吗？放在start（）回调不是变成onResume了吗
     *
     * 我自己之前在项目中的做法是，在程序启动的最初阶段（MainActivity的onCreated）通过异步去开启SD卡文件的遍历。这样不会阻塞主线程的流程。同时在主线程绘制view
     * 的过程中，完了之后会初始化loader去异步查询数据库。这个查询与写入是并发的，这就可能存在loader第一次查询并没有数据（遍历SD卡是需要时间的，
     * 查询几乎是看不到现象的）所以这里存在等遍历完后provider会notify loader再加载一次。这两个流程我还需要仔细的比较下，暂时先按自己的流程走。
     *
     * @param context
     * @param loaderManager
     * @param videoView
     */
    public LocalVideoPresenter(Context context, LoaderManager loaderManager, LocalVideoContract.View videoView){
        mLoaderManager = loaderManager;
        mVideosView = videoView;
        mContext = context;
        mVideosView.setPresenter(this);
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(LOCAL_VIDEO_LOADER, null, this);
    }

    @Override
    public void loadVideos() {

        SyncService.startActionCheck(mContext);
        SyncService.startActionTraversal(mContext);
    }

    @Override
    public void playVideo(@NonNull Video video) {

        Intent intent = new Intent(mContext, PlayerActivityV2.class);
        intent.setData(Uri.parse(video.getPath()));
        intent.setAction(PlayerActivityV2.ACTION_VIEW);
        intent.putExtra(PlayerActivityV2.CONTENT_ID_EXTRA, video.getId());
        intent.putExtra(PlayerActivityV2.CONTENT_TYPE_INTENT, D.TYPE_VIDEO);
        intent.putExtra(PlayerActivityV2.CONTENT_TYPE_EXTRA, video.getMimeType());
        intent.putExtra(PlayerActivityV2.CONTENT_SUBTITLE_EXTRA,video.getSubtitlePath());
        intent.putExtra(PlayerActivityV2.CONTENT_POSITION_EXTRA,0L);

        /**
        Intent intent = new Intent(mContext, PlayerActivity.class)
                .setData(Uri.parse(video.getPath()))
                .putExtra(PlayerActivity.CONTENT_ID_EXTRA, video.getId())
                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, video.getMimeType())
                .putExtra(PlayerActivity.PROVIDER_EXTRA,"0");
         v1版本的参数**/
        mContext.startActivity(intent);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        //activity的参数回传，暂时没这方面业务，保留的接口
    }

    @Override
    public void refreshData() {
        mLoaderManager.restartLoader(LOCAL_VIDEO_LOADER,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOCAL_VIDEO_LOADER:
                return new CursorLoader(
                        mContext,
                        SamplesPersistenceContract.VideoEntry.buildVideosUri(),
                        null,
                        null,
                        null,
                        null
                );
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()) {
            case LOCAL_VIDEO_LOADER:
                mVideosView.showNoVideos();
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case LOCAL_VIDEO_LOADER:
                if (data.moveToLast()) {

                    mVideosView.showVideos(data);
                } else {

                    mVideosView.showNoVideos();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }
}