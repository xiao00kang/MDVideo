package com.studyjams.mdvideo.RecordVideo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.studyjams.mdvideo.Data.Video;
import com.studyjams.mdvideo.Data.source.local.SamplesPersistenceContract;
import com.studyjams.mdvideo.PlayerModule.PlayerActivity;

/**
 * Created by syamiadmin on 2016/9/23.
 */

public class VideoPlayHistoryPresenter implements VideoPlayHistoryContract.Presenter,LoaderManager.LoaderCallbacks<Cursor>{

    private final LoaderManager mLoaderManager;
    private final VideoPlayHistoryContract.View mVideosView;
    private Context mContext;

    private static final int VIDEO_PLAY_HISTORY_LOADER = 1;

    public VideoPlayHistoryPresenter(Context context, LoaderManager loaderManager, VideoPlayHistoryContract.View videoView){
        mLoaderManager = loaderManager;
        mVideosView = videoView;
        mContext = context;
        mVideosView.setPresenter(this);
    }

    @Override
    public void playVideo(@NonNull Video video) {
        Intent intent = new Intent(mContext, PlayerActivity.class)
                .setData(Uri.parse(video.getPath()))
                .putExtra(PlayerActivity.CONTENT_ID_EXTRA, video.getId())
                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, video.getMimeType())
                .putExtra(PlayerActivity.PROVIDER_EXTRA,video.getPlayDuration());
        mContext.startActivity(intent);
    }

    @Override
    public void refreshData() {
        mLoaderManager.restartLoader(VIDEO_PLAY_HISTORY_LOADER,null,this);
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(VIDEO_PLAY_HISTORY_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case VIDEO_PLAY_HISTORY_LOADER:
                return new CursorLoader(
                        mContext,
                        SamplesPersistenceContract.VideoEntry.buildVideosUri(),
                        null,
                        SamplesPersistenceContract.VideoEntry.COLUMN_VIDEO_PLAY_DURATION + " > -1",
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
            case VIDEO_PLAY_HISTORY_LOADER:
                mVideosView.showNoVideos();
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case VIDEO_PLAY_HISTORY_LOADER:
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
