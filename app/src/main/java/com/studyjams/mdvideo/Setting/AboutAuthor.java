package com.studyjams.mdvideo.Setting;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.studyjams.mdvideo.R;

/**
 * Created by visn on 2016/12/20.
 */

public class AboutAuthor extends DialogPreference{

        public AboutAuthor(Context context, AttributeSet attrs) {
            super(context, attrs);
            setDialogLayoutResource(R.layout.setting_about_author);
            setPositiveButtonText(android.R.string.ok);
            setNegativeButtonText(android.R.string.cancel);
            setDialogIcon(null);
        }
}
