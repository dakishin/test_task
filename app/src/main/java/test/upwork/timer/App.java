package test.upwork.timer;

import android.app.Application;
import android.util.Log;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

/**
 * Created by dakishin@gmail.com
 */

public class App extends Application {
    private static final String TAG = App.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }

            @Override
            public void onFailure(Exception error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });
    }
}
