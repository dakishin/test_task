package test.upwork.timer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by dakishin@gmail.com
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "alarm received");
    }
}
