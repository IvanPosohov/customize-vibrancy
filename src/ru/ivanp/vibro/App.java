package ru.ivanp.vibro;

import java.util.regex.Pattern;

import ru.ivanp.vibro.utils.Pref;
import ru.ivanp.vibro.vibrations.Player;
import ru.ivanp.vibro.vibrations.TriggerManager;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class App extends Application {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	/**
	 * Something like preprocessor directive, used for enable/disable unstable code or debugging
	 * features like logging. If true all debug stuff is enabled
	 */
	public static final boolean DEBUG = false;
	/**
	 * App version name
	 */
	public static String VERSION_NAME;
	/**
	 * App version code
	 */
	public static int VERSION_CODE;
	/**
	 * Email address validation regexp pattern
	 */
    public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

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
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
			VERSION_NAME = packageInfo.versionName;
			VERSION_CODE = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Pref.load(context);
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================
}
