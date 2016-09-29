package com.studyjams.mdvideo.Data.source.remote;

import com.studyjams.mdvideo.Util.Tools;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by syamiadmin on 2016/8/17.
 */
public class VideoFileFilter implements FileFilter {

    private final String[] filter;

    //FMP4, MP4, M4A, MKV, WebM, MP3, AAC, MPEG-TS, MPEG-PS, OGG, FLV, WAV
    //*.avi    *.rmvb    *.rm    *.asf    *.divx    *.mpg    *.mpeg *.mpe    *.wmv    *.mp4    *.mkv    *.vob

    public static final String[] VIDEO_FILTER = new String[]{"avi","asf","mpg","wmv","mp4","mkv","vob","flv","f4v"};
    public static final String[] SUBTITLE_FILTER = new String[]{"srt"};

    public VideoFileFilter(String ...filter){
        if(filter.length == 0){
            this.filter = Tools.concat(VIDEO_FILTER,SUBTITLE_FILTER);
        }else{
            this.filter = filter;
        }
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory() && file.canRead()) {
            return true;
        } else {
            /** Check whether name of the file ends with the extension. Added if it does.*/
            String name = file.getName().toLowerCase();
            for (String ext : filter) {
                if (name.endsWith(ext)) {
                    return true;
                }
            }
        }
        return false;
    }
}
