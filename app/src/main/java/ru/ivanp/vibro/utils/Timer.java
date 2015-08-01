package ru.ivanp.vibro.utils;

import android.os.Bundle;

public class Timer {
	// ============================================================================================
	// INTERFACE
	// ============================================================================================
	public interface OnTimerTickListener {
		void onTimerTick();
	}

	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private OnTimerTickListener onTimerTickListener;
	private int interval;
	private boolean isRunning;
	private TimerHandler handler;

	// ============================================================================================
	// GETTERS
	// ============================================================================================
	public boolean isRunning() {
		return isRunning;
	}

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	public Timer(int _interval, OnTimerTickListener _listener) {
		interval = _interval;
		onTimerTickListener = _listener;
		handler = new TimerHandler(this);
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	/**
	 * Start timer. First tick after delay
	 */
	public void start() {
		if (!isRunning) {
			isRunning = true;
			handler.sendEmptyMessageDelayed(0, interval);
		}
	}

	/**
	 * Stop timer, no more tick after that
	 */
	public void stop() {
		isRunning = false;
		handler.removeMessages(0);
	}

	/**
	 * Restarts timer. If timer is running next tick after full interval, otherwise start timer
	 */
	public void restart() {
		handler.removeMessages(0);
		handler.sendEmptyMessageDelayed(0, interval);
		isRunning = true;
	}

	/**
	 * Set timer ticks interval. If timer is running next tick after new interval, otherwise start
	 * timer
	 * 
	 * @param _interval
	 *            interval between timer ticks
	 */
	public void setInterval(int _interval) {
		interval = _interval;
		restart();
	}

	// ============================================================================================
	// INTERNAL CLASSES
	// ============================================================================================
	private static class TimerHandler extends WeakEventHandler<Timer> {
		private TimerHandler(Timer _owner) {
			super(_owner);
		}

		@Override
		public void handleEvent(Timer _owner, int _eventId, Bundle _data) {
			if (_owner.isRunning) {
				// continue sending
				_owner.handler.sendEmptyMessageDelayed(0, _owner.interval);
				_owner.onTimerTickListener.onTimerTick();
			}
		}
	}
}