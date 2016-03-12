package ru.ivanp.vibro.ui;

import com.immersion.uhl.ImmVibe;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.views.SeekBarPreference;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.SeekBar;

/**
 * Settings activity for touch recorder
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class RecorderSettingsFragment extends PreferenceFragmentCompat {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	// preference keys
	public static final String RECORDER_FIXED_MAGNITUDE = "recorder_fixed_magnitude";
	public static final String RECORDER_MAGNITUDE = "recorder_magnitude";
	public static final String RECORDER_LIMIT_DURATION = "recorder_limit_duration_of_vibration";
	public static final String RECORDER_DURATION = "recorder_duration";

    // ============================================================================================
    // LIFECYCLE
    // ============================================================================================
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.recorder_preferences);

		// set intensity seekbar change listener
		SeekBarPreference seekBarPreference = (SeekBarPreference) findPreference(RECORDER_MAGNITUDE);
		seekBarPreference.setOnSeekBarChangeListener(onSeekBarChangeListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		App.getPlayer().stop();
	}

	// ============================================================================================
	// SEEK BAR LISTENER
	// ============================================================================================
	private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
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
	};

    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static RecorderSettingsFragment newInstance() {
        return new RecorderSettingsFragment();
    }
}