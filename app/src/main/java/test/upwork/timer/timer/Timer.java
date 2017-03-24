package test.upwork.timer.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import test.upwork.timer.PreferencesAdapter;
import test.upwork.timer.receiver.AlarmReceiver;

/**
 * Created by dakishin@gmail.com
 */

public class Timer {

    public static void startAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getAlarmIntent(context);


        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(context);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timerParameters.fromHour);
        calendar.set(Calendar.MINUTE, timerParameters.fromMinute);


        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            getPeriod(timerParameters),
//            3000,
            alarmIntent);

        timerParameters.isRunning = true;
        PreferencesAdapter.saveTimerParameters(context, timerParameters);

    }

    public static void stopAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) {
            alarmMgr.cancel(getAlarmIntent(context));
        }
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


    private static PendingIntent getAlarmIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        return alarmIntent;
    }
}
