package test.upwork.timer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dakishin@gmail.com
 */

public class FileFinder {
    private List<String> filePaths = new ArrayList<>();
    private static final int MAX_RECURSIVE_LEVEL = 3;

    public void search(File root) {
        searchRecursive(root.listFiles(), 0);
    }

    private void searchRecursive(File[] subFiles, int level) {
        if (subFiles == null || level > MAX_RECURSIVE_LEVEL) {
            return;
        }
        for (File file : subFiles) {
            if (file.isDirectory()) {
                searchRecursive(file.listFiles(), level + 1);
            } else {
                String path = file.getAbsolutePath().toLowerCase();
                if (path.endsWith(".mp3")) {
                    filePaths.add(file.getAbsolutePath());
                }
            }
        }
    }


    public List<String> getFilePaths() {
        return filePaths;
    }
}
