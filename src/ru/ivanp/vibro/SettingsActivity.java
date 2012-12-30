package ru.ivanp.vibro;

import ru.ivanp.vibro.utils.Pref;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Application settings activity
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SettingsActivity extends PreferenceActivity {
	// ========================================================================
	// OVERRIDDEN
	// ========================================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		addPreferencesFromResource(R.xml.app_settings);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Pref.reload(this);
	}
}