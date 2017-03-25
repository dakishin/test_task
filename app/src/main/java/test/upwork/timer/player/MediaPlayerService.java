package test.upwork.timer.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import test.upwork.timer.PreferencesAdapter;
import test.upwork.timer.timer.TimerParameters;

/**
 * Created by dakishin@gmail.com
 */

/**
 * Service holds running Timer state.
 */
public class MediaPlayerService extends Service {

    private static final String TAG = MediaPlayerService.class.getName();
    private MediaPlayerAdapter mediaPlayer;
    private Timer timerForPauseAndContinuePlay;
    private long playInterval;
    private long pauseInterval;

    public static void start(Context context) {
        Intent intent = new Intent(context, MediaPlayerService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MediaPlayerService.class);
        context.stopService(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayerAdapter();
        mediaPlayer.init(getApplicationContext());
        timerForPauseAndContinuePlay = new Timer();
        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(getApplicationContext());
        playInterval = TimeUnit.MINUTES.toMillis(timerParameters.playIntervalInMinutes);
        pauseInterval = TimeUnit.MINUTES.toMillis(timerParameters.pauseIntervalInMinutes);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerForPauseAndContinuePlay.schedule(createPauseTask(), playInterval);
        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private TimerTask createPauseTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "pause task");
                mediaPlayer.pause();
                timerForPauseAndContinuePlay.schedule(createContinueTask(), pauseInterval);
            }
        };
    }

    private TimerTask createContinueTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "continue task");
                mediaPlayer.start();
                timerForPauseAndContinuePlay.schedule(createPauseTask(), playInterval);
            }
        };
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "destroy");
        mediaPlayer.stop();
        timerForPauseAndContinuePlay.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
