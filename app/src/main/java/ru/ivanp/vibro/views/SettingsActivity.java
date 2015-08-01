package ru.ivanp.vibro.views;

import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.Pref;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * Application settings activity
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends BasePreferencesActivity implements OnPreferenceClickListener {
	// ============================================================================================
	// LIFECYCLE
	// ============================================================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	
    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static void startActivity(Context _context) {
        Intent intent = new Intent(_context, SettingsActivity.class);
        _context.startActivity(intent);
    }
}