package com.studyjams.mdvideo.FileChooserModule;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by syamiadmin on 2016/8/11.
 */
public class FileChooserFilter implements FileFilter {
    private final String[] validExtensions;
    private FileChooserProperties properties;

    public FileChooserFilter(FileChooserProperties properties) {
        if (properties.extensions != null) {
            this.validExtensions = properties.extensions;
        } else {
            this.validExtensions = new String[]{""};
        }
        this.properties = properties;
    }

    //Function to filter files based on defined rules
    @Override
    public boolean accept(File file) {
        //All directories are added in the least that can be read by the Application
        if (file.isDirectory() && file.canRead()) {
            return true;
        } else if (properties.selection_type == FileChooserProperties.DIR_SELECT) {
            /*  True for files, If the selection type is Directory type, ie.
             *  Only directory has to be selected from the list, then all files are
             *  ignored.
             */
            return false;
        } else {
            /*  Check whether name of the file ends with the extension. Added if it
             *  does.
             */
            String name = file.getName().toLowerCase();
            for (String ext : validExtensions) {
                if (name.endsWith(ext)) {
                    return true;
                }
            }
        }
        return false;
    }
}
