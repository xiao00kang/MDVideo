package com.studyjams.mdvideo.PlayerModule.EventBusMessage;

/**
 * Created by syamiadmin on 2016/8/8.
 */
public class ControllerMessage {

    public static final int MENU = 1;
    public static final int SUBTITLE = 2;

    private int code;

    public ControllerMessage(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
