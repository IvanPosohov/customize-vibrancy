package ru.ivanp.vibro.vibrations;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.ivanp.vibro.R;

/**
 * Provides possible triggers, methods to get and set vibration matched to the
 * trigger. All triggers are separated into two isolated groups. First used for
 * short duration events like Call Finished etc. Second used for long duration
 * events like Incoming Call
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public final class TriggerManager {
	// ==========================================================================
	// CONSTANTS
	// ==========================================================================
	private final int TRIGGERS_COUNT = 8;
	/**
	 * Key to store pattern IDs matched to triggers in SharedPreferences
	 */
	private final static String PATTERN_IDS_KEY = "triggers_pids";

	// default vibration identifiers
	// TODO set default
	private final static int DEFAULT_INCOMING_CALL = 135; // Imperial march
	private final static int DEFAULT_PICK_UP_THE_PHONE = 21; // triple strong
																// click
	private final static int DEFAULT_CALL_FINISHED = 74; // explosion2
	private final static int DEFAULT_CALL_TIME_INTERVAL = 4; // strong click 66
	// private final static int DEFAULT_MISSED_CALL = -1;
	private final static int DEFAULT_INCOMING_SMS = 129; // Mission impossible
	// private final static int DEFAULT_MISSED_SMS = -1;
	private final static int DEFAULT_INTERNET_UNAVAILABLE = 40; // short
																// transition
																// ramp down 66
	private final static int DEFAULT_INTERNET_AVAILABLE_WIFI = 37; // short
																	// transition
																	// ramp up
																	// 66
	private final static int DEFAULT_INTERNET_AVAILABLE_MOBILE = 34; // long
																		// transition
																		// ramp
																		// up 66

	// ==========================================================================
	// FIELDS
	// ==========================================================================
	private Context context;
	private ArrayList<Trigger> triggers;

	// ==========================================================================
	// GETTERS
	// ==========================================================================
	/**
	 * @return trigger list
	 */
	public ArrayList<Trigger> getTriggers() {
		return triggers;
	}

	// ==========================================================================
	// CONSTRUCTOR
	// ==========================================================================
	public TriggerManager(Context _context) {
		context = _context;
		triggers = new ArrayList<Trigger>();

		/*
		 * pattern identifiers matched to triggers stored in SharedPreferences
		 * the same for magnitudes
		 */
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(_context);

		// pattern IDs
		String[] pidsRaw = pref.getString(PATTERN_IDS_KEY, "").split(",");
		int[] pids = new int[TRIGGERS_COUNT];
		if (pidsRaw.length < TRIGGERS_COUNT) {
			/*
			 * preferences are empty or cracked, so pattern id for each trigger
			 * will be set to default value
			 */
			pids[Trigger.INCOMING_CALL] = DEFAULT_INCOMING_CALL;
			pids[Trigger.PICK_UP_THE_PHONE] = DEFAULT_PICK_UP_THE_PHONE;
			pids[Trigger.CALL_FINISHED] = DEFAULT_CALL_FINISHED;
			pids[Trigger.CALL_TIME_INTERVAL] = DEFAULT_CALL_TIME_INTERVAL;
			// pids[Trigger.MISSED_CALL] = DEFAULT_MISSED_CALL;
			pids[Trigger.INCOMING_SMS] = DEFAULT_INCOMING_SMS;
			// pids[Trigger.MISSED_SMS] = DEFAULT_MISSED_SMS;
			pids[Trigger.INTERNET_UNAVAILABLE] = DEFAULT_INTERNET_UNAVAILABLE;
			pids[Trigger.INTERNET_AVAILABLE_WIFI] = DEFAULT_INTERNET_AVAILABLE_WIFI;
			pids[Trigger.INTERNET_AVAILABLE_MOBILE] = DEFAULT_INTERNET_AVAILABLE_MOBILE;
		} else {
			// parse stored values
			for (int i = 0; i < TRIGGERS_COUNT; i++) {
				pids[i] = Integer.valueOf(pidsRaw[i]);
			}
		}

		// fill pattern list
		triggers.add(Trigger.INCOMING_CALL, new Trigger(Trigger.INCOMING_CALL,
				context.getString(R.string.trigger_incoming_call),
				pids[Trigger.INCOMING_CALL]));

		triggers.add(
				Trigger.PICK_UP_THE_PHONE,
				new Trigger(Trigger.PICK_UP_THE_PHONE, context
						.getString(R.string.trigger_pick_up_the_phone),
						pids[Trigger.PICK_UP_THE_PHONE]));

		triggers.add(Trigger.CALL_FINISHED, new Trigger(Trigger.CALL_FINISHED,
				context.getString(R.string.trigger_call_ends),
				pids[Trigger.CALL_FINISHED]));

		triggers.add(
				Trigger.CALL_TIME_INTERVAL,
				new Trigger(Trigger.CALL_TIME_INTERVAL, context
						.getString(R.string.trigger_call_time_interval),
						pids[Trigger.CALL_TIME_INTERVAL]));

		/*
		 * triggers.add(Trigger.MISSED_CALL, new Trigger(Trigger.MISSED_CALL,
		 * context.getString(R.string.trigger_missed_call),
		 * pids[Trigger.MISSED_CALL]));
		 */

		triggers.add(Trigger.INCOMING_SMS, new Trigger(Trigger.INCOMING_SMS,
				context.getString(R.string.trigger_incoming_sms),
				pids[Trigger.INCOMING_SMS]));

		/*
		 * triggers.add(Trigger.MISSED_SMS, new Trigger(Trigger.MISSED_SMS,
		 * context.getString(R.string.trigger_missed_sms),
		 * pids[Trigger.MISSED_SMS]));
		 */

		triggers.add(
				Trigger.INTERNET_UNAVAILABLE,
				new Trigger(Trigger.INTERNET_UNAVAILABLE, context
						.getString(R.string.trigger_internet_unavailable),
						pids[Trigger.INTERNET_UNAVAILABLE]));

		triggers.add(
				Trigger.INTERNET_AVAILABLE_WIFI,
				new Trigger(Trigger.INTERNET_AVAILABLE_WIFI, context
						.getString(R.string.trigger_internet_available_wifi),
						pids[Trigger.INTERNET_AVAILABLE_WIFI]));

		triggers.add(
				Trigger.INTERNET_AVAILABLE_MOBILE,
				new Trigger(Trigger.INTERNET_AVAILABLE_MOBILE, context
						.getString(R.string.trigger_internet_available_mobile),
						pids[Trigger.INTERNET_AVAILABLE_MOBILE]));
	}

	// ==========================================================================
	// METHODS
	// ==========================================================================
	/**
	 * Store trigger array to preferences
	 */
	private void store() {

		// comma separated format
		StringBuilder patternIDs = new StringBuilder();
		for (Trigger trigger : triggers) {
			patternIDs.append(trigger.vibrationID).append(',');
		}
		// store to SharedPreferences
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(PATTERN_IDS_KEY, patternIDs.toString());

		editor.commit();
	}

	/**
	 * Returns trigger
	 * 
	 * @param _triggerID
	 *            trigger identifier (use this class constants)
	 * @return trigger
	 */
	public Trigger getTrigger(int _triggerID) {
		return triggers.get(_triggerID);
	}

	/**
	 * Returns human-readable name of trigger
	 * 
	 * @param _triggerID
	 *            trigger identifier (use this class constants)
	 * @return trigger name
	 */
	public String getName(int _triggerID) {
		return triggers.get(_triggerID).name;
	}

	/**
	 * Returns vibration identifier matched to trigger
	 * 
	 * @param _triggerID
	 *            trigger identifier (use this class constants)
	 * @return vibration identifier or VibrationManager.NO_VIBRATION_ID if
	 *         vibration identifier not set
	 */
	public int getVibrationID(int _triggerID) {
		return triggers.get(_triggerID).vibrationID;
	}

	/**
	 * Checks if user is allowed to create custom vibration
	 * 
	 * @param _triggerID
	 *            trigger identifier (use this class constants)
	 * @return true if user is allowed to create custom vibration, false
	 *         otherwise
	 */
	public boolean isCustomVibrationAllowed(int _triggerID) {
		return _triggerID != Trigger.INCOMING_CALL
				&& _triggerID != Trigger.INCOMING_SMS;
	}

	/**
	 * Set pattern identifier to specified trigger
	 * 
	 * @param _triggerID
	 *            trigger identifier (use this class constants)
	 * @param _patternID
	 *            pattern identifier
	 */
	public void setTrigger(int _triggerID, int _patternID) {
		triggers.get(_triggerID).vibrationID = _patternID;
		store();
	}
}