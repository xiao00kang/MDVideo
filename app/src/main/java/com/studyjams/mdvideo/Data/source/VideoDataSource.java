package com.studyjams.mdvideo.Data.source;

import android.support.annotation.NonNull;

import com.studyjams.mdvideo.Data.bean.Video;
import com.studyjams.mdvideo.Data.source.remote.FileItem;

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

    void saveVideo(@NonNull FileItem fileItem);

    void saveVideo(@NonNull Video video);

    void updateVideo(@NonNull Video video);

    void updateVideo(@NonNull String videoId, String playDuration, String createdDate);

    void clearNotExistsVideos();

    void deleteVideo(@NonNull String videoId);
}
