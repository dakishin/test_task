package test.upwork.timer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import test.upwork.timer.timer.TimerParameters;

/**
 * Created by dakishin@gmail.com
 */
public class PreferencesAdapter {

    private static final String SETTINGS = "SETTINGS";
    private static final String PREFS = "TIMER_PREFS";
    private static final String MUSIC_FILES = "MUSIC_FILES";


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

    public static void saveMusicFiles(Context context, List<String> musicFiles) {
        if (musicFiles == null) {
            return;
        }
        getSharedPreferences(context).edit().putString(MUSIC_FILES, new Gson().toJson(musicFiles)).apply();
    }

    public static List<String> getMusicFiles(Context context) {
        String string = getSharedPreferences(context).getString(MUSIC_FILES, null);
        if (StringUtils.isEmpty(string)) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(string, new TypeToken<List<String>>() {
        }.getType());
    }


    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }


}
