package com.studyjams.mdvideo.FileChooserModule;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by syamiadmin on 2016/8/11.
 */
public class FileChooserUtil {

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

    public static ArrayList<FileChooserItem> prepareFileListEntries(ArrayList<FileChooserItem> internalList, File inter, FileChooserFilter filter) {
        try {
            //Check for each and every directory/file in 'inter' directory.
            //Filter by extension using 'filter' reference.
            for (File name : inter.listFiles(filter)) {
                //If file/directory can be read by the Application
                if (name.canRead()) {
                    //Create a row item for the directory list and define properties.
                    FileChooserItem item = new FileChooserItem();
                    item.setFileName(name.getName());
                    item.setDirectory(name.isDirectory());
                    item.setLocation(name.getAbsolutePath());
                    item.setTime(name.lastModified());
                    //Add row to the List of directories/files
                    internalList.add(item);
                }
            }
            //Sort the files and directories in alphabetical order.
            //See compareTo method in FileListItem class.
            Collections.sort(internalList);
        } catch (NullPointerException e) {
            //Just don't worry, it rarely occurs.
            e.printStackTrace();
            internalList = new ArrayList<>();
        }
        return internalList;
    }
}
