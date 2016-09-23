package com.studyjams.mdvideo.HistoryVideo;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.studyjams.mdvideo.BasePresenter;
import com.studyjams.mdvideo.BaseView;
import com.studyjams.mdvideo.Data.Video;
import com.studyjams.mdvideo.LocalVideo.LocalVideoContract;

/**
 * Created by syamiadmin on 2016/9/23.
 */

public interface VideoPlayHistoryContract {
    interface View extends BaseView<LocalVideoContract.Presenter> {
        void showNoVideos();
        void showVideos(Cursor cursor);
    }

    interface Presenter extends BasePresenter {

        void loadVideos();

        void playVideo(@NonNull Video video);

        void refreshData();
    }
}
