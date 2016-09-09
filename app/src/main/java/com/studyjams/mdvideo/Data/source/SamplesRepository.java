package com.studyjams.mdvideo.Data.source;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.studyjams.mdvideo.Data.Video;
import com.studyjams.mdvideo.Data.source.remote.FileItem;

import java.util.List;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class SamplesRepository implements VideoDataSource,SubtitleDataSource{

    private static SamplesRepository INSTANCE = null;

    private final VideoDataSource mVideosRemoteDataSource;

    // Prevent direct instantiation.
    private SamplesRepository(@NonNull VideoDataSource tasksRemoteDataSource) {
        mVideosRemoteDataSource = tasksRemoteDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @return the {@link SamplesRepository} instance
     */
    public static SamplesRepository getInstance(VideoDataSource tasksRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new SamplesRepository(tasksRemoteDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(VideoDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void clearNotExistsSubtitles() {

    }

    @Override
    public void getSubtitle(@NonNull String subtitleId, @NonNull GetSubtitleCallback callback) {

    }

    @Override
    public void getSubtitles(@NonNull GetSubtitlesCallback callback) {

    }

    @Override
    public void saveSubtitle(@NonNull FileItem fileItem) {

    }

    @Override
    public void clearNotExistsVideos() {
        mVideosRemoteDataSource.clearNotExistsVideos();
    }

    @Override
    public void deleteVideo(@NonNull String videoId) {
        mVideosRemoteDataSource.deleteVideo(videoId);
    }

    @Override
    public void getVideo(@NonNull String videoId, @NonNull final GetVideoCallback callback) {
        // Load from server
        mVideosRemoteDataSource.getVideo(videoId, new GetVideoCallback() {
            @Override
            public void onVideoLoaded(Video video) {
                callback.onVideoLoaded(video);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getVideos(@NonNull final GetVideosCallback callback) {
        mVideosRemoteDataSource.getVideos(new GetVideosCallback() {
            @Override
            public void onVideosLoaded(List<Video> videos) {
                refreshLocalDataSource(videos);
                callback.onVideosLoaded(null);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveVideo(@NonNull FileItem fileItem) {
        mVideosRemoteDataSource.saveVideo(fileItem);
    }

    @Override
    public void saveVideo(@NonNull Video video) {
        mVideosRemoteDataSource.saveVideo(video);
    }

    @Override
    public void updateVideo(@NonNull Video video) {
        mVideosRemoteDataSource.updateVideo(video);
    }

    @Override
    public void updateVideo(@NonNull String videoId, String playDuration, String createdDate) {
        mVideosRemoteDataSource.updateVideo(videoId,playDuration,createdDate);
    }

    private void refreshLocalDataSource(List<Video> videos) {
        for (Video video : videos) {
            mVideosRemoteDataSource.saveVideo(video);
        }
    }

    public interface LoadDataCallback {
        void onDataLoaded(Cursor data);

        void onDataEmpty();

        void onDataNotAvailable();

        void onDataReset();
    }
}
