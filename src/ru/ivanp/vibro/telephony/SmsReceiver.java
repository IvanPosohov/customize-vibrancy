package ru.ivanp.vibro.telephony;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.utils.Pref;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * New SMS notify receiver <br>
 * Receiver register broadcasts and start SmsService if needs.
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SmsReceiver extends BroadcastReceiver {
	// ========================================================================
	// OVERRIDDEN
	// ========================================================================
	@Override
	public void onReceive(Context context, Intent intent) {
		if (App.DEBUG) {
			Log.d("SmsReceiver.onReceive",
					"Action = " + intent != null ? intent.getAction() : "null");
		}

		if (Pref.vibrationEnabled) {
			// if application and phone settings allow to vibrate
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (manager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
				// if there are no calls in this moment
				if (Pref.cancelSmsVibration) {
					// start service, it will stop self after read sms or
					// vibration finished
					SmsService.start(context);
				} else {
					// start vibration service, it will be stopped after vibration
					// finished or after 30 seconds delay
					int imcomingSmsVibrationID = App.getTriggerManager().getVibrationID(Trigger.INCOMING_SMS);
					if (imcomingSmsVibrationID != VibrationsManager.NO_VIBRATION_ID) {
						VibrationService.start(context, imcomingSmsVibrationID, false, 30);
					}
				}
			} else {
				if (App.DEBUG)
					Log.d("SmsReceiver.onReceive",
							"Can't start SmsService because call state is not IDLE");
			}
		} else {
			if (App.DEBUG) {
				Log.d("SmsReceiver.onReceive",
						"Can't start SmsService according to preferences");
			}
		}
	}
}