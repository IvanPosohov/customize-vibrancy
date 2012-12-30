package ru.ivanp.vibro;

import java.lang.ref.WeakReference;

import com.immersion.uhl.ImmVibe;

import ru.ivanp.vibro.vibrations.Morse;
import ru.ivanp.vibro.vibrations.Player;
import ru.ivanp.vibro.vibrations.UserVibration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity for translating text to vibration using Morse code
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class MorseActivity extends Activity implements OnClickListener {
	// ========================================================================
	// ENUMS
	// ========================================================================
	/**
	 * Enumerates all possible activity states
	 */
	private static enum MorseState {
		/**
		 * Activity is ready to input/change text which will be translated into
		 * vibration pattern
		 */
		IDLE,

		/**
		 * Playing translated pattern
		 */
		PLAYING
	}

	// ========================================================================
	// CONSTANTS
	// ========================================================================
	private static final int TIMER_TICK_WHAT = 0;
	/**
	 * Timer text update interval in milliseconds
	 */
	private static final int INTERVAL_TIMER_TICK = 100;

	// ========================================================================
	// FIELDS
	// ========================================================================
	// widgets
	private TextView text_time_cur;
	private TextView text_time_total;
	private EditText txt_input;
	private ImageView img_play;
	private ImageView img_stop;
	private ImageView img_save;
	// preferences
	private int intensityLevel;
	private int timeUnitLong;
	// fields
	private LocalHandler localHandler;
	private MorseState state;
	private long timerStartTime;
	private int vibrationLength;
	private UserVibration recording;

	// ========================================================================
	// OVERRIDDEN
	// ========================================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_morse);
		setupWidgets();
		localHandler = new LocalHandler(this);
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
		case R.id.img_help:
			help();
			break;
		case R.id.img_settings:
			settings();
			break;
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

	// ========================================================================
	// MENU
	// ========================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_morse_recorder, menu);
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			settings();
			break;

		case R.id.menu_help:
			help();
			break;
		}
		return true;
	};

	// ========================================================================
	// METHODS
	// ========================================================================
	/**
	 * Process widgets setup
	 */
	private void setupWidgets() {
		ImageView img_help = (ImageView) findViewById(R.id.img_help);
		ImageView img_settings = (ImageView) findViewById(R.id.img_settings);
		text_time_cur = (TextView) findViewById(R.id.text_time_current);
		text_time_total = (TextView) findViewById(R.id.text_time_total);
		txt_input = (EditText) findViewById(R.id.txt_input);
		txt_input.setFilters(new InputFilter[] { new MorseFilter() });
		txt_input.addTextChangedListener(new TextChangeHandler());
		img_play = (ImageView) findViewById(R.id.img_play);
		img_stop = (ImageView) findViewById(R.id.img_stop);
		img_save = (ImageView) findViewById(R.id.img_save);

		img_help.setOnClickListener(this);
		img_settings.setOnClickListener(this);
		img_play.setOnClickListener(this);
		img_stop.setOnClickListener(this);
		img_save.setOnClickListener(this);
	}

	/**
	 * Initialize activity specific preferences
	 */
	private void loadPreferences() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		intensityLevel = pref.getInt(MorseSettingsActivity.MORSE_MAGNITUDE,
				MorseSettingsActivity.DEFAULT_MORSE_MAGNITUDE);
		timeUnitLong = pref.getInt(
				MorseSettingsActivity.MORSE_TIME_UNIT_LENGTH,
				MorseSettingsActivity.DEFAULT_MORSE_TIME_UNIT_LENGTH);
	}

	/**
	 * Perform recorder state related changes of user interface
	 * 
	 * @param _state
	 *            setting recorder state
	 */
	private void setState(MorseState _state) {
		state = _state;
		boolean gotVibration = vibrationLength > 0;

		text_time_cur.setTextColor(state == MorseState.IDLE ? getResources()
				.getColor(R.color.gray) : getResources()
				.getColor(R.color.white));

		txt_input.setEnabled(state == MorseState.IDLE);

		img_play.setVisibility(state == MorseState.IDLE ? View.VISIBLE
				: View.GONE);
		img_stop.setVisibility(state == MorseState.IDLE ? View.GONE
				: View.VISIBLE);

		img_play.setEnabled(gotVibration);
		img_save.setEnabled(state == MorseState.IDLE && gotVibration);
	}

	/**
	 * Opens Morse translator help activity
	 */
	private void help() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.morse_recorder_help)
				.setMessage(R.string.morse_recorder_help_msg)
				.setPositiveButton("OK", null).create().show();
	}

	/**
	 * Opens Morse translator settings activity
	 */
	private void settings() {
		startActivity(new Intent(this, MorseSettingsActivity.class));
	}

	private void stop() {
		App.getPlayer().stop();
		stopTimer();
		setState(MorseState.IDLE);
	}

	private void startPlay() {
		recording.setElements(Morse
				.translate(txt_input.getText().toString(), intensityLevel
						* ImmVibe.VIBE_MAX_MAGNITUDE / 100, timeUnitLong));
		App.getPlayer().playOrStop(recording);
		startTimer();
		setState(MorseState.PLAYING);
	}

	private void save() {
		stop();

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		final EditText txt = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lp.setMargins(30, 30, 30, 30);
		txt.setLayoutParams(lp);
		String baseName = txt_input.getText().toString();
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
					recording.setElements(Morse
							.translate(txt_input.getText().toString(), intensityLevel
									* ImmVibe.VIBE_MAX_MAGNITUDE / 100, timeUnitLong));
					App.getVibrationManager().add(recording);
					App.getVibrationManager().storeUserVibrations();
					dialog.dismiss();
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		});

		builder.setNegativeButton(getString(R.string.cancel), null);
		builder.create().show();
	}

	private void startTimer() {
		timerStartTime = SystemClock.elapsedRealtime();
		localHandler.sendEmptyMessageDelayed(TIMER_TICK_WHAT,
				INTERVAL_TIMER_TICK);
	}

	private void stopTimer() {
		text_time_cur.setText(formatTimer(0));
		localHandler.removeMessages(TIMER_TICK_WHAT);
	}

	private void onTimerTick() {
		int diff = (int) (SystemClock.elapsedRealtime() - timerStartTime);
		text_time_cur.setText(formatTimer(diff));
		localHandler.sendEmptyMessageDelayed(TIMER_TICK_WHAT,
				INTERVAL_TIMER_TICK);
	}

	/**
	 * Formats value according to a needed format
	 * 
	 * @param _value
	 *            time in millisecond
	 * @return string representation of _value arg in format 'MM:SS.m'
	 */
	public static String formatTimer(long _value) {
		// MMM:SS.m
		long ms = _value % 1000 / 100;
		long sec = _value / 1000 % 60;
		long min = _value / 1000 / 60;

		return String.format("%02d:%02d.%d", min, sec, ms);
	}

	// ========================================================================
	// INTERNAL CLASSES
	// ========================================================================
	/**
	 * Morse permitted characters filter
	 */
	private class MorseFilter implements InputFilter {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
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
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// refreshing pattern length
			vibrationLength = Morse.stringToTimeUnits(s.toString())
					* timeUnitLong;
			text_time_total.setText(formatTimer(vibrationLength));

			// enable/disable buttons if need
			img_play.setEnabled(vibrationLength != 0);
			img_save.setEnabled(vibrationLength != 0);
		}
	}

	/**
	 * Message handler for MorseActivity
	 */
	private static class LocalHandler extends Handler {
		private WeakReference<MorseActivity> mTarget;

		private LocalHandler(MorseActivity _target) {
			mTarget = new WeakReference<MorseActivity>(_target);
		}

		@Override
		public void handleMessage(Message msg) {
			MorseActivity target = mTarget.get();
			switch (msg.what) {
			case TIMER_TICK_WHAT:
				target.onTimerTick();
				break;
			case Player.EVENT_PLAYING_FINISHED:
				target.stop();
				break;
			}
			super.handleMessage(msg);
		}
	}
}