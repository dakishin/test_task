package test.upwork.timer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import test.upwork.timer.player.MusicBrowserClient;

/**
 * Created by dakishin@gmail.com
 */

/**
 * Class received broadcast from {@link test.upwork.timer.timer.Timer}
 */
public class FromTimeReceiver extends BroadcastReceiver {
    private static final String TAG = FromTimeReceiver.class.getName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "FromTimeReceiver");
        MusicBrowserClient.start(context);
    }
}
