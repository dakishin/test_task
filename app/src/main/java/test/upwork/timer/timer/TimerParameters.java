package test.upwork.timer.timer;

import java.util.Calendar;

/**
 * Created by dakishin@gmail.com
 */

public class TimerParameters {
    public boolean isRunning;

    public int repeatInterval = 0;
    public int fromHour = 0;
    public int fromMinute = 0;

    public int toHour = 0;
    public int toMinute = 0;

    public int playInterval = 0;
    public int pauseInterval = 0;
    public String soundFileName;


    public Calendar getFrom() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, fromHour);
        calendar.set(Calendar.MINUTE, fromMinute);
        return calendar;
    }

    public Calendar getTo() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, toHour);
        calendar.set(Calendar.MINUTE, toMinute);
        return calendar;
    }
}
