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
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	/**
	 * Playing finished event code. Event with such code occurs when vibration
	 * stops (manually or in the end of not repeated pattern)
	 */
	public static final int EVENT_PLAYING_FINISHED = 100;
	/**
	 * Pattern finished event code. Event with such code occurs every time when
	 * pattern finished playing
	 */
	private static final int EVENT_PATTERN_FINISHED = 101;
	/**
	 * Element finished event code. Event with such code occurs when one of the
	 * user vibration element finished playing
	 */
	private static final int EVENT_ELEMENT_FINISHED = 102;

	// ============================================================================================
	// FIELDS
	// ============================================================================================
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

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	public Player(Context _context) {
		try {
			device = Device.newDevice(_context);
			launcher = new Launcher(_context);
			immVibe = ImmVibe.getInstance();
			effect = new MagSweepEffectDefinition(Integer.MAX_VALUE, 0, ImmVibe.VIBE_STYLE_SMOOTH,
					0, 0, 0, 0, 0);
		} catch (RuntimeException e) {
			Log.e("Player.init", "Create new device failed!", e);
		}
		handler = new LocalHandler(this);
		addEventListener(handler);
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================
	/**
	 * Stop vibration
	 */
	public void stop() {
		try {
			if (playing == null || playing instanceof IVTVibration
					|| playing instanceof UserVibration) {
				device.stopAllPlayingEffects();
			} else {
				launcher.stop();
			}
		} catch (RuntimeException e) {
			/* what could I do? nothing =( */
		}
		handler.removeMessages(EVENT_ELEMENT_FINISHED);
		handler.removeMessages(EVENT_PATTERN_FINISHED);
		repeat = false;
		effectHandle = null;
		playing = null;
		dispatchEvent(EVENT_PLAYING_FINISHED);
	}

	/**
	 * Enable vibration with passed magnitude, if vibration is disabled
	 */
	public void playMagnitude(int _magnitude) {
		effect.setMagnitude(_magnitude);
		if (effectHandle == null) {
			effectHandle = device.playMagSweepEffect(effect);
		} else {
			effectHandle.modifyPlayingMagSweepEffect(effect);
		}
	}

	/**
	 * If there aren't playing vibrations start to play passed. If passed
	 * vibration is matched to playing - stop playing. If passed vibration is
	 * not matched to playing - play passed vibration
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
	 * Play passed vibration in cycle until stop() call
	 */
	public void playRepeat(Vibration _vibration) {
		repeat = true;
		play(_vibration);
	}

	/**
	 * Play passed vibration, if passed is null playing will be stopped
	 */
	private void play(Vibration _vibration) {
		if (_vibration == null) {
			stop();
			// call play method according to vibration type
		} else if (_vibration instanceof IVTVibration) {
			playIVT((IVTVibration) _vibration);
		} else if (_vibration instanceof UserVibration) {
			playUser((UserVibration) _vibration);
		} else {
			playImmersion(_vibration);
		}
	}

	/**
	 * Play one of preset vibrations stored in IVT file
	 */
	private void playIVT(IVTVibration _ivtVibration) {
		playing = _ivtVibration;
		device.playIVTEffect(App.getVibrationManager().getIVTBuffer(), _ivtVibration.ivtID);
		int duration = App.getVibrationManager().getIVTBuffer()
				.getEffectDuration(_ivtVibration.ivtID);
		// fire event after playing finished
		handler.sendEmptyMessageDelayed(EVENT_PATTERN_FINISHED, duration);
	}

	/**
	 * Play one of user created vibrations stored in the file
	 */
	private void playUser(UserVibration _userVibration) {
		playing = _userVibration;
		/*
		 * user vibration are stored like array of [[magnitude,time]...]
		 * elements, so to play user vibration we just play each element of it
		 */
		playingElementIndex = 0;
		playingElementCount = _userVibration.getElements().length;
		playNextElement();
	}

	/**
	 * Find next element of playing user vibration and play it, or finish
	 * vibration if there are no more elements
	 */
	private void playNextElement() {
		if (playingElementIndex == playingElementCount) {
			handler.sendEmptyMessage(EVENT_PATTERN_FINISHED);
		} else {
			UserVibration vibration = (UserVibration) playing;
			VibrationElement element = vibration.getElements()[playingElementIndex];
			playMagnitude(element.magnitude);
			// fire event after element has played
			handler.sendEmptyMessageDelayed(EVENT_ELEMENT_FINISHED, element.duration);
		}
		playingElementIndex++;
	}

	/**
	 * Play Immersion preset vibration
	 */
	private void playImmersion(Vibration _vibration) {
		playing = _vibration;
		try {
			launcher.play(_vibration.id);
			int duration = immVibe.getUHLEffectDuration(_vibration.id);
			// fire event after playing finished
			handler.sendEmptyMessageDelayed(EVENT_PATTERN_FINISHED, duration);
		} catch (RuntimeException e) {
			/* doesn't matter */
		}
	}

	// ============================================================================================
	// INTERNAL CLASSES
	// ============================================================================================
	private static class LocalHandler extends Handler {
		private WeakReference<Player> mTarget;

		private LocalHandler(Player _target) {
			mTarget = new WeakReference<Player>(_target);
		}

		@Override
		public void handleMessage(Message msg) {
			Player target = mTarget.get();
			switch (msg.what) {
			case EVENT_PATTERN_FINISHED:
				/*
				 * if need to play pattern in cycle just call play() with
				 * current playing pattern argument, otherwise call stop()
				 */
				if (target.repeat) {
					target.play(target.playing);
				} else {
					target.stop();
				}
				break;
			case EVENT_ELEMENT_FINISHED:
				target.playNextElement();
				break;
			}
			super.handleMessage(msg);
		}
	}
}