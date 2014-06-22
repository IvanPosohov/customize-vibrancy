package ru.ivanp.vibro.views;

import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.Pref;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * Application settings activity
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener {
	// ============================================================================================
	// LIFECYCLE
	// ============================================================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		addPreferencesFromResource(R.xml.app_settings);
		findPreference("about").setOnPreferenceClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Pref.reload(this);
	}

    // ============================================================================================
    // WIDGET CALLBACKS
    // ============================================================================================
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals("about")) {
            AboutActivity.startActivity(this);
        }
		return false;
	}
}