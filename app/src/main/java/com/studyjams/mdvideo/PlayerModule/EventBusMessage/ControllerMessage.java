package com.studyjams.mdvideo.PlayerModule.EventBusMessage;

import android.view.View;

/**
 * Created by syamiadmin on 2016/8/8.
 */
public class ControllerMessage {

    public static final int CODE = 1;

    private View view;

    public ControllerMessage(View view){
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
