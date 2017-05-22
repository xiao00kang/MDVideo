package com.studyjams.mdvideo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.studyjams.mdvideo.MainFrame.MainActivity;

/**
 * Created by syamiadmin on 2016/6/12.
 *
 String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
 String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
 String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
 String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
 String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
 String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
 long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
 long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
 */
public class MDVideoApplication extends Application implements Application.ActivityLifecycleCallbacks{

    private static final String TAG = "MDVideoApplication";
    private VideoChangeObserver mObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        mObserver = new VideoChangeObserver(new Handler());
        this.registerActivityLifecycleCallbacks(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity.getClass() == MainActivity.class){
          getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,true, mObserver);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity.getClass() == MainActivity.class){
            getContentResolver().unregisterContentObserver(mObserver);
        }
    }

    private class VideoChangeObserver extends ContentObserver {

        private VideoChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG, "=========onChange: ");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);

            Log.d(TAG, "==========onChange: " + uri);
        }
    }
}
