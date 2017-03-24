package test.upwork.timer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.repeat_spinner)
    Spinner repeatSpinner;

    @BindView(R.id.play_interval_spinner)
    Spinner playIntervalSpinner;

    @BindView(R.id.pause_onterval_spinner)
    Spinner pauseIntervalSpinner;

    @BindView(R.id.fromTime)
    TimePicker fromTimePicker;

    @BindView(R.id.toTime)
    TimePicker toTimePicker;

    @BindView(R.id.startTimerButton)
    Button startTimerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TimerParameters timerParameters = PreferencesAdapter.getTimerParameters(getApplicationContext());
        initRepeat(timerParameters);
        initFromToTime(timerParameters);
        initPlayInterval(timerParameters);
        initPauseInterval(timerParameters);
        initStartTimerButton(timerParameters);
    }

    private void initStartTimerButton(final TimerParameters timerParameters) {
        startTimerButton.setText(timerParameters.isRunning ? R.string.button_stop : R.string.button_start);
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerParameters.isRunning = !timerParameters.isRunning;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);

            }
        });
    }

    private void initPlayInterval(final TimerParameters timerParameters) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.play_interval, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playIntervalSpinner.setAdapter(adapter);
        playIntervalSpinner.setSelection(timerParameters.playInterval);

        playIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timerParameters.playInterval = position;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initPauseInterval(final TimerParameters timerParameters) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.pause_interval, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pauseIntervalSpinner.setAdapter(adapter);
        pauseIntervalSpinner.setSelection(timerParameters.pauseInterval);

        pauseIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timerParameters.pauseInterval = position;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initFromToTime(final TimerParameters timerParameters) {
        Calendar calendar = Calendar.getInstance();

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);


        fromTimePicker.setIs24HourView(true);
        fromTimePicker.setCurrentHour(timerParameters.fromHour);
        fromTimePicker.setCurrentMinute(timerParameters.fromMinute);

        fromTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timerParameters.fromHour=hourOfDay;
                timerParameters.fromMinute=minute;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);

            }
        });

        toTimePicker.setIs24HourView(true);
        toTimePicker.setCurrentHour(timerParameters.toHour);
        toTimePicker.setCurrentMinute(timerParameters.toMinute);

        toTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timerParameters.toHour=hourOfDay;
                timerParameters.toMinute=minute;
                PreferencesAdapter.saveTimerParameters(getApplicationContext(), timerParameters);
            }
        });

    }

    private void initRepeat(final TimerParameters timerParameters) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.repeat_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
}
