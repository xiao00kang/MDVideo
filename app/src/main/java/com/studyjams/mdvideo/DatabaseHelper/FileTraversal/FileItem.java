package com.studyjams.mdvideo.DatabaseHelper.FileTraversal;

import android.support.annotation.NonNull;

/**
 * Created by syamiadmin on 2016/8/17.
 */
public class FileItem implements Comparable<FileItem>{
    private String name;
    private String path;
    private long date;
    private long size;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int compareTo(@NonNull FileItem fileItem) {
        return name.toLowerCase().compareTo(fileItem.getName().toLowerCase());
    }
}
