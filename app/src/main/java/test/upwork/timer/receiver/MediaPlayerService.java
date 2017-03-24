package test.upwork.timer.receiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by dakishin@gmail.com
 */

public class MediaPlayerService extends Service {
    private static final String TAG = MediaPlayerService.class.getName();
    public static final String DO_START = "DO_START";
    private MediaPlayerAdapter mediaPlayer;


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

    public static void pause(Context context) {
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.putExtra(DO_START, false);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayerAdapter();
        mediaPlayer.init(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isStart = intent.getBooleanExtra(DO_START, false);
        if (isStart) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "destroy");
        mediaPlayer.stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
