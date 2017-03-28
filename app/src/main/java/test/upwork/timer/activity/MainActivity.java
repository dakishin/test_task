package test.upwork.timer.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Formatter;

import test.upwork.timer.PreferencesAdapter;
import test.upwork.timer.R;
import test.upwork.timer.player.MediaPlayerService;
import test.upwork.timer.timer.Timer;
import test.upwork.timer.timer.TimerParameters;
import test.upwork.timer.util.PrepareMusicFilesService;


public class MainActivity extends AppCompatActivity {


    private static final int READ_EXTERNAL_STORAGE_CODE = 124;
    private static final String TAG = MainActivity.class.getName();
    public static final String HOURS_PARAM = "hours";
    public static final String MINUTES_PARAM = "minutes";
    public static final String LISTENER_PARAM = "listener";
    private TextView fromTimeTextView;
    private TextView toTimeTextView;
    private TimerParameters timerParameters;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromTimeTextView = (TextView) findViewById(R.id.fromTimeTextView);
        toTimeTextView = (TextView) findViewById(R.id.toTimeTextView);

        timerParameters = PreferencesAdapter.getTimerParameters(getApplicationContext());
        initDefaultValues(timerParameters);

        initRepeat();
        initFromToTime();
        initPlayInterval();
        initPauseInterval();
        initStartTimerButton();


