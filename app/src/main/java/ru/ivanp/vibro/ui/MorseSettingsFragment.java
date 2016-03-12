package ru.ivanp.vibro.ui;

import com.immersion.uhl.ImmVibe;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.views.SeekBarPreference;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.SeekBar;

/**
 * Settings activity for Morse translator activity
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class MorseSettingsFragment extends PreferenceFragmentCompat {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	// preference keys
	public static final String MORSE_MAGNITUDE = "morse_magnitude";
	public static final String MORSE_TIME_UNIT_LENGTH = "morse_time_unit_length";

	public static final int DEFAULT_MORSE_MAGNITUDE = 100;
	public static final int DEFAULT_MORSE_TIME_UNIT_LENGTH = 100;

	// ============================================================================================
	// LIFECYCLE
	// ============================================================================================
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.morse_preferences);
        SeekBarPreference seekBarPreference = (SeekBarPreference) findPreference(MORSE_MAGNITUDE);
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
    public static MorseSettingsFragment newInstance() {
        return new MorseSettingsFragment();
    }
}