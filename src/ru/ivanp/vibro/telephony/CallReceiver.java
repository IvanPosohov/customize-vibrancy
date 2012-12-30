package ru.ivanp.vibro.telephony;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.utils.Pref;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Receive call state changes, registering them and do processing stuff like
 * start vibration service or call service etc. <br>
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class CallReceiver extends BroadcastReceiver {
	// ========================================================================
	// CONSTANTS
	// ========================================================================
	/**
	 * Key to store last call state in SharedPreferences
	 */
	private static final String STATE_KEY = "call_state";

	/**
	 * Key to store second call flag in SharedPreferences
	 */
	private static final String SECOND_CALL_KEY = "second_call";

	// ========================================================================
	// OVERRIDDEN
	// ========================================================================
	public void onReceive(Context context, Intent intent) {
		// at first check preferences
		if (!Pref.vibrationEnabled) {
			if (App.DEBUG) {
				Log.d("CallReceiver.onReceive",
						"Return without processing, because vibration disabled in preferences");
			}
			return;
		}

		// find old and new call state
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String oldState = pref.getString(STATE_KEY,
				TelephonyManager.EXTRA_STATE_IDLE);
		String newState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

		// store new state
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(STATE_KEY, newState);

		// process state change
		if (App.DEBUG) {
			Log.d("CallReceiver.onReceive", "Start processing, oldState = "
					+ oldState + ", newState = " + newState);
		}

		/*
		 * 1) IDLE -> RINGING. First incoming call and need to start repeating
		 * vibration while user picks up the phone
		 */
		if (oldState.equals(TelephonyManager.EXTRA_STATE_IDLE)
				&& newState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			int imcomingCallVibrationID = App.getTriggerManager()
					.getVibrationID(Trigger.INCOMING_CALL);
			if (imcomingCallVibrationID != VibrationsManager.NO_VIBRATION_ID) {
				if (App.DEBUG) {
					Log.d("CallReceiver.onReceive",
							"play incoming call vibration");
				}
				VibrationService.start(context, imcomingCallVibrationID, true,
						-1);
			}
		}

		/*
		 * 2) RINGING -> OFFHOOK. Incoming call, user picked up the phone, stop
		 * incoming call vibration, check if this call the first one if it start
		 * minute interval timer, otherwise do nothing
		 */
		else if (oldState.equals(TelephonyManager.EXTRA_STATE_RINGING)
				&& newState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
			VibrationService.stop(context);
			if (!pref.getBoolean(SECOND_CALL_KEY, false)) {
				CallService.start(context, false);
			}
		}

		/*
		 * 3) IDLE -> OFFHOOK. Outgoing call. Start log watcher to know when
		 * user on the over side pick up the phone
		 */
		else if (oldState.equals(TelephonyManager.EXTRA_STATE_IDLE)
				&& newState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
			CallService.start(context, true);
		}

		/*
		 * 4) OFFHOOK -> IDLE. Call finished, stop timer (or log watcher) play
		 * call finished vibration and erase second call flag
		 */
		else if (oldState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)
				&& newState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
			// stop call service
			CallService.stop(context);
			// play call finished vibration if need
			int callFinishedVibrationID = App.getTriggerManager()
					.getVibrationID(Trigger.CALL_FINISHED);
			if (callFinishedVibrationID != VibrationsManager.NO_VIBRATION_ID) {
				if (App.DEBUG) {
					Log.d("CallReceiver.onReceive",
							"play call finished vibration");
				}
				VibrationService.start(context, callFinishedVibrationID, false,
						-1);
			}
			// erase second call flag
			editor.putBoolean(SECOND_CALL_KEY, false);
		}
		/*
		 * 5) OFFHOOK -> RINGING. Second incoming call while we already have one
		 */
		else if (oldState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)
				&& newState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			editor.putBoolean(SECOND_CALL_KEY, true);
		}

		editor.commit();
	}
}