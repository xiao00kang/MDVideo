package com.studyjams.mdvideo.PlayerModule.MediaController;

import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by syamiadmin on 2016/8/6.
 */
public abstract class AbstractMediaController {
    abstract void setAnchorView(View rootView);
    abstract void setMediaPlayer(SimpleExoPlayer player);
    abstract void setEnabled(boolean enabled);
    abstract boolean isShowing();
    abstract void hide();
    abstract void show(int timeout);
    abstract void setTitle(String videoName);
}
