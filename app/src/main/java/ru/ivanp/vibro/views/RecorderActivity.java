package ru.ivanp.vibro.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.WeakEventHandler;
import ru.ivanp.vibro.vibrations.Player;
import ru.ivanp.vibro.vibrations.UserVibration;
import ru.ivanp.vibro.vibrations.VibrationElement;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.immersion.uhl.ImmVibe;

/**
 * Activity for pattern recording via touching the screen
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class RecorderActivity extends Activity implements OnClickListener, OnTouchListener {
	// ============================================================================================
	// ENUMS
	// ============================================================================================
	/**
	 * Enumerates all possible recorder states
	 */
	private static enum RecorderState {
		/**
		 * Recorder is ready for record/play. Recording will start after button REC or after touch
		 * recording layout. Playing will start after button PLAY
		 */
		IDLE,

		/**
		 * Recoding new pattern
		 */
		RECORDING,

		/**
		 * Playing recorded pattern
		 */
		PLAYING
	}

	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	private static final int TIMER_TICK_WHAT = 0;
	/**
	 * Timer text update interval in milliseconds
	 */
	private static final int INTERVAL_TIMER_TICK = 100;

	// ============================================================================================
	// FIELDS
	// ============================================================================================
	// widgets
	private ProgressBar prgb_dot;
	private ImageView img_settings;
	private TextView text_time_cur;
	private TextView text_intensity_current;
	private TextView text_time_total;
	private RelativeLayout layout_touch;
	private TextView text_state;
	private ImageView img_rec;
	private ImageView img_play;
	private ImageView img_stop;
	private ImageView img_save;
	// preferences
	private boolean isIntensityFixed;
	private int intensityLevel;
	private boolean isLengthLimited;
	private int lengthPatternLimit;
	// fields
	private LocalHandler localHandler;
	private ArrayList<VibrationElement> elements;
	private RecorderState state;
	private long timerStartTime;
	private long lastTouchTime;
	private int lastIntensity;
	private UserVibration recording;
	private boolean isBlockedFromTap;

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recorder);
		setupWidgets();
		localHandler = new LocalHandler(this);
		elements = new ArrayList<VibrationElement>();
		recording = new UserVibration(App.getVibrationManager().getEmptyId());
	}

	@Override
	protected void onResume() {
		super.onResume();
		App.getPlayer().addEventListener(localHandler);
		loadPreferences();
		setState(RecorderState.IDLE);
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
		case R.id.img_settings:
			settings();
			break;
		case R.id.img_rec:
			startRec();
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

	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.layout_touch) {
			// start recording on the first touch only if not blocked
			if (state == RecorderState.IDLE && !isBlockedFromTap) {
				startRec();
			}

			// manage vibration
			int intensity;
			if (state == RecorderState.RECORDING) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					// calculate intensity
					intensity = ImmVibe.VIBE_MAX_MAGNITUDE;
					if (isIntensityFixed) {
						// cause intensityLevel is a percentage value
						intensity = intensityLevel * ImmVibe.VIBE_MAX_MAGNITUDE / 100;
						text_intensity_current.setText(String.format("%d%%", intensityLevel));
					} else {
						int y = (int) event.getY();
						int height = v.getMeasuredHeight();
						float percent = y < 0 ? 1 : y > height ? 0 : (height - y) / (float) height;
						intensity = (int) (percent * ImmVibe.VIBE_MAX_MAGNITUDE);
						text_intensity_current.setText(String.format("%d%%", (int) (percent * 100)));
					}

					App.getPlayer().playMagnitude(intensity);
					break;
				case MotionEvent.ACTION_UP:
					intensity = 0;
					App.getPlayer().playMagnitude(intensity);
					text_intensity_current.setText("0%");
					break;
				default:
					return false;
				}

				long now = SystemClock.elapsedRealtime();
				int diff = (int) (now - lastTouchTime);
				elements.add(new VibrationElement(diff, lastIntensity));
				/*
				 * Log.d("Recorder.onTouch", "diff=" + diff + ", intensity=" + lastIntensity);
				 */
				lastTouchTime = now;
				lastIntensity = intensity;
				return true;
			}
		}
		return false;
	}

	// ============================================================================================
	// MENU
	// ============================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_tap_recorder, menu);
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			settings();
			break;
		}
		return true;
	};

	// ============================================================================================
	// METHODS
	// ============================================================================================
	public static void startActivityForResult(Activity _parentActivity) {
		Intent intent = new Intent(_parentActivity, RecorderActivity.class);
		_parentActivity.startActivityForResult(intent, 0);
	}

	/**
	 * Process widgets setup
	 */
	private void setupWidgets() {
		prgb_dot = (ProgressBar) findViewById(R.id.prgb_dot);
		img_settings = (ImageView) findViewById(R.id.img_settings);
		text_time_cur = (TextView) findViewById(R.id.text_time_current);
		text_intensity_current = (TextView) findViewById(R.id.text_intensity_current);
		text_time_total = (TextView) findViewById(R.id.text_time_total);
		layout_touch = (RelativeLayout) findViewById(R.id.layout_touch);
		text_state = (TextView) findViewById(R.id.text_state);
		img_rec = (ImageView) findViewById(R.id.img_rec);
		img_play = (ImageView) findViewById(R.id.img_play);
		img_stop = (ImageView) findViewById(R.id.img_stop);
		img_save = (ImageView) findViewById(R.id.img_save);

		img_settings.setOnClickListener(this);
		layout_touch.setOnTouchListener(this);
		img_rec.setOnClickListener(this);
		img_play.setOnClickListener(this);
		img_stop.setOnClickListener(this);
		img_save.setOnClickListener(this);
	}

	/**
	 * Initialize activity specific preferences
	 */
	private void loadPreferences() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		isIntensityFixed = pref.getBoolean(RecorderSettingsActivity.RECORDER_FIXED_MAGNITUDE, false);
		intensityLevel = pref.getInt(RecorderSettingsActivity.RECORDER_MAGNITUDE, 100);
		isLengthLimited = pref.getBoolean(RecorderSettingsActivity.RECORDER_LIMIT_DURATION, false);
		lengthPatternLimit = pref.getInt(RecorderSettingsActivity.RECORDER_DURATION, 60);
	}

	/**
	 * Perform recorder state related changes of user interface
	 * 
	 * @param _state
	 *            setting recorder state
	 */
	private void setState(RecorderState _state) {
		state = _state;
		boolean gotVibration = elements.size() > 0;

		prgb_dot.setVisibility(state == RecorderState.RECORDING ? View.VISIBLE : View.GONE);
		text_time_cur.setTextColor(state == RecorderState.IDLE ? getResources().getColor(R.color.gray) : getResources()
				.getColor(R.color.white));
		text_intensity_current.setVisibility(state == RecorderState.RECORDING ? View.VISIBLE : View.INVISIBLE);
		text_time_total.setVisibility(state == RecorderState.RECORDING || !gotVibration ? View.GONE : View.VISIBLE);

		layout_touch.setBackgroundDrawable(state == RecorderState.RECORDING ? getResources().getDrawable(
				R.drawable.recorder_touch_panel_active) : getResources().getDrawable(R.drawable.recorder_touch_panel));

		if (state == RecorderState.IDLE) {
			text_state.setText(isBlockedFromTap ? R.string.rec_to_start : R.string.touch_to_start);
		} else if (state == RecorderState.PLAYING) {
			text_state.setText(R.string.playing);
		} else {
			text_state.setText("");
		}

		img_rec.setEnabled(state == RecorderState.IDLE);
		img_play.setVisibility(state == RecorderState.IDLE ? View.VISIBLE : View.GONE);
		img_stop.setVisibility(state == RecorderState.IDLE ? View.GONE : View.VISIBLE);

		img_play.setEnabled(gotVibration);
		img_save.setEnabled(state == RecorderState.IDLE && gotVibration);
	}

	/**
	 * Opens recorder settings activity
	 */
	private void settings() {
		startActivity(new Intent(this, RecorderSettingsActivity.class));
	}

	private void startRec() {
		isBlockedFromTap = false;
		elements.clear();
		lastTouchTime = SystemClock.elapsedRealtime();
		lastIntensity = 0;
		text_intensity_current.setText("0%");
		startTimer();
		setState(RecorderState.RECORDING);
	}

	private void stop() {
		if (state == RecorderState.RECORDING) {
			recording.setElements(elements);
			text_time_total.setText(formatTimer(recording.getLength()));
		}

		App.getPlayer().stop();
		stopTimer();
		setState(RecorderState.IDLE);
	}

	private void startPlay() {
		App.getPlayer().playOrStop(recording);
		startTimer();
		setState(RecorderState.PLAYING);
	}

	private void save() {
		stop();

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		final EditText txt = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lp.setMargins(30, 30, 30, 30);
		txt.setLayoutParams(lp);
		String baseName = "Untitled-" + new SimpleDateFormat("HH:mm:ss").format(new Date());
		txt.setText(baseName);
		txt.setSelection(baseName.length());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.vibration_name));

		linearLayout.addView(txt);

		builder.setView(linearLayout);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String name = txt.getText().toString().trim();
				if (!name.equals("")) {
					recording.setName(name);
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
		localHandler.sendEmptyMessageDelayed(TIMER_TICK_WHAT, INTERVAL_TIMER_TICK);
	}

	private void stopTimer() {
		text_time_cur.setText(formatTimer(0));
		localHandler.removeMessages(TIMER_TICK_WHAT);
	}

	private void onTimerTick() {
		int diff = (int) (SystemClock.elapsedRealtime() - timerStartTime);
		/* Log.d("Recoreder.onTimerTick", "diff=" + diff); */
		text_time_cur.setText(formatTimer(diff));
		localHandler.sendEmptyMessageDelayed(TIMER_TICK_WHAT, INTERVAL_TIMER_TICK);

		if (isLengthLimited && diff > lengthPatternLimit * 1000) {
			// stop by interval and block from tap
			isBlockedFromTap = true;
			stop();
		}
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

	// ============================================================================================
	// LOCAL HANDLER
	// ============================================================================================
	private static class LocalHandler extends WeakEventHandler<RecorderActivity> {
		private LocalHandler(RecorderActivity _owner) {
			super(_owner);
		}

		@Override
		public void handleEvent(RecorderActivity _owner, int _eventId, Bundle _data) {
			switch (_eventId) {
			case TIMER_TICK_WHAT:
				_owner.onTimerTick();
				break;
			case Player.EVENT_PLAYING_FINISHED:
				_owner.stop();
				break;
			}
		}
	}
}