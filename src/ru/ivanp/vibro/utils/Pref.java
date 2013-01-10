package ru.ivanp.vibro.utils;

import ru.ivanp.vibro.App;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Application preferences helper
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class Pref {
	// ========================================================================
	// CONSTANTS
	// ========================================================================
	private final static String VIBRATION_ENABLED_KEY = "vibration_enabled";
	private final static String MINUTE_INTERVAL_KEY = "minute_interval";
	private final static String CANCEL_SMS_VIBRATION_KEY = "cancel_sms_vibration_on_read";

	// ========================================================================
	// FIELDS
	// ========================================================================
	public static boolean vibrationEnabled;
	public static int minuteInterval;
	public static boolean cancelSmsVibration;

	// ========================================================================
	// SAVE LOAD METHODS
	// ========================================================================
	public static void load(Context _context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(_context);

		vibrationEnabled = pref.getBoolean(VIBRATION_ENABLED_KEY, true);
		minuteInterval = Integer.valueOf(pref.getString(MINUTE_INTERVAL_KEY, "55"));
		cancelSmsVibration = pref.getBoolean(CANCEL_SMS_VIBRATION_KEY, true);

		if (App.DEBUG) {
			Log.d("Pref.load", "Preferences loaded");
		}
	}

	public static void save(Context _context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(_context);
		SharedPreferences.Editor editor = pref.edit();
		// now all preferences are save automatically in PreferencesActivities
		editor.commit();

		if (App.DEBUG) {
			Log.d("Pref.save", "Preferences saved");
		}
	}

	public static void reload(Context _context) {
		save(_context);
		load(_context);
	}
}