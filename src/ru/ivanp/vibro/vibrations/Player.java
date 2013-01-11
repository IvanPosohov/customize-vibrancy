package ru.ivanp.vibro.vibrations;

import java.lang.ref.WeakReference;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.utils.EventDispatcher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.immersion.uhl.Device;
import com.immersion.uhl.EffectHandle;
import com.immersion.uhl.Launcher;
import com.immersion.uhl.MagSweepEffectDefinition;
import com.immersion.uhl.internal.ImmVibe;
import com.immersion.uhl.internal.ImmVibeAPI;

/**
 * Player for all vibration types
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class Player extends EventDispatcher {
	// ========================================================================
	// CONSTANTS
	// ========================================================================
	public static final int EVENT_PLAYING_FINISHED = 100;
	private static final int EVENT_CHANGE_MAGNITUDE = 101;

	// ========================================================================
	// FIELDS
	// ========================================================================
	private Device device;
	private Launcher launcher;
	private ImmVibeAPI immVibe;
	private MagSweepEffectDefinition effect;
	private EffectHandle effectHandle;
	private Handler handler;
	private Vibration playing;
	private boolean repeat;
	private int playingElementIndex;
	private int playingElementCount;

	// ========================================================================
	// GETTERS
	// ========================================================================
	public boolean getIsPlaying() {
		return playing != null;
	}

	public Vibration getPlayingVibration() {
		return playing;
	}

	// ========================================================================
	// CONSTRUCTOR
	// ========================================================================
	public Player(Context _context) {
		try {
			device = Device.newDevice(_context);
			launcher = new Launcher(_context);
			immVibe = ImmVibe.getInstance();
			effect = new MagSweepEffectDefinition(Integer.MAX_VALUE, 0,
					ImmVibe.VIBE_STYLE_SMOOTH, 0, 0, 0, 0, 0);
		} catch (RuntimeException e) {
			Log.e("Player.init", "Create new device failed!", e);
		}
		handler = new LocalHandler(this);
		addEventListener(handler);
	}

	// ========================================================================
	// METHODS
	// ========================================================================
	/**
	 * Stop vibration
	 */
	public void stop() {
		// stop play
		try {
			device.stopAllPlayingEffects();
		} catch (RuntimeException e) {
			if (App.DEBUG) {
				Log.d("Player.stop", "stopAllPlayingEffects RuntimeException", e);
			}
		}
		// remove delayed "playing finished" event
		removeDelayedEvent(EVENT_PLAYING_FINISHED);

		handler.removeMessages(EVENT_CHANGE_MAGNITUDE);
		effectHandle = null;
		playing = null;
		repeat = false;
	}

	// start vibrate
	public void play() {
		effectHandle = device.playMagSweepEffect(effect);
	}

	public void setMagnitude(int _magnitude) {
		effect.setMagnitude(_magnitude);
		if (effectHandle == null) {
			play();
		} else {
			effectHandle.modifyPlayingMagSweepEffect(effect);
		}
	}

	/**
	 * If there aren't playing vibrations start to play passed. If passed
	 * vibration is matched to playing - stop playing. If passed vibration is
	 * not matched to playing - play passed vibration
	 * 
	 * @param _vibration
	 */
	public void playOrStop(Vibration _vibration) {
		if (playing == null) {
			play(_vibration);
		} else {
			if (_vibration == playing) {
				stop();
			} else {
				stop();
				play(_vibration);
			}
		}
	}

	/**
	 * Play vibration in cycle until stop() call
	 * 
	 * @param _vibration
	 *            vibration to play
	 */
	public void playRepeat(Vibration _vibration) {
		repeat = true;
		play(_vibration);
	}

	/**
	 * Play or stop current playing vibration
	 * 
	 * @param _vibration
	 *            vibration to play
	 */
	private void play(Vibration _vibration) {
		/* for this vibration type we play one of default Immersion vibration */
		if (_vibration == null)
			stop();
		if (_vibration instanceof IVTVibration) {
			playIVT((IVTVibration) _vibration);
		} else if (_vibration instanceof UserVibration) {
			playUser((UserVibration) _vibration);
		} else {

			try {
				// start playing
				launcher.play(_vibration.id);
				// find duration of vibration
				int duration = immVibe.getUHLEffectDuration(_vibration.id);
				// fire event after playing finished
				dispatchEvent(EVENT_PLAYING_FINISHED, duration);
				playing = _vibration;
			} catch (RuntimeException e) {
				/* doesn't matter */
			}
		}
	}

	/**
	 * Play one of default vibrations stored in IVT file
	 */
	private void playIVT(IVTVibration _ivtVibration) {
		// start playing
		device.playIVTEffect(App.getVibrationManager().getIVTBuffer(),
				_ivtVibration.ivtID);
		// find duration of vibration
		int duration = App.getVibrationManager().getIVTBuffer()
				.getEffectDuration(_ivtVibration.ivtID);
		// fire event after playing finished
		dispatchEvent(EVENT_PLAYING_FINISHED, duration);
		playing = _ivtVibration;
	}

	/**
	 * Play one of user vibrations
	 */
	private void playUser(UserVibration _userVibration) {
		playingElementIndex = 0;
		playingElementCount = _userVibration.getElements().length;
		VibrationElement element = _userVibration.getElements()[playingElementIndex];
		effect.setMagnitude(element.magnitude);
		effectHandle = device.playMagSweepEffect(effect);
		handler.sendEmptyMessageDelayed(EVENT_CHANGE_MAGNITUDE,
				element.duration);
		playing = _userVibration;
	}

	private void onChangeMagnitude() {
		playingElementIndex++;
		if (playingElementIndex == playingElementCount) {
			if (repeat) {
				play(playing);
			} else {
				stop();
				dispatchEvent(EVENT_PLAYING_FINISHED);
			}
		} else {
			UserVibration vibration = (UserVibration) playing;
			VibrationElement element = vibration.getElements()[playingElementIndex];
			effect.setMagnitude(element.magnitude);
			effectHandle.modifyPlayingMagSweepEffect(effect);
			handler.sendEmptyMessageDelayed(EVENT_CHANGE_MAGNITUDE,
					element.duration);
		}
	}

	// ========================================================================
	// INTERNAL CLASS
	// ========================================================================
	private static class LocalHandler extends Handler {
		private WeakReference<Player> mTarget;

		private LocalHandler(Player _target) {
			mTarget = new WeakReference<Player>(_target);
		}

		@Override
		public void handleMessage(Message msg) {
			Player target = mTarget.get();
			switch (msg.what) {
			case EVENT_PLAYING_FINISHED:
				if (target.repeat) {
					target.play(target.playing);
				} else {
					target.playing = null;
				}
				break;
			case EVENT_CHANGE_MAGNITUDE:
				target.onChangeMagnitude();
				break;
			}
			super.handleMessage(msg);
		}
	}
}