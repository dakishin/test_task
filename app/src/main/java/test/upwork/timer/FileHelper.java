package test.upwork.timer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dakishin@gmail.com
 */

public class FileHelper {
    private static final String TAG = FileHelper.class.getName();
    public static final String PLAYFILE = "playfile";

    public static void saveFile(Context context, Uri fileUri) {
        try {
            OutputStream os = context.openFileOutput(PLAYFILE, MODE_PRIVATE);
            IOUtils.copy(context.getContentResolver().openInputStream(fileUri), os);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static FileInputStream getFileInputStream(Context context) {
        try {
            return context.openFileInput(PLAYFILE);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }
}
