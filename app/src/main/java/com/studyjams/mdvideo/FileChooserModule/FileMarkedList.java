package com.studyjams.mdvideo.FileChooserModule;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by syamiadmin on 2016/8/11.
 */
public class FileMarkedList {
    private static HashMap<String, FileChooserItem> ourInstance = new HashMap<>();

    public static void addSelectedItem(FileChooserItem item) {
        ourInstance.put(item.getLocation(), item);
    }

    public static void removeSelectedItem(String key) {
        ourInstance.remove(key);
    }

    public static boolean hasItem(String key) {
        return ourInstance.containsKey(key);
    }

    public static void clearSelectionList() {
        ourInstance.clear();
    }

    public static FileChooserItem[] addSingleFile(FileChooserItem item) {
        FileChooserItem[] oldfiles = new FileChooserItem[ourInstance.size()];
        int i = 0;
        for (String key : ourInstance.keySet()) {
            oldfiles[i] = ourInstance.get(key);
        }
        ourInstance.clear();
        ourInstance.put(item.getLocation(), item);
        return oldfiles;
    }

    public static String[] getSelectedPaths() {
        Set<String> paths = ourInstance.keySet();
        String fpaths[] = new String[paths.size()];
        int i = 0;
        for (String path : paths) {
            fpaths[i++] = path;
        }
        return fpaths;
    }

    public static int getFileCount() {
        return ourInstance.size();
    }

    private FileMarkedList() {
    }
}
