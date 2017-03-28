package test.upwork.timer.util;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by dakishin@gmail.com
 */

class WmaFileSearcher {
    /**
     * TODO:
     * Resolve naming problem
     * /sdcard/Download/BachCPE_SonataAmin_1.wma
     * /storage/emulated/0/Download/BachCPE_SonataAmin_1.wma
     *
     * Canonical path could be used here
     *
     */
    private HashMap<String, File> files = new HashMap<>();

    /**
     * Set up the following limitations to reduce search and conversion to mp3 time
     */
    private static final int MAX_FILES = 2;
    private static final int MAX_RECURSIVE_LEVEL = 3;

    void search() {
        searchRecursive(new File("/").listFiles(), 0);
    }

    private void searchRecursive(File[] subFiles, int level) {
        if (subFiles == null || level > MAX_RECURSIVE_LEVEL || files.size() > MAX_FILES) {
            return;
        }
        for (File file : subFiles) {
            if (file.isDirectory()) {
                searchRecursive(file.listFiles(), level + 1);
            } else {
                String path = file.getAbsolutePath().toLowerCase();
                if (path.endsWith(".wma") && !files.containsKey(file.getName()) && files.size() < MAX_FILES) {
                    files.put(file.getName(), file);
                }
            }
        }
    }


    Collection<File> getFiles() {
        return files.values();
    }
}
