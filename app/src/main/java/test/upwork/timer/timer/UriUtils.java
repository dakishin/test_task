package test.upwork.timer.timer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by dakishin@gmail.com
 */

public class UriUtils {

    /**
     * Получить имя ресурса по его uri
     */
    public static String extractFilename(Context context, Uri uri) {
        String name = extractFileNameFromMetaData(context, uri);
        if (name == null) {
            name = extractFileNameFromUriPath(uri);
        }
        return name == null ? "" : name;
    }

    /**
     * Получить размер файла ресурса в байтах.
     * Если размер получить не удалось возвращаем -1.
     */
    public static long extractFileSize(Context context, Uri uri) {
        long fileSize = UriUtils.extractFileSizeFromMetaData(context, uri);

        if (fileSize == -1) {
            return extractFileSizeFromPath(uri);
        }

        return fileSize;
    }


    @Nullable
    private static String extractFileNameFromUriPath(Uri uri) {
        try {
            String path = uri.getPath();
            if (path == null) {
                return null;
            }
            int index = path.lastIndexOf("/");
            if (index < 0) {
                return path;
            }
            return path.substring(index + 1);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * https://developer.android.com/intl/ru/guide/topics/providers/document-provider.html
     * Метод читающий мета данные документа из Google Drive
     */
    private static String extractFileNameFromMetaData(Context context, Uri uri) {
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * Излекаем размер файла  из провайдера. Верный пусть.
     */
    private static long extractFileSizeFromMetaData(Context context, Uri uri) {

        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return -1;
    }

    /**
     * Извлекаем имя файла из пути Uri. Костыль. Нужен для недобросовестных провайдеров. Например, dropbox
     */
    private static long extractFileSizeFromPath(Uri uri) {
        try {
            if ("file".equals(uri.getScheme())) {
                return new File(uri.getPath()).length();
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }
}
