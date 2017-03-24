package test.upwork.timer.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dakishin@gmail.com
 */

public class MediaPlayerService extends Service {

    private static final String TAG = MediaPlayerService.class.getName();
    public static final String DO_START = "DO_START";
    private MediaPlayerAdapter mediaPlayer;
    private Timer timerForPauseAndContinuePlay;

    public static void start(Context context) {
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.putExtra(DO_START, true);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.putExtra(DO_START, false);
        context.stopService(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayerAdapter();
        mediaPlayer.init(getApplicationContext());
        timerForPauseAndContinuePlay = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerForPauseAndContinuePlay.schedule(createPauseTask(), 5000);
        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private TimerTask createPauseTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "pause task");
                mediaPlayer.pause();
                timerForPauseAndContinuePlay.schedule(createContinueTask(), 5000);
            }
        };
    }

    private TimerTask createContinueTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "continue task");
                mediaPlayer.start();
                timerForPauseAndContinuePlay.schedule(createPauseTask(), 5000);
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
