package ru.ivanp.vibro.utils;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class WeakEventHandler<T> extends Handler {
	private WeakReference<T> ownerReference;

	public WeakEventHandler(T _owner) {
		ownerReference = new WeakReference<T>(_owner);
	}

	@Override
	public void handleMessage(Message _msg) {
		T owner = ownerReference.get();
		if (owner != null) {
			handleEvent(owner, _msg.what, _msg.getData());
		}
	}

	/**
	 * Calls just after {@code handleMessage} if owner still alive (isn't null)
	 * @param _owner 	owner object
	 * @param _eventId 	equals {@code message.what}
	 * @param _data 	equals {@code message.data}
	 */
	abstract public void handleEvent(T _owner, int _eventId, Bundle _data);
}