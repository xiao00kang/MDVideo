package com.studyjams.mdvideo.Data.source.local;

import android.support.annotation.NonNull;

import com.studyjams.mdvideo.Data.Video;
import com.studyjams.mdvideo.Data.source.SubtitleDataSource;
import com.studyjams.mdvideo.Data.source.VideoDataSource;

/**
 * Created by syamiadmin on 2016/9/8.
 */
public class SamplesLocalDataSource implements VideoDataSource,SubtitleDataSource {

    @Override
    public void activateTask(@NonNull Video video) {

    }

    @Override
    public void activateTask(@NonNull String videoId) {

    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void deleteAllVideos() {

    }

    @Override
    public void deleteVideo(@NonNull String videoId) {

    }

    @Override
    public void getVideo(@NonNull String videoId, @NonNull GetVideoCallback callback) {

    }

    @Override
    public void getVideos(@NonNull GetVideosCallback callback) {

    }

    @Override
    public void saveVideo(@NonNull Video video) {

    }

    @Override
    public void updateVideo(@NonNull Video video) {

    }

    @Override
    public void updateVideo(@NonNull String videoId) {

    }
}
