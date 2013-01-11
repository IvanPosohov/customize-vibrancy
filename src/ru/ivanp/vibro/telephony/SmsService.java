package ru.ivanp.vibro.telephony;

import java.lang.ref.WeakReference;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.vibrations.Player;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Watching for unread SMS count
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SmsService extends Service {
	// ========================================================================
	// CONSTANTS
	// ========================================================================
	private final static Uri SMS_URI = Uri
			.parse("content://mms-sms/conversations/");
	private final static Uri SMS_INBOX = Uri.parse("content://sms/inbox");

	// ========================================================================
	// FIELDS
	// ========================================================================
	private ContentObserver observer;
	private ContentResolver resolver;
	private LocalHandler handler;

	// ========================================================================
	// OVERRIDDEN
	// ========================================================================
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handler = new LocalHandler(this);
		App.getPlayer().addEventListener(handler);

		// handle SMS count change
		if (observer == null) {
			observer = new ContentObserver(new Handler()) {
				@Override
				public void onChange(boolean selfChange) {
					super.onChange(selfChange);
					// if all SMS are read stop self
					int count = getUnreadSmsCount(getApplicationContext());
					if (App.DEBUG) {
						Log.d("SmsService.onStartCommand", "unreaded count="
								+ count);
					}
					if (count == 0) {
						VibrationService.stop(SmsService.this);
						stopSelf();
					}
				}
			};
			resolver = getContentResolver();
			resolver.registerContentObserver(SMS_URI, true, observer);
		}

		if (App.DEBUG) {
			Log.d("SmsService.onStartCommand", "Service started");
		}

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (resolver != null) {
			resolver.unregisterContentObserver(observer);
		}
		App.getPlayer().removeEventListener(handler);

		if (App.DEBUG) {
			Log.d("SmsService.onDestroy", "Service destroyed");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// ========================================================================
	// METHODS
	// ========================================================================
	/**
	 * Start service with needed arguments
	 * 
	 * @param _context
	 *            application context
	 */
	public static void start(Context _context) {
		Intent intent = new Intent(_context, SmsService.class);
		_context.startService(intent);
	}

	/**
	 * Returns unread SMS count
	 * 
	 * @param _context
	 *            application context
	 * @return unread SMS count
	 */
	synchronized private int getUnreadSmsCount(Context _context) {
		Cursor cursor = _context.getContentResolver().query(SMS_INBOX, null,
				"read = 0", null, null);
		int count = 0;
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	// ========================================================================
	// INTERNAL CLASSES
	// ========================================================================
	/**
	 * Local event handler
	 */
	private static class LocalHandler extends Handler {
		private WeakReference<SmsService> mTarget;

		private LocalHandler(SmsService _target) {
			mTarget = new WeakReference<SmsService>(_target);
		}

		@Override
		public void handleMessage(Message msg) {
			SmsService target = mTarget.get();
			if (target != null && msg.what == Player.EVENT_PLAYING_FINISHED) {
				target.stopSelf();
			}
			super.handleMessage(msg);
		}
	}
}