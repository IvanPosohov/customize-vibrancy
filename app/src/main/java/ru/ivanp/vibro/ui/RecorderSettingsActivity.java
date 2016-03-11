package ru.ivanp.vibro.ui;

import com.immersion.uhl.ImmVibe;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.SeekbarPreference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Settings activity for touch recorder
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
@SuppressWarnings("deprecation")
public class RecorderSettingsActivity extends BasePreferencesActivity implements OnSeekBarChangeListener {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	// preference keys
	public static final String RECORDER_FIXED_MAGNITUDE = "recorder_fixed_magnitude";
	public static final String RECORDER_MAGNITUDE = "recorder_magnitude";
	public static final String RECORDER_LIMIT_DURATION = "recorder_limit_duration_of_vibration";
	public static final String RECORDER_DURATION = "recorder_duration";

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.recorder_preferences);

		// set intensity seekbar change listener
		SeekbarPreference recoder_intensity = (SeekbarPreference) findPreference(RECORDER_MAGNITUDE);
		recoder_intensity.setOnSeekBarChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		App.getPlayer().stop();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int magnitude = progress * ImmVibe.VIBE_MAX_MAGNITUDE / 100;
		App.getPlayer().playMagnitude(magnitude);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		int magnitude = seekBar.getProgress() * ImmVibe.VIBE_MAX_MAGNITUDE / 100;
		App.getPlayer().playMagnitude(magnitude);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		App.getPlayer().stop();
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================
	public static void startActivity(Context _context) {
		Intent intent = new Intent(_context, RecorderSettingsActivity.class);
		_context.startActivity(intent);
	}
}