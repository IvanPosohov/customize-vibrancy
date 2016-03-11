package ru.ivanp.vibro.ui;

import com.immersion.uhl.ImmVibe;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.Timer;
import ru.ivanp.vibro.utils.Timer.OnTimerTickListener;
import ru.ivanp.vibro.utils.WeakEventHandler;
import ru.ivanp.vibro.vibrations.Morse;
import ru.ivanp.vibro.vibrations.Player;
import ru.ivanp.vibro.vibrations.UserVibration;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity for translating text to vibration using Morse code
 *
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class MorseActivity extends BaseActivity implements OnClickListener, OnTimerTickListener {
    // ============================================================================================
    // ENUMS
    // ============================================================================================

    /**
     * Enumerates all possible activity states
     */
    private enum MorseState {
        /**
         * Activity is ready to input/change text which will be translated into vibration pattern
         */
        IDLE,

        /**
         * Playing translated pattern
         */
        PLAYING
    }

    // ============================================================================================
    // CONSTANTS
    // ============================================================================================
    /**
     * Timer text update interval in milliseconds
     */
    private static final int INTERVAL_TIMER_TICK = 100;

    // ============================================================================================
    // FIELDS
    // ============================================================================================
    // widgets
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private EditText inputEditText;
    private View playButton;
    private View stopButton;
    private View saveButton;
    // preferences
    private int intensityLevel;
    private int timeUnitLong;
    // fields
    private LocalHandler localHandler;
    private Timer timer;
    private MorseState state;
    private long timerStartTime;
    private int vibrationLength;
    private UserVibration recording;

    // ============================================================================================
    // OVERRIDDEN
    // ============================================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse);
        currentTimeTextView = (TextView) findViewById(R.id.text_time_current);
        totalTimeTextView = (TextView) findViewById(R.id.text_time_total);
        inputEditText = (EditText) findViewById(R.id.txt_input);
        inputEditText.setFilters(new InputFilter[]{new MorseFilter()});
        inputEditText.addTextChangedListener(new TextChangeHandler());
        playButton = findViewById(R.id.img_play);
        playButton.setOnClickListener(this);
        stopButton = findViewById(R.id.img_stop);
        stopButton.setOnClickListener(this);
        saveButton = findViewById(R.id.img_save);
        saveButton.setOnClickListener(this);

        localHandler = new LocalHandler(this);
        timer = new Timer(INTERVAL_TIMER_TICK, this);
        recording = new UserVibration(App.getVibrationManager().getEmptyId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getPlayer().addEventListener(localHandler);
        loadPreferences();
        setState(MorseState.IDLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.getPlayer().removeEventListener(localHandler);
        App.getPlayer().stop();
        stopTimer();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_play:
                startPlay();
                break;
            case R.id.img_stop:
                stop();
                break;
            case R.id.img_save:
                save();
                break;
        }
    }

    // ============================================================================================
    // MENU
    // ============================================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_morse_recorder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                MorseSettingsActivity.startActivity(this);
                break;
            case R.id.menu_help:
                showHelpDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static void startActivityForResult(Activity _parentActivity) {
        Intent intent = new Intent(_parentActivity, MorseActivity.class);
        _parentActivity.startActivityForResult(intent, 0);
    }

    public static void startActivity(Context _context) {
        Intent intent = new Intent(_context, MorseActivity.class);
        _context.startActivity(intent);
    }

    /**
     * Initialize activity specific preferences
     */
    private void loadPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        intensityLevel = pref.getInt(MorseSettingsActivity.MORSE_MAGNITUDE,
                MorseSettingsActivity.DEFAULT_MORSE_MAGNITUDE);
        timeUnitLong = pref.getInt(MorseSettingsActivity.MORSE_TIME_UNIT_LENGTH,
                MorseSettingsActivity.DEFAULT_MORSE_TIME_UNIT_LENGTH);
    }

    /**
     * Perform recorder state related changes of user interface
     *
     * @param _state setting recorder state
     */
    private void setState(MorseState _state) {
        state = _state;
        boolean gotVibration = vibrationLength > 0;

        currentTimeTextView.setTextColor(state == MorseState.IDLE ? getResources().getColor(R.color.gray) : getResources()
                .getColor(R.color.white));

        inputEditText.setEnabled(state == MorseState.IDLE);

        playButton.setVisibility(state == MorseState.IDLE ? View.VISIBLE : View.GONE);
        stopButton.setVisibility(state == MorseState.IDLE ? View.GONE : View.VISIBLE);

        playButton.setEnabled(gotVibration);
        saveButton.setEnabled(state == MorseState.IDLE && gotVibration);
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.morse_recorder_help)
                .setMessage(R.string.morse_recorder_help_msg)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void stop() {
        App.getPlayer().stop();
        stopTimer();
        setState(MorseState.IDLE);
    }

    private void startPlay() {
        recording.setElements(Morse.translate(inputEditText.getText().toString(), intensityLevel
                * ImmVibe.VIBE_MAX_MAGNITUDE / 100, timeUnitLong));
        App.getPlayer().playOrStop(recording);
        startTimer();
        setState(MorseState.PLAYING);
    }

    private void save() {
        stop();

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        final EditText txt = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        lp.setMargins(30, 30, 30, 30);
        txt.setLayoutParams(lp);
        String baseName = inputEditText.getText().toString();
        txt.setText(baseName);
        txt.setSelection(baseName.length());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.vibration_name);

        linearLayout.addView(txt);

        builder.setView(linearLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String name = txt.getText().toString().trim();
                if (!name.equals("")) {
                    recording.setName(name);
                    recording.setElements(Morse.translate(inputEditText.getText().toString(), intensityLevel
                            * ImmVibe.VIBE_MAX_MAGNITUDE / 100, timeUnitLong));
                    App.getVibrationManager().add(recording);
                    App.getVibrationManager().storeUserVibrations();
                    dialog.dismiss();
                    Intent data = new Intent();
                    data.putExtra(SelectVibrationActivity.VIBRATION_ID_KEY, recording.id);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }

    private void startTimer() {
        timerStartTime = SystemClock.elapsedRealtime();
        timer.start();
    }

    private void stopTimer() {
        currentTimeTextView.setText(formatTimer(0));
        timer.stop();
    }

    @Override
    public void onTimerTick() {
        int diff = (int) (SystemClock.elapsedRealtime() - timerStartTime);
        currentTimeTextView.setText(formatTimer(diff));
    }

    /**
     * Formats value according to a needed format
     *
     * @param _value time in millisecond
     * @return string representation of _value arg in format 'MM:SS.m'
     */
    public static String formatTimer(long _value) {
        // MMM:SS.m
        long ms = _value % 1000 / 100;
        long sec = _value / 1000 % 60;
        long min = _value / 1000 / 60;

        return String.format("%02d:%02d.%d", min, sec, ms);
    }

    // ============================================================================================
    // INTERNAL CLASSES
    // ============================================================================================

    /**
     * Morse permitted characters filter
     */
    private class MorseFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            char[] v = new char[end - start];
            TextUtils.getChars(source, start, end, v, 0);
            // to upper case
            String s = new String(v).toUpperCase();
            for (int i = 0; i < s.length(); i++) {
                char character = s.charAt(i);
                // permitted only latin letters, digits and spaces
                if (!Morse.isAvailable(character) && character != Morse.SPACE) {
                    return "";
                }
            }
            return s;
        }
    }

    /**
     * Handle text change events to text input
     */
    private class TextChangeHandler implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // refreshing pattern length
            vibrationLength = Morse.stringToTimeUnits(s.toString()) * timeUnitLong;
            totalTimeTextView.setText(formatTimer(vibrationLength));

            // enable/disable buttons if need
            playButton.setEnabled(vibrationLength != 0);
            saveButton.setEnabled(vibrationLength != 0);
        }
    }

    // ============================================================================================
    // LOCAL HANDLER
    // ============================================================================================
    private static class LocalHandler extends WeakEventHandler<MorseActivity> {
        private LocalHandler(MorseActivity _owner) {
            super(_owner);
        }

        @Override
        public void handleEvent(MorseActivity _owner, int _eventId, Bundle _data) {
            switch (_eventId) {
                case Player.EVENT_PLAYING_FINISHED:
                    _owner.stop();
                    break;
            }
        }
    }
}