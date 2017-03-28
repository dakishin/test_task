package test.upwork.timer.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import test.upwork.timer.PreferencesAdapter;

/**
 * Created by dakishin@gmail.com
 */

/**
 * Adapter plays list of files using MediaPlayer
 */
class MediaPlayerAdapter {
    private MediaPlayer mediaPlayer;
    private static final String TAG = MediaPlayerAdapter.class.getName();
    private List<String> filePaths;
    private int currentIndex = 0;

    void init(Context context) {
        this.filePaths = PreferencesAdapter.getMusicFiles(context);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePaths.get(0));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentIndex++;
                    if (currentIndex >= filePaths.size()) {
                        currentIndex = 0;
                    }
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(filePaths.get(currentIndex));
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    start();
                    Log.e(TAG, "started new melody");
                }
            });

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    void start() {
        mediaPlayer.start();
    }

    void stop() {
        if (mediaPlayer == null) {
            return;
        }
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    void pause() {

        try {
            if (mediaPlayer == null) {
                return;
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }


    }
}
