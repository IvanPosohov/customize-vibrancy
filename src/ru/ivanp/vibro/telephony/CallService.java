package ru.ivanp.vibro.telephony;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.utils.Pref;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * Vibration management service at calling <br>
 * Service starts with from CallReceiver.
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class CallService extends Service {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	private final static String START_LOG_WATCHER_KEY = "start_log_watcher";

	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private TimerHandler handler;
	private LogThread logThread;
	private int callTimeIntervalVibrationID;
	private long callStartTime;

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean startLogWatcher = intent.getBooleanExtra(START_LOG_WATCHER_KEY, false);

		handler = new TimerHandler(this);

		/*
		 * when we got outgoing call, we need to start log parser to know to
		 * know when user on the over side pick up the phone
		 */
		if (startLogWatcher) {
			startLogWatcher();
		} else {
			// on incoming call just start interval timer
			startTimer();
		}

		if (App.DEBUG) {
			Log.d("CallService.onStartCommand", "Service started, startLogWatcher="
					+ startLogWatcher);
		}

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		App.getPlayer().stop();
		handler.removeMessages(0);
		if (logThread != null) {
			logThread.kill();
		}

		if (App.DEBUG) {
			Log.d("CallService.onDestroy", "Service destriyed");
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
	 * @param _startLogWatcher
	 *            if true Log watcher thread will be started at first, to know
	 *            to know when user on the over side pick up the phone,
	 *            otherwise minute interval timer will be started
	 */
	public static void start(Context _context, boolean _startLogWatcher) {
		Intent intent = new Intent(_context, CallService.class);
		intent.putExtra(START_LOG_WATCHER_KEY, _startLogWatcher);
		_context.startService(intent);
	}

	/**
	 * Gracefully stop service
	 * 
	 * @param _context
	 *            application context
	 */
	public static void stop(Context _context) {
		_context.stopService(new Intent(_context, CallService.class));
	}

	private void startLogWatcher() {
		logThread = new LogThread();
		logThread.start();
	}

	private void startTimer() {
		// find interval vibration
		callTimeIntervalVibrationID = App.getTriggerManager().getVibrationID(
				Trigger.CALL_TIME_INTERVAL);

		if (callTimeIntervalVibrationID != VibrationsManager.NO_VIBRATION_ID) {
			// remember start timer time
			callStartTime = SystemClock.elapsedRealtime();
			// start timer
			handler.sendEmptyMessage(0);
		} else {
			// no vibration nothing to do so stop self
			stopSelf();
		}
	}

	private void onTimerTick() {
		// find difference between real time and start time in seconds
		long diff = Math.round((SystemClock.elapsedRealtime() - callStartTime) / 1000.0);
		if (App.DEBUG) {
			Log.d("CallService.onTimerTick", "diff = " + diff);
		}
		if (diff % 60 == Pref.minuteInterval) {
			if (App.DEBUG) {
				Log.d("CallService.onTimerTick", "play call time interval vibration, diff = "
						+ diff);
			}
			VibrationService.start(getApplicationContext(), callTimeIntervalVibrationID, false, -1);
		}
	}

	private void onPick() {
		if (App.DEBUG) {
			Log.d("CallService.onPick", "picked up, starting timer");
		}
		int pickedUpPhoneVibrationID = App.getTriggerManager().getVibrationID(
				Trigger.PICK_UP_THE_PHONE);
		if (pickedUpPhoneVibrationID != VibrationsManager.NO_VIBRATION_ID) {
			if (App.DEBUG) {
				Log.d("CallService.onPick", "play picked up vibration");
			}
			VibrationService.start(getApplicationContext(), pickedUpPhoneVibrationID, false, -1);
		}

		startTimer();
	}

	// ============================================================================================
	// INTERNAL CLASSES
	// ============================================================================================
	/**
	 * Timer event handler
	 */
	private static class TimerHandler extends Handler {
		private WeakReference<CallService> mTarget;

		private TimerHandler(CallService _target) {
			mTarget = new WeakReference<CallService>(_target);
		}

		@Override
		public void handleMessage(Message msg) {
			CallService target = mTarget.get();
			if (target == null) {
				return;
			}
			target.onTimerTick();
			sendEmptyMessageDelayed(0, 1000);
			super.handleMessage(msg);
		}
	}

	private class LogThread extends Thread {
		private boolean isRunning = true;
		private long startTime;

		public void kill() {
			isRunning = false;
		}

		@Override
		public final void run() {
			if (App.DEBUG) {
				Log.d("CallService.LogThread.run", "Thread started");
			}
			super.run();

			String str;
			Process logcat = null;
			InputStreamReader streamReader = null;
			BufferedReader bufReader = null;

			try {
				startTime = SystemClock.elapsedRealtime();
				logcat = Runtime.getRuntime().exec("logcat -b radio");
				streamReader = new InputStreamReader(logcat.getInputStream());
				bufReader = new BufferedReader(streamReader);

				while (isRunning) {
					str = bufReader.readLine();
					if (App.DEBUG) {
						// used to log logcat =)
						// Log.d("LOGCAT", str);
					}
					if (str.contains("GET_CURRENT_CALLS") && str.contains("ACTIVE")) {
						// ACTIVE string arrives too fast, so it seems like old
						// log message has been read
						if (SystemClock.elapsedRealtime() - startTime < 1000) {
							continue;
						}
						onPick();
						break;
					}
				}
			} catch (Exception e) {
				/* doesn't matter */
			} finally {
				if (logcat != null) {
					logcat.destroy();
				}

				try {
					Runtime.getRuntime().exec("logcat -c");
				} catch (IOException e) {
					/* doesn't matter */
				}

				if (App.DEBUG) {
					Log.d("CallService.LogThread.run", "Thread finished");
				}
			}
		}
	}
}