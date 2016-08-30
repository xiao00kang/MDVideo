package com.studyjams.mdvideo.Util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.studyjams.mdvideo.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by syamiadmin on 2016/6/8.
 */
public class Tools {

    /**图片加载**/
    public static void LoadNormalImage(Context context, String url, ImageView imageView){
        Glide.with(context).load(url)
                .placeholder(R.mipmap.empty_photo)
                .crossFade()
                .into(imageView);
    }

    /**获取当前本地系统时间的**/
    public static String getCurrentTimeMillis() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    /**API 23以上权限判定**/
    public static boolean checkStorageAccessPermissions(Context context) {
        //Only for Android M and above.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String permission = "android.permission.READ_EXTERNAL_STORAGE";
            int res = context.checkCallingOrSelfPermission(permission);
            return (res == PackageManager.PERMISSION_GRANTED);
        } else {
            //Pre Marshmallow can rely on Manifest defined permissions.
            return true;
        }
    }

    /**判断SD卡是否存在**/
    public static boolean checkSDCardExists(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**合并两个数组**/
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
