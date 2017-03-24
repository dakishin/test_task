package test.upwork.timer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import test.upwork.timer.player.MediaPlayerService;

/**
 * Created by dakishin@gmail.com
 */

public class FromTimeReceiver extends BroadcastReceiver {
    private static final String TAG = FromTimeReceiver.class.getName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "FromTimeReceiver");
        MediaPlayerService.start(context);
    }
}
