package com.studyjams.mdvideo.LocalVideo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.studyjams.mdvideo.Data.Video;
import com.studyjams.mdvideo.Data.source.VideoDataSource;

import java.util.List;

/**
 * Created by syamiadmin on 2016/9/2.
 */
public class LocalVideoPresenter implements LocalVideoContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>,VideoDataSource.GetVideosCallback{

    private final LoaderManager mLoaderManager;
    private final LocalVideoContract.View mVideosView;

    public LocalVideoPresenter(LoaderManager loaderManager,LocalVideoContract.View videoView){
        mLoaderManager = loaderManager;
        mVideosView = videoView;
    }

    @Override
    public void start() {

    }

    @Override
    public void loadVideos() {

    }

    @Override
    public void playVideo(@NonNull Video video) {

    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void refreshData() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onVideosLoaded(List<Video> videos) {

    }
}