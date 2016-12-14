package com.studyjams.mdvideo.LocalVideo;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.studyjams.mdvideo.BasePresenter;
import com.studyjams.mdvideo.BaseView;
import com.studyjams.mdvideo.Data.bean.Video;

/**
 * Created by syamiadmin on 2016/9/2.
 */
public interface LocalVideoContract {

    interface View extends BaseView<Presenter> {
        void showNoVideos();
        void showVideos(Cursor cursor);
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadVideos();

        void playVideo(@NonNull Video video);

        void refreshData();
    }
}
