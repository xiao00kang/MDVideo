package com.studyjams.mdvideo.FileChooserModule;

import android.support.annotation.NonNull;

/**
 * Created by syamiadmin on 2016/8/11.
 */
public class FileChooserItem implements Comparable<FileChooserItem>{

    private String fileName;
    private String location;
    private boolean directory;
    private boolean marked;
    private boolean isVideo;
    private long time;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int compareTo(@NonNull FileChooserItem fileListItem) {
        if (fileListItem.isDirectory() && isDirectory()) {
            //If the comparison is between two directories, return the directory with
            //alphabetic order first.
            return fileName.toLowerCase().compareTo(fileListItem.getFileName().toLowerCase());
        } else if (!fileListItem.isDirectory() && !isDirectory()) {
            //If the comparison is not between two directories, return the file with
            //alphabetic order first.
            return fileName.toLowerCase().compareTo(fileListItem.getFileName().toLowerCase());
        } else if (fileListItem.isDirectory() && !isDirectory()) {
            //If the comparison is between a directory and a file, return the directory.
            return 1;
        } else {
            //Same as above but order of occurence is different.
            return -1;
        }
    }
}
