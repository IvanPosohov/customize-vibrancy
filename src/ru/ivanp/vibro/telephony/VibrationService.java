package ru.ivanp.vibro.telephony;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.Pref;
import ru.ivanp.vibro.utils.WeakEventHandler;
import ru.ivanp.vibro.vibrations.Player;
import ru.ivanp.vibro.vibrations.Vibration;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import ru.ivanp.vibro.views.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * Allow to play vibration in background
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class VibrationService extends Service {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	private static final String VIBRATION_ID_KEY = "vibration_id";
	private static final String REPEAT_KEY = "repeat";
	private static final String STOP_SELF_DELAY_KEY = "stop_self_delay";
	private static final int NOTIFICATION_ID = 16030901;

	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private LocalHandler handler;
	private PowerManager.WakeLock wakeLock;
	private NotificationManager notificationManager;

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int vibrationID = intent.getIntExtra(VIBRATION_ID_KEY, VibrationsManager.NO_VIBRATION_ID);
		boolean repeat = intent.getBooleanExtra(REPEAT_KEY, false);
		int stopSelfDelay = intent.getIntExtra(STOP_SELF_DELAY_KEY, -1);

		// check for correct vibration ID
		if (vibrationID == VibrationsManager.NO_VIBRATION_ID) {
			stopSelf();
		}

		// start foreground only if High priority enabled
		if (Pref.enableHighPriority) {
			notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			// set the icon, scrolling text and timestamp
			Notification notification = new Notification(R.drawable.ic_notification, getText(R.string.app_name),
					System.currentTimeMillis());

			// the PendingIntent to launch our activity if the user selects this
			// notification
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

			// set the info for the views that show in the notification panel.
			notification
					.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.app_name), contentIntent);

			// send the notification.
			notificationManager.notify(NOTIFICATION_ID, notification);

			// start this service as foreground
			startForeground(NOTIFICATION_ID, notification);
		}

		handler = new LocalHandler(this);
		// create wake lock to keep screen on
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
				"Customize Vibrancy");
		wakeLock.acquire();

		// stop self after delay if need
		if (stopSelfDelay != -1) {
			handler.sendEmptyMessageDelayed(Player.EVENT_PLAYING_FINISHED, stopSelfDelay * 1000);
		}

		// get vibration
		Vibration vibration = App.getVibrationManager().getVibration(vibrationID);

		// start vibration
		App.getPlayer().addEventListener(handler);
		if (repeat) {
			App.getPlayer().playRepeat(vibration);
		} else {
			App.getPlayer().playOrStop(vibration);
		}

		if (App.DEBUG) {
			Log.d("VibroService.onStartCommand", "Service started, vibrationID=" + vibrationID);
		}

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		App.getPlayer().removeEventListener(handler);
		App.getPlayer().stop();

		// stop foreground service and remove notification from statusBar
		stopForeground(true);

		if (wakeLock.isHeld()) {
			wakeLock.release();
		}

		if (App.DEBUG) {
			Log.d("VibroService.onDestroy", "Service destriyed");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================
	/**
	 * Start service with needed arguments
	 * 
	 * @param _context
	 *            application context
	 * @param _vibrationID
	 *            vibration identifier which need to be played
	 * @param _repeat
	 *            repeat vibration until service manual stop
	 * @param _stopSelfDelay
	 *            delay to automatic stop service in seconds, -1 if not used
	 */
	public static void start(Context _context, int _vibrationID, boolean _repeat, int _stopSelfDelay) {
		Intent intent = new Intent(_context, VibrationService.class);
		intent.putExtra(VIBRATION_ID_KEY, _vibrationID);
		intent.putExtra(REPEAT_KEY, _repeat);
		intent.putExtra(STOP_SELF_DELAY_KEY, _stopSelfDelay);
		_context.startService(intent);
	}

	// ============================================================================================
	// LOCAL HANDLER
	// ============================================================================================
	private static class LocalHandler extends WeakEventHandler<VibrationService> {
		private LocalHandler(VibrationService _owner) {
			super(_owner);
		}

		@Override
		public void handleEvent(VibrationService _owner, int _eventId, Bundle _data) {
			switch (_eventId) {
			case Player.EVENT_PLAYING_FINISHED:
				// stops service after playing finished
				_owner.stopSelf();
				break;
			}
		}
	}
}