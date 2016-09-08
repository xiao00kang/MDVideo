package com.studyjams.mdvideo.Data.source;

import android.support.annotation.NonNull;

import com.studyjams.mdvideo.Data.Video;

import java.util.List;

/**
 * Created by syamiadmin on 2016/9/8.
 */
public interface VideoDataSource {

    interface GetVideosCallback {

        void onVideosLoaded(List<Video> videos);

        void onDataNotAvailable();
    }

    interface GetVideoCallback {

        void onVideoLoaded(Video video);

        void onDataNotAvailable();
    }

    void getVideos(@NonNull GetVideosCallback callback);

    void getVideo(@NonNull String videoId, @NonNull GetVideoCallback callback);

    void saveVideo(@NonNull Video video);

    void updateVideo(@NonNull Video video);

    void updateVideo(@NonNull String videoId);

    void activateTask(@NonNull Video video);

    void activateTask(@NonNull String videoId);

    void clearCompletedTasks();

    void deleteAllVideos();

    void deleteVideo(@NonNull String videoId);
}
