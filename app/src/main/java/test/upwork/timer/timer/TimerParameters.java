package test.upwork.timer.timer;

import java.util.Calendar;

/**
 * Created by dakishin@gmail.com
 */

public class TimerParameters {
    public boolean isRunning;

    public int repeatInterval = 0;
    public Integer fromHour;
    public Integer fromMinute;

    public Integer toHour = 0;
    public Integer toMinute = 0;

    public int playIntervalInMinutes = 1;
    public int pauseIntervalInMinutes = 1;
    public String soundFileName;


    public Calendar getFromCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, fromHour);
        calendar.set(Calendar.MINUTE, fromMinute);
        return calendar;
    }

    public Calendar getToCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, toHour);
        calendar.set(Calendar.MINUTE, toMinute);
        return calendar;
    }
}
