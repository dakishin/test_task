package test.upwork.timer.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Formatter;

import test.upwork.timer.FileHelper;
import test.upwork.timer.PreferencesAdapter;
import test.upwork.timer.R;
import test.upwork.timer.player.MediaPlayerService;
import test.upwork.timer.timer.TimerParameters;
import test.upwork.timer.timer.UriUtils;


public class MainActivity extends AppCompatActivity {


    private static final int CHOOSE_FILE_RESULT_CODE = 123;
    private static final int READ_EXTERNAL_STORAGE_CODE = 124;
    private static final String TAG = MainActivity.class.getName();
    private Uri chosenUri;
    TextView fromTimeTextView;
    TextView toTimeTextView;
    private TimerParameters timerParameters;


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
    }



    private void initStartTimerButton() {
        final Switch startTimerSwitch = (Switch) findViewById(R.id.startTimerSwitch);
        startTimerSwitch.setChecked(timerParameters.isRunning);
        startTimerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && StringUtils.isEmpty(timerParameters.soundFileName)) {
                    Toast.makeText(MainActivity.this, "Choose WMA file first", Toast.LENGTH_LONG).show();
                    startTimerSwitch.setChecked(false);
                    return;
                }
                timerParameters.isRunning = isChecked;
                if (isChecked) {
                    MediaPlayerService.start(getApplicationContext());
//                    Timer.startAlarm(getApplicationContext());
                } else {
                    MediaPlayerService.stop(getApplicationContext());
//                    Timer.stopAlarm(getApplicationContext());
                }
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            }
        });

        TextView fileName = (TextView) findViewById(R.id.file_name);
        fileName.setText(timerParameters.soundFileName);

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
            int hour = getArguments().getInt("hours");
            int minute = getArguments().getInt("minutes");
            TimePickerDialog.OnTimeSetListener listener = (TimePickerDialog.OnTimeSetListener) getArguments().getSerializable("listener");
            return new TimePickerDialog(getActivity(), listener, hour, minute, true);
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
        param.putInt("hours", hour);
        param.putInt("minutes", minute);
        param.putSerializable("listener", listener);
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

    public void selectFile(View view) {
        checkPermission();
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
                    checkPermission();
                    return;
                }

            }
        }
    }

    private void checkPermission() {
        if (!hasRunTimePermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }

        try {
            showChooseFileDialogNative();
        } catch (ActivityNotFoundException e) {
            showChooseFileDialogAFile();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_select_file, Toast.LENGTH_SHORT).show();
        }

    }

    private void showChooseFileDialogAFile() {
        Intent getMp3Intent = createGetMp3Intent();
        Intent intent = Intent.createChooser(getMp3Intent, getString(R.string.choose_file));
        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
    }


    private void showChooseFileDialogNative() {
        Intent intent = createGetMp3Intent();
        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
    }


    @NonNull
    private Intent createGetMp3Intent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_FILE_RESULT_CODE:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                if (data == null || data.getData() == null) {
                    Toast.makeText(this, getString(R.string.error_select_file), Toast.LENGTH_SHORT).show();
                    return;
                }

                chosenUri = data.getData();

                timerParameters.soundFileName = UriUtils.extractFilename(getApplicationContext(), chosenUri);
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
                initStartTimerButton();

                FileHelper.saveFile(getApplicationContext(), chosenUri);
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void restartTimerIfNeeded() {
        if (timerParameters.isRunning) {
            MediaPlayerService.stop(getApplicationContext());
            MediaPlayerService.start(getApplicationContext());
//            Timer.stop(getApplicationContext());
//            Timer.start(getApplicationContext());
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

}
