package ru.ivanp.vibro.telephony;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.utils.Pref;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Receive Internet state changes
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class InternetStateReceiver extends BroadcastReceiver {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	/**
	 * Key to store current Internet state value
	 */
	private final static String STATE_KEY = "internet_state";

	/**
	 * Internet is not available
	 */
	public static final int STATE_NONE = 0;

	/**
	 * Internet is available via Wi-Fi
	 */
	public static final int STATE_WIFI = 1;

	/**
	 * Internet is available via mobile (gprs, 3g, etc.)
	 */
	public static final int STATE_MOBILE = 2;

	// ============================================================================================
	// OVERRIDEN
	// ============================================================================================
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			return;
		}
		String action = intent.getAction();
		if (App.DEBUG) {
			Log.d("InternetStateReceiver.onReceive", "Action = " + action);
		}

		if (action == null || !action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			return;
		}

		if (Pref.vibrationEnabled) {
			// if application and phone settings allow to vibrate
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (manager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
				// if there are no calls in this moment

				// get old Internet state, stored in preferences
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
				int oldState = pref.getInt(STATE_KEY, STATE_NONE);
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

				// get current Internet state
				int currentState = STATE_NONE;
				if (networkInfo != null && networkInfo.getState() == State.CONNECTED) {
					currentState = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE ? STATE_MOBILE
							: STATE_WIFI;
				}

				// if state has changed
				if (oldState != currentState) {
					// store current state
					SharedPreferences.Editor editor = pref.edit();
					editor.putInt(STATE_KEY, currentState);
					editor.commit();

					// start vibration
					int vibrationID = VibrationsManager.NO_VIBRATION_ID;
					switch (currentState) {
					case STATE_NONE:
						vibrationID = App.getTriggerManager().getVibrationID(
								Trigger.INTERNET_UNAVAILABLE);
						break;
					case STATE_WIFI:
						vibrationID = App.getTriggerManager().getVibrationID(
								Trigger.INTERNET_AVAILABLE_WIFI);
						break;
					case STATE_MOBILE:
						vibrationID = App.getTriggerManager().getVibrationID(
								Trigger.INTERNET_AVAILABLE_MOBILE);
						break;
					}
					if (vibrationID != VibrationsManager.NO_VIBRATION_ID) {
						// start vibration
						VibrationService.start(context, vibrationID, false, -1);
					}
				}
			} else {
				if (App.DEBUG)
					Log.d("InternetStateReceiver.onReceive",
							"Can't start VibrationService because call state is not IDLE");
			}
		} else {
			if (App.DEBUG) {
				Log.d("InternetStateReceiver.onReceive",
						"Can't start VibrationService according to preferences");
			}
		}
	}
}