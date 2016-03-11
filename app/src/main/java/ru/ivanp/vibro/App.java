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
	 * Something like preprocessor directive, used for enable/disable unstable code or debugging
	 * features like logging. If true all debug stuff is enabled
	 */
	public static final boolean DEBUG = true;
	/**
	 * App version name
	 */
	public static String VERSION_NAME = BuildConfig.VERSION_NAME;

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
	// APPLICATION LIFECYCLE
	// ============================================================================================
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		Pref.load(context);
	}
}
