package com.studyjams.mdvideo;

import android.app.Application;
import android.content.Context;

/**
 * Created by syamiadmin on 2016/6/12.
 */
public class MDVideoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
}
