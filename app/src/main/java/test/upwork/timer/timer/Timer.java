package test.upwork.timer.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
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


        Calendar fromTime = Calendar.getInstance();
        fromTime.set(Calendar.HOUR_OF_DAY, timerParameters.fromHour);
        fromTime.set(Calendar.MINUTE, timerParameters.fromMinute);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            fromTime.getTimeInMillis(),
            getPeriod(timerParameters),
            getFromTimeIntent(context));


        Calendar toTime = Calendar.getInstance();
        toTime.set(Calendar.HOUR_OF_DAY, timerParameters.toHour);
        toTime.set(Calendar.MINUTE, timerParameters.toMinute);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            toTime.getTimeInMillis(),
            getPeriod(timerParameters),
            getToTimeIntent(context));


        timerParameters.isRunning = true;
        PreferencesAdapter.saveTimerParameters(context, timerParameters);

    }

    public static void stopAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(getFromTimeIntent(context));
            alarmManager.cancel(getToTimeIntent(context));
        }
        MediaPlayerService.stop(context);
        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(context);
        timerParameters.isRunning = false;
        PreferencesAdapter.saveTimerParameters(context, timerParameters);
    }


    private static long getPeriod(TimerParameters timerParameters) {
        switch (timerParameters.playInterval) {
            case 0:
                return TimeUnit.HOURS.toMillis(24);
            case 1:
                return TimeUnit.DAYS.toMillis(7);
            default:
                return TimeUnit.DAYS.toMillis(31);
        }

    }


    private static PendingIntent getFromTimeIntent(Context context) {
        Intent intent = new Intent(context, FromTimeReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private static PendingIntent getToTimeIntent(Context context) {
        Intent intent = new Intent(context, ToTimeReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
