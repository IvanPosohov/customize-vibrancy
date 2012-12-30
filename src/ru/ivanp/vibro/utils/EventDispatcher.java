package ru.ivanp.vibro.utils;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Base class for all event dispatching classes. This class allows any object to
 * be the event target.
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class EventDispatcher {
	// ========================================================================
	// FIELDS
	// ========================================================================
	/**
	 * List of event handlers. Event will be sent to all of them
	 */
	private ArrayList<Handler> listeners = new ArrayList<Handler>();

	// ========================================================================
	// METHODS
	// ========================================================================
	/**
	 * Register event listener
	 * 
	 * @param _handler
	 *            listener handler
	 */
	public synchronized void addEventListener(Handler _handler) {
		listeners.add(_handler);
	}

	/**
	 * Remove event listener
	 */
	public synchronized void removeEventListener(Handler _handler) {
		_handler.removeCallbacksAndMessages(null);
		listeners.remove(_handler);
	}

	/**
	 * Dispatch event
	 * 
	 * @param _what
	 *            event code, generally all codes defined in derived class
	 */
	public synchronized void dispatchEvent(int _what) {
		for (Handler item : listeners) {
			item.sendEmptyMessage(_what);
		}
	}

	/**
	 * Dispatch event
	 * 
	 * @param _what
	 *            event code, generally all codes defined in derived class
	 * @param _data
	 *            event data
	 */
	public synchronized void dispatchEvent(int _what, Bundle _data) {
		for (Handler item : listeners) {
			Message msg = Message.obtain(item, _what);
			msg.setData(_data);
			msg.sendToTarget();
		}
	}

	/**
	 * Dispatch event after delay
	 * 
	 * @param _what
	 *            event code, generally all codes defined in derived class
	 * @param _delay
	 *            delay in milliseconds
	 */
	public synchronized void dispatchEvent(int _what, long _delay) {
		for (Handler item : listeners) {
			item.sendEmptyMessageDelayed(_what, _delay);
		}
	}

	/**
	 * Remove delayed events of specified type from all event listeners
	 * 
	 * @param _what
	 *            event code, generally all codes defined in derived class
	 */
	protected void removeDelayedEvent(int _what) {
		for (Handler item : listeners) {
			item.removeMessages(_what);
		}
	}
}