package ru.ivanp.vibro;

import ru.ivanp.vibro.utils.Pref;
import ru.ivanp.vibro.vibrations.Player;
import ru.ivanp.vibro.vibrations.TriggerManager;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import android.app.Application;
import android.content.Context;

public class App extends Application {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	/**
	 * Used as "preprocessor directive" to enable event logging and other debug
	 * stuff
	 */
	public static final boolean DEBUG = false;

	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private static Context context;
	private static Player player;
	private static VibrationsManager vibrationsManager;
	private static TriggerManager triggerManager;

	// ============================================================================================
	// GETTERS
	// ============================================================================================
	public static Player getPlayer() {
		if (player == null) {
			player = new Player(context);
		}
		return player;
	}

	public static VibrationsManager getVibrationManager() {
		if (vibrationsManager == null) {
			vibrationsManager = new VibrationsManager(context);
		}
		return vibrationsManager;
	}

	public static TriggerManager getTriggerManager() {
		if (triggerManager == null) {
			triggerManager = new TriggerManager(context);
		}
		return triggerManager;
	}

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();
		Pref.load(context);
	}
}
