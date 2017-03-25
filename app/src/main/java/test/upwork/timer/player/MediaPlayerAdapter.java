package test.upwork.timer.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import test.upwork.timer.PreferencesAdapter;
import test.upwork.timer.timer.TimerParameters;

/**
 * Created by dakishin@gmail.com
 */

/**
 * Class handles {@link MediaPlayer} logic.
 */
class MediaPlayerAdapter {
    private MediaPlayer mediaPlayer;
    private static final String TAG = MediaPlayerAdapter.class.getName();

    public void init(Context context) {
        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(context);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(timerParameters.soundFilePath);
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
