package ru.ivanp.vibro.ui;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.Pref;

/**
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    // ============================================================================================
    // LIFECYCLE
    // ============================================================================================
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        findPreference("about").setOnPreferenceClickListener(onPreferenceClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Pref.reload(getActivity());
    }

    // ============================================================================================
    // CLICK HANDLER
    // ============================================================================================
    private final Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "about":
                    AboutActivity.startActivity(getActivity());
                    return true;
            }
            return false;
        }
    };

    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
}
