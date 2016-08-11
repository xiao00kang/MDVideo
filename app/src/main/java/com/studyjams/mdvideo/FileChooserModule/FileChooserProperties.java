package com.studyjams.mdvideo.FileChooserModule;

import java.io.File;

/**
 * Created by syamiadmin on 2016/8/11.
 */
public class FileChooserProperties {

    public static final int SINGLE_MODE = 0;
    public static final int MULTI_MODE = 1;

    public static final int FILE_SELECT = 0;
    public static final int DIR_SELECT = 1;
    public static final int FILE_AND_DIR_SELECT = 2;

    /*  PARENT_DIRECTORY*/
    public static final String DIRECTORY_SEPERATOR = "/";
    public static final String STORAGE_DIR = "sdcard";

    public static final String DEFAULT_DIR = DIRECTORY_SEPERATOR + STORAGE_DIR;

    public int selection_mode;
    public int selection_type;
    public File root;
    public String[] extensions;

    public FileChooserProperties() {
        selection_mode = SINGLE_MODE;
        selection_type = FILE_SELECT;
        root = new File(DEFAULT_DIR);
        extensions = null;
    }
}
