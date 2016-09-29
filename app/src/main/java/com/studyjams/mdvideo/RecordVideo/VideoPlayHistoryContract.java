package com.studyjams.mdvideo.RecordVideo;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.studyjams.mdvideo.BasePresenter;
import com.studyjams.mdvideo.BaseView;
import com.studyjams.mdvideo.Data.Video;

/**
 * Created by syamiadmin on 2016/9/23.
 */

public interface VideoPlayHistoryContract {

    interface View extends BaseView<VideoPlayHistoryContract.Presenter> {
        void showNoVideos();
        void showVideos(Cursor cursor);
    }

    interface Presenter extends BasePresenter {

        void playVideo(@NonNull Video video);

        void refreshData();
    }
}
