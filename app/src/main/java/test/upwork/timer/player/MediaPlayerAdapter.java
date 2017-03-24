package test.upwork.timer.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;

import test.upwork.timer.FileHelper;

/**
 * Created by dakishin@gmail.com
 */

public class MediaPlayerAdapter {
    private MediaPlayer mediaPlayer;
    private static final String TAG = MediaPlayerAdapter.class.getName();

    public void init(Context context) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            FileInputStream fileInputStream = FileHelper.getFileInputStream(context);
            if (fileInputStream == null) {
                return;
            }
            FileDescriptor fd = fileInputStream.getFD();
            mediaPlayer.setDataSource(fd);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public void stop() {
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

    public void pause() {
        mediaPlayer.pause();

    }
}
