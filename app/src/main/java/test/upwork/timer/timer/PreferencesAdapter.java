package test.upwork.timer.timer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by dakishin@gmail.com
 */
public class PreferencesAdapter {

    private static final String SETTINGS = "SETTINGS";
    private static final String PREFS = "TIMER_PREFS";


    public static TimerParameters getTimerParameters(Context context) {
        String string = getSharedPreferences(context).getString(SETTINGS, null);
        if (StringUtils.isEmpty(string)) {
            return new TimerParameters();
        }
        return new Gson().fromJson(string, TimerParameters.class);
    }


    public static void saveTimerParameters(Context context, TimerParameters timerParameters) {
        if (timerParameters == null) {
            return;
        }
        getSharedPreferences(context).edit().putString(SETTINGS, new Gson().toJson(timerParameters)).apply();
    }


    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }


}
