package test.upwork.timer.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import test.upwork.timer.PreferencesAdapter;
import test.upwork.timer.player.MediaPlayerService;
import test.upwork.timer.receiver.FromTimeReceiver;
import test.upwork.timer.receiver.ToTimeReceiver;

/**
 * Created by dakishin@gmail.com
 */

public class Timer {

    public static void startAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(context);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            timerParameters.getFromCalendar().getTimeInMillis(),
            getPeriod(timerParameters),
            getPendingIntent(context, FromTimeReceiver.class, 0));

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            timerParameters.getToCalendar().getTimeInMillis(),
            getPeriod(timerParameters),
            getPendingIntent(context, ToTimeReceiver.class, 1));


        timerParameters.isRunning = true;
        PreferencesAdapter.saveTimerParameters(context, timerParameters);

    }

    public static void stopAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(getPendingIntent(context, FromTimeReceiver.class, 0));
            alarmManager.cancel(getPendingIntent(context, ToTimeReceiver.class, 1));
        }
        MediaPlayerService.stop(context);
        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(context);
        timerParameters.isRunning = false;
        PreferencesAdapter.saveTimerParameters(context, timerParameters);
    }


    private static long getPeriod(TimerParameters timerParameters) {
        switch (timerParameters.playIntervalMillis) {
            case 0:
                return TimeUnit.HOURS.toMillis(24);
            case 1:
                return TimeUnit.DAYS.toMillis(7);
            default:
                return TimeUnit.DAYS.toMillis(31);
        }

    }


    private static PendingIntent getPendingIntent(Context context, Class receiver, int requestCode) {
        Intent intent = new Intent(context, receiver);
        return PendingIntent.getBroadcast(context, requestCode, intent, 0);
    }


}