        checkPermissionAndPrepareWma();
    }

    private void showProgress() {
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.main_dialog_wait));
        progress.show();
    }


    private PrepareMusicServiceStatusReceiver prepareStatusReceiver = new PrepareMusicServiceStatusReceiver();


    private class PrepareMusicServiceStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PrepareMusicFilesService.Status status = (PrepareMusicFilesService.Status) intent.getSerializableExtra(PrepareMusicFilesService.PREPARE_STATUS);
            Log.e(TAG, "status " + status);
            if (status == PrepareMusicFilesService.Status.COMPLETED && progress != null) {
                progress.dismiss();
            }

        }

    }


    private void initStartTimerButton() {
        final Switch startTimerSwitch = (Switch) findViewById(R.id.startTimerSwitch);
        startTimerSwitch.setChecked(timerParameters.isRunning);
        startTimerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && PreferencesAdapter.getMusicFiles(getApplicationContext()).isEmpty()) {
                    checkPermissionAndPrepareWma();
                    startTimerSwitch.setChecked(false);
                    return;
                }


                timerParameters.isRunning = isChecked;
                if (isChecked) {
//                    Timer.start(getApplicationContext());
                    MediaPlayerService.start(getApplicationContext());
                } else {
//                    Timer.stop(getApplicationContext());
                    MediaPlayerService.stop(getApplicationContext());
                }
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            }
        });

    }

    private void initPlayInterval() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.interval_minutes, R.layout.spiner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner playIntervalSpinner = (Spinner) findViewById(R.id.play_interval_spinner);
        playIntervalSpinner.setAdapter(adapter);

        playIntervalSpinner.setSelection(timerParameters.playIntervalInMinutes - 1);

        playIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (timerParameters.playIntervalInMinutes == position + 1) {
                    return;
                }
                timerParameters.playIntervalInMinutes = position + 1;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
                restartTimerIfNeeded();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initPauseInterval() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.interval_minutes, R.layout.spiner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner pauseIntervalSpinner = (Spinner) findViewById(R.id.pause_onterval_spinner);


        pauseIntervalSpinner.setAdapter(adapter);
        pauseIntervalSpinner.setSelection(timerParameters.pauseIntervalInMinutes - 1);

        pauseIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (timerParameters.pauseIntervalInMinutes == position + 1) {
                    return;
                }
                timerParameters.pauseIntervalInMinutes = position + 1;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
                restartTimerIfNeeded();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private class FromTimeListener implements TimePickerDialog.OnTimeSetListener, Serializable {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timerParameters.fromHour = hourOfDay;
            timerParameters.fromMinute = minute;
            initTimeView(fromTimeTextView, hourOfDay, minute);

            if (timerParameters.getFromCalendar().after(timerParameters.getToCalendar())) {
                timerParameters.toHour = hourOfDay;
                timerParameters.fromHour = hourOfDay;
                initTimeView(toTimeTextView, hourOfDay, minute);
            }

            PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            restartTimerIfNeeded();
        }
    }


    private class ToTimeListener implements TimePickerDialog.OnTimeSetListener, Serializable {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timerParameters.toHour = hourOfDay;
            timerParameters.toMinute = minute;
            if (timerParameters.getFromCalendar().after(timerParameters.getToCalendar())) {
                timerParameters.toHour = timerParameters.fromHour;
                timerParameters.toMinute = timerParameters.fromMinute;
            }
            initTimeView(toTimeTextView, timerParameters.toHour, timerParameters.toMinute);
            PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            restartTimerIfNeeded();
        }
    }


    private FromTimeListener fromTimeListener = new FromTimeListener();
    private ToTimeListener toTimeListener = new ToTimeListener();


    public static class TimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour = getArguments().getInt(HOURS_PARAM);
            int minute = getArguments().getInt(MINUTES_PARAM);
            TimePickerDialog.OnTimeSetListener listener = (TimePickerDialog.OnTimeSetListener) getArguments().getSerializable(LISTENER_PARAM);
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), listener, hour, minute, true);
            return timePickerDialog;
        }
    }

    public void initTimeView(TextView textView, int hours, int minutes) {
        textView.setText(new Formatter().format("%02d:%02d", hours, minutes).toString());
    }

    private void initFromToTime() {
        initTimeView(fromTimeTextView, timerParameters.fromHour, timerParameters.fromMinute);
        initTimeView(toTimeTextView, timerParameters.toHour, timerParameters.toMinute);

        fromTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(timerParameters.fromHour, timerParameters.fromMinute, fromTimeListener);
            }
        });

        toTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(timerParameters.toHour, timerParameters.toMinute, toTimeListener);
            }
        });


    }

    private void showTimePicker(int hour, int minute, Serializable listener) {
        DialogFragment newFragment = new TimePickerFragment();
        Bundle param = new Bundle();
        param.putInt(HOURS_PARAM, hour);
        param.putInt(MINUTES_PARAM, minute);
        param.putSerializable(LISTENER_PARAM, listener);
        newFragment.setArguments(param);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }


    private void initRepeat() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.repeat_items, R.layout.spiner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner repeatSpinner = (Spinner) findViewById(R.id.repeat_spinner);
        repeatSpinner.setAdapter(adapter);
        repeatSpinner.setSelection(timerParameters.repeatInterval);

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == timerParameters.repeatInterval) {
                    return;
                }
                timerParameters.repeatInterval = position;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
                restartTimerIfNeeded();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private boolean hasRunTimePermission(String permission) {
        boolean permissionRequired = ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;

        if (!permissionRequired) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, READ_EXTERNAL_STORAGE_CODE);
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionAndPrepareWma();
                    return;
                }

            }
        }
    }

    private void checkPermissionAndPrepareWma() {
        if (!hasRunTimePermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }

        if (!hasRunTimePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }

        if (PreferencesAdapter.getMusicFiles(getApplicationContext()).isEmpty()) {
//          there is no melodies yet. show progress
            showProgress();
        }
        startService(new Intent(getApplicationContext(), PrepareMusicFilesService.class));

    }


    private void restartTimerIfNeeded() {
        if (timerParameters.isRunning) {
            Timer.stop(getApplicationContext());
            Timer.start(getApplicationContext());
        }
    }


    private void initDefaultValues(TimerParameters timerParameters) {
        Calendar now = Calendar.getInstance();

        if (timerParameters.fromHour == null) {
            timerParameters.fromHour = now.get(Calendar.HOUR_OF_DAY);
        }

        if (timerParameters.fromMinute == null) {
            timerParameters.fromMinute = now.get(Calendar.MINUTE);
        }

        now.add(Calendar.MINUTE, 2);

        if (timerParameters.toHour == null) {
            timerParameters.toHour = now.get(Calendar.HOUR_OF_DAY);
        }

        if (timerParameters.toMinute == null) {
            timerParameters.toMinute = now.get(Calendar.MINUTE);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(prepareStatusReceiver, new IntentFilter(PrepareMusicFilesService.PREPARED_ACTION));
    }

    @Override
    protected void onStop() {
        unregisterReceiver(prepareStatusReceiver);
        super.onStop();
    }

}
