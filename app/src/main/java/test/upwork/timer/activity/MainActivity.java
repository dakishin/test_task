package test.upwork.timer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.util.Calendar;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(getApplicationContext());
        initRepeat(timerParameters);
        initFromToTime(timerParameters);
        initPlayInterval(timerParameters);
        initPauseInterval(timerParameters);
        initStartTimerButton(timerParameters);
    }


    private void initStartTimerButton(final TimerParameters timerParameters) {
        Switch startTimerSwitch = (Switch) findViewById(R.id.startTimerSwitch);
        startTimerSwitch.setChecked(timerParameters.isRunning);
        startTimerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timerParameters.isRunning = isChecked;
                if (isChecked) {
                    MediaPlayerService.start(getApplicationContext());
//                    Timer.startAlarm(getApplicationContext());
                } else {
                    MediaPlayerService.stop(getApplicationContext());
//                    Timer.stopAlarm(getApplicationContext());
                }
            }
        });

        TextView fileName = (TextView) findViewById(R.id.file_name);
        fileName.setText(timerParameters.soundFileName);

    }

    private void initPlayInterval(final TimerParameters timerParameters) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.interval_minutes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner playIntervalSpinner = (Spinner) findViewById(R.id.play_interval_spinner);
        playIntervalSpinner.setAdapter(adapter);

        playIntervalSpinner.setSelection(timerParameters.playIntervalInMinutes - 1);

        playIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timerParameters.playIntervalInMinutes = position + 1;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initPauseInterval(final TimerParameters timerParameters) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.interval_minutes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner pauseIntervalSpinner = (Spinner) findViewById(R.id.pause_onterval_spinner);


        pauseIntervalSpinner.setAdapter(adapter);
        pauseIntervalSpinner.setSelection(timerParameters.pauseIntervalInMinutes - 1);

        pauseIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timerParameters.pauseIntervalInMinutes = position + 1;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void initFromToTime(final TimerParameters timerParameters) {
        final TimePicker fromTimePicker = (TimePicker) findViewById(R.id.fromTime);
        final TimePicker toTimePicker = (TimePicker) findViewById(R.id.toTime);


        fromTimePicker.setIs24HourView(true);
        fromTimePicker.setCurrentHour(timerParameters.fromHour);
        fromTimePicker.setCurrentMinute(timerParameters.fromMinute);

        fromTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timerParameters.fromHour = hourOfDay;
                timerParameters.fromMinute = minute;
                if (getCalendarFromPicker(fromTimePicker).after(getCalendarFromPicker(toTimePicker))) {
                    toTimePicker.setCurrentHour(hourOfDay);
                    toTimePicker.setCurrentMinute(minute);
                    timerParameters.toHour = hourOfDay;
                    timerParameters.toMinute = minute;
                }
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);

            }
        });

        toTimePicker.setIs24HourView(true);
        toTimePicker.setCurrentHour(timerParameters.toHour);
        toTimePicker.setCurrentMinute(timerParameters.toMinute);

        toTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timerParameters.toHour = hourOfDay;
                timerParameters.toMinute = minute;
                if (getCalendarFromPicker(fromTimePicker).after(getCalendarFromPicker(toTimePicker))) {
                    toTimePicker.setCurrentHour(fromTimePicker.getCurrentHour());
                    toTimePicker.setCurrentMinute(fromTimePicker.getCurrentMinute());
                    timerParameters.toHour = fromTimePicker.getCurrentHour();
                    timerParameters.toMinute = fromTimePicker.getCurrentMinute();
                }
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            }
        });

    }


    private Calendar getCalendarFromPicker(TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        return calendar;
    }

    private void initRepeat(final TimerParameters timerParameters) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.repeat_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner repeatSpinner = (Spinner) findViewById(R.id.repeat_spinner);
        repeatSpinner.setAdapter(adapter);
        repeatSpinner.setSelection(timerParameters.repeatInterval);

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timerParameters.repeatInterval = position;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
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

                TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(getApplicationContext());
                timerParameters.soundFileName = UriUtils.extractFilename(getApplicationContext(), chosenUri);
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
                initStartTimerButton(timerParameters);

                FileHelper.saveFile(getApplicationContext(), chosenUri);
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }


}
