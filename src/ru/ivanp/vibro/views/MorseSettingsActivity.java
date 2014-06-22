package ru.ivanp.vibro.views;

import com.immersion.uhl.ImmVibe;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.R.layout;
import ru.ivanp.vibro.R.xml;
import ru.ivanp.vibro.utils.SeekbarPreference;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Settings activity for Morse translator activity
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class MorseSettingsActivity extends PreferenceActivity implements OnSeekBarChangeListener {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	// preference keys
	public static final String MORSE_MAGNITUDE = "morse_magnitude";
	public static final String MORSE_TIME_UNIT_LENGTH = "morse_time_unit_length";

	public static final int DEFAULT_MORSE_MAGNITUDE = 100;
	public static final int DEFAULT_MORSE_TIME_UNIT_LENGTH = 100;

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_morse_settings);
		addPreferencesFromResource(R.xml.morse_settings);

		// set intensity seekbar change listener
		SeekbarPreference morse_intensity = (SeekbarPreference) findPreference(MORSE_MAGNITUDE);
		morse_intensity.setOnSeekBarChangeListener(this);
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
}