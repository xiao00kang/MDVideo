package com.studyjams.mdvideo.Data.source;

import android.support.annotation.NonNull;

import com.studyjams.mdvideo.Data.bean.Subtitle;
import com.studyjams.mdvideo.Data.source.remote.FileItem;

import java.util.List;

/**
 * Created by syamiadmin on 2016/9/8.
 */
public interface SubtitleDataSource {
    interface GetSubtitlesCallback {

        void onSubtitlesLoaded(List<Subtitle> subtitles);

        void onDataNotAvailable();
    }

    interface GetSubtitleCallback {

        void onSubtitleLoaded(Subtitle subtitle);

        void onDataNotAvailable();
    }

    void getSubtitles(@NonNull GetSubtitlesCallback callback);

    void getSubtitle(@NonNull String subtitleId, @NonNull GetSubtitleCallback callback);

    void saveSubtitle(@NonNull FileItem fileItem);

    void clearNotExistsSubtitles();

    void clearAllSubtitles();
}
