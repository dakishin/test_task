package test.upwork.timer.util;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import test.upwork.timer.PreferencesAdapter;

/**
 * Created by dakishin@gmail.com
 */

/**
 * Service convert wma file to mp3.
 * Put mp3 wmpFile to WmaTimer external folder
 */
public class PrepareMusicFilesService extends Service {

    public static final String PREPARED_ACTION = "test_timer_PREPARED_ACTION";
    public static final String PREPARE_STATUS = "PREPARE_STATUS";
    public static final String PREPARED_FILE_NAME = "PREPARED_FILE_NAME";
    private final Map<String, File> filesToConvert = new HashMap<>();
    private Status status = Status.IN_PROGRESS;

    public enum Status {
        IN_PROGRESS,
        COMPLETED
    }

    private static final String TAG = PrepareMusicFilesService.class.getName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        filesToConvert.clear();
        searchFilesTask.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendStatus(null);
        return super.onStartCommand(intent, flags, startId);
    }

    private AsyncTask<Void, Void, Collection<File>> searchFilesTask = new AsyncTask<Void, Void, Collection<File>>() {

        @Override
        protected Collection<File> doInBackground(Void... params) {
            WmaFileSearcher wmaFileSearcher = new WmaFileSearcher();
            wmaFileSearcher.search();
            return wmaFileSearcher.getFiles();
        }

        @Override
        protected void onPostExecute(Collection<File> files) {
            for (File file : files) {
                filesToConvert.put(file.getName(), file);
            }
            convert(files);
        }
    };

    public void convert(Collection<File> wmaFiles) {
        for (final File wmaFile : wmaFiles) {
            final File timerFolder = new File(Environment.getExternalStorageDirectory(), "WmaTimer");
            if (!timerFolder.exists()) {
                timerFolder.mkdirs();
            }
            String fileName = wmaFile.getName().replace("wma", "mp3");
            final File file = new File(timerFolder.getAbsolutePath() + "/" + fileName);
            if (file.exists()) {
//              file already converted
                List<String> musicFiles = PreferencesAdapter.getMusicFiles(getApplicationContext());
                if (!musicFiles.contains(file.getAbsolutePath())) {
                    musicFiles.add(file.getAbsolutePath());
                    PreferencesAdapter.saveMusicFiles(getApplicationContext(), musicFiles);
                }
                sendFileStatus(wmaFile);
                continue;
            }


            sendStatus(file);

            AndroidAudioConverter.with(getApplicationContext())
                .setFile(wmaFile)
                .setFormat(AudioFormat.MP3)
                .setCallback(new IConvertCallback() {
                    @Override
                    public void onSuccess(File mp3File) {
//                      move file to WmaTimer folder
                        File to = new File(timerFolder.getAbsolutePath() + "/" + Utils.getFileName(mp3File));
                        mp3File.renameTo(to);

                        List<String> musicFiles = PreferencesAdapter.getMusicFiles(getApplicationContext());

                        if (!musicFiles.contains(to.getAbsolutePath())) {
                            musicFiles.add(to.getAbsolutePath());
                            PreferencesAdapter.saveMusicFiles(getApplicationContext(), musicFiles);
                        }
                        sendFileStatus(wmaFile);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                        sendFileStatus(wmaFile);
                    }
                })
                .convert();
        }
    }

    private void sendFileStatus(File wmaFile) {
        filesToConvert.remove(wmaFile.getName());
        if (filesToConvert.isEmpty()) {
            status = Status.COMPLETED;
        }
        sendStatus(wmaFile);
    }

    private void sendStatus(File file) {
        Intent intent = new Intent();
        intent.setAction(PREPARED_ACTION);
        if (file != null) {
            intent.putExtra(PREPARED_FILE_NAME, file.getName());
        }
        intent.putExtra(PREPARE_STATUS, status);
        sendBroadcast(intent);

        if (status == Status.COMPLETED) {
            stopSelf();
        }
    }


}