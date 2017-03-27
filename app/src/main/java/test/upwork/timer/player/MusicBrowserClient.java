package test.upwork.timer.player;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;

import java.util.List;
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
public class MusicBrowserClient extends Service {

    private static final String TAG = MusicBrowserClient.class.getName();
    private Timer timerForPauseAndContinuePlay;

    private long playInterval;
    private long pauseInterval;
    private MediaBrowserCompat mMediaBrowser;
    private String mMediaId;

    private MediaControllerCompat.TransportControls mTransportControls;

    private MediaControllerCompat.Callback mSessionCallback = new MediaControllerCompat.Callback() {

    };

    private MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            mTransportControls.play();
        }

    };


    private MediaBrowserCompat.ConnectionCallback mConnectionCallback =

        new MediaBrowserCompat.ConnectionCallback() {
            @Override
            public void onConnected() {
                Log.d(TAG, "onConnected");
                if (mMediaId == null) {
                    mMediaId = mMediaBrowser.getRoot();
                }

                mMediaBrowser.subscribe(mMediaId, mSubscriptionCallback);

                if (mMediaBrowser.getSessionToken() == null) {
                    throw new IllegalArgumentException("No Session token");
                }

                try {
                    MediaControllerCompat mediaController = new MediaControllerCompat(getApplicationContext(),
                        mMediaBrowser.getSessionToken());
                    mTransportControls = mediaController.getTransportControls();
                    mediaController.registerCallback(mSessionCallback);
                } catch (RemoteException e) {
                    Log.e(TAG, e.getMessage(), e);
                }


            }

            @Override
            public void onConnectionFailed() {
                Log.d(TAG, "onConnectionFailed");
            }

            @Override
            public void onConnectionSuspended() {
                Log.d(TAG, "onConnectionSuspended");
            }
        };


    public static void start(Context context) {
        Intent intent = new Intent(context, MusicBrowserClient.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MusicBrowserClient.class);
        context.stopService(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        mediaPlayer = new MediaPlayerAdapter();
//        mediaPlayer.init(getApplicationContext());
//
        timerForPauseAndContinuePlay = new Timer();

        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(getApplicationContext());
        playInterval = TimeUnit.MINUTES.toMillis(timerParameters.playIntervalInMinutes);
        pauseInterval = TimeUnit.MINUTES.toMillis(timerParameters.pauseIntervalInMinutes);

        mMediaBrowser = new MediaBrowserCompat(getApplicationContext(), new ComponentName(getApplicationContext(),
            MusicBrowserService.class), mConnectionCallback, null);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerForPauseAndContinuePlay.schedule(createPauseTask(), playInterval);
//        mediaPlayer.start();
        mMediaBrowser.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    private TimerTask createPauseTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "pause task");
                mTransportControls.pause();
//                mediaPlayer.pause();
                timerForPauseAndContinuePlay.schedule(createContinueTask(), pauseInterval);
            }
        };
    }

    private TimerTask createContinueTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "continue task");
                mTransportControls.play();
//                mediaPlayer.start();
                timerForPauseAndContinuePlay.schedule(createPauseTask(), playInterval);
            }
        };
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "destroy");
//        mediaPlayer.stop();
        mTransportControls.stop();
        timerForPauseAndContinuePlay.cancel();
        mMediaBrowser.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
