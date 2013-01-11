package ru.ivanp.vibro.vibrations;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.EventDispatcher;

import com.immersion.uhl.IVTBuffer;
import com.immersion.uhl.Launcher;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Vibration manager. Provide list of vibrations and typical actions on it
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 * 
 */
public class VibrationsManager extends EventDispatcher {
	// ==========================================================================
	// CONSTANTS
	// ==========================================================================
	public static final int NO_VIBRATION_ID = -1;

	/**
	 * Actually it's just a first ID of first default Customize Vibrancy
	 * vibration (first 124 numbers represent Immersion default vibration
	 * identifiers)
	 */
	private static final int MAGIC_NUMBER = 124;

	/**
	 * Name of default vibrations file stored in assets
	 */
	private static final String IVT_FILENAME = "melodies.ivt";

	/**
	 * Name of user vibrations file in local storage
	 */
	private static final String USER_FILENAME = "melodies.dat";

	// ==========================================================================
	// FIELDS
	// ==========================================================================
	private Context context;
	private ArrayList<Vibration> vibrations;
	private IVTBuffer ivtBuffer;

	// ==========================================================================
	// GETTERS
	// ==========================================================================
	public ArrayList<Vibration> getVibrations() {
		return vibrations;
	}

	/**
	 * Returns vibrations list allowed to passed trigger
	 * 
	 * @param _triggerID
	 *            trigger identifier (use this class constants)
	 * @return list of vibrations
	 */
	public ArrayList<Vibration> getVibrations(int _triggerID) {
		// service and short by default
		byte type = Vibration.TYPE_SERVICE | Vibration.TYPE_SHORT;
		switch (_triggerID) {
		case Trigger.INCOMING_CALL:
			// all
			type |= Vibration.TYPE_ALL;
			break;
		case Trigger.INCOMING_SMS:
			// all, excluding TYPE_INFINITY
			type |= Vibration.TYPE_ALL & ~Vibration.TYPE_INFINITY;
			break;
		}
		
		// select vibrations by type
		ArrayList<Vibration> res = new ArrayList<Vibration>();
		for (Vibration vibration : vibrations) {
			if ((vibration.type & type) == vibration.type) {
				res.add(vibration);
			}
		}
		return res;
	}

	public IVTBuffer getIVTBuffer() {
		return ivtBuffer;
	}

	// ==========================================================================
	// CONSTRUCTOR
	// ==========================================================================
	public VibrationsManager(Context _context) {
		context = _context;
		vibrations = new ArrayList<Vibration>();
		vibrations.add(new Vibration(NO_VIBRATION_ID, Vibration.TYPE_SERVICE,
				context.getString(R.string.do_not_vibrate)));
		loadIVTVibrations(_context);
		loadImmersionDefaultVibrations();
		loadUserVibrations(_context);
	}

	// ==========================================================================
	// METHODS
	// ==========================================================================
	public Vibration getVibration(int _id) {
		if (_id == NO_VIBRATION_ID)
			return null;
		for (Vibration vibration : vibrations) {
			if (vibration.id == _id)
				return vibration;
		}
		return null;
	}

	private void loadImmersionDefaultVibrations() {
		Resources res = context.getResources();
		vibrations.add(new Vibration(Launcher.SHARP_CLICK_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.sharp_click), 100)));
		vibrations.add(new Vibration(Launcher.SHARP_CLICK_66,Vibration.TYPE_SHORT, String.format(res.getString(R.string.sharp_click), 66)));
		vibrations.add(new Vibration(Launcher.SHARP_CLICK_33,Vibration.TYPE_SHORT, String.format(res.getString(R.string.sharp_click), 33)));
		vibrations.add(new Vibration(Launcher.STRONG_CLICK_100,Vibration.TYPE_SHORT, String.format(res.getString(R.string.strong_click), 100)));
		vibrations.add(new Vibration(Launcher.STRONG_CLICK_66,Vibration.TYPE_SHORT, String.format(res.getString(R.string.strong_click), 66)));
		vibrations.add(new Vibration(Launcher.STRONG_CLICK_33,Vibration.TYPE_SHORT, String.format(res.getString(R.string.strong_click), 33)));
		vibrations.add(new Vibration(Launcher.BUMP_100, Vibration.TYPE_SHORT,String.format(res.getString(R.string.bump), 100)));
		vibrations.add(new Vibration(Launcher.BUMP_66, Vibration.TYPE_SHORT,String.format(res.getString(R.string.bump), 66)));
		vibrations.add(new Vibration(Launcher.BUMP_33, Vibration.TYPE_SHORT,String.format(res.getString(R.string.bump), 33)));
		vibrations.add(new Vibration(Launcher.BOUNCE_100, Vibration.TYPE_SHORT,String.format(res.getString(R.string.bounce), 100)));
		vibrations.add(new Vibration(Launcher.BOUNCE_66, Vibration.TYPE_SHORT,String.format(res.getString(R.string.bounce), 66)));
		vibrations.add(new Vibration(Launcher.BOUNCE_33, Vibration.TYPE_SHORT,String.format(res.getString(R.string.bounce), 33)));
		vibrations.add(new Vibration(Launcher.DOUBLE_SHARP_CLICK_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_sharp_click), 100)));
		vibrations.add(new Vibration(Launcher.DOUBLE_SHARP_CLICK_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_sharp_click), 66)));
		vibrations.add(new Vibration(Launcher.DOUBLE_SHARP_CLICK_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_sharp_click), 33)));
		vibrations.add(new Vibration(Launcher.DOUBLE_STRONG_CLICK_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_strong_click), 100)));
		vibrations.add(new Vibration(Launcher.DOUBLE_STRONG_CLICK_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_strong_click), 66)));
		vibrations.add(new Vibration(Launcher.DOUBLE_STRONG_CLICK_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_strong_click), 33)));
		vibrations.add(new Vibration(Launcher.DOUBLE_BUMP_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_bump), 100)));
		vibrations.add(new Vibration(Launcher.DOUBLE_BUMP_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_bump), 66)));
		vibrations.add(new Vibration(Launcher.DOUBLE_BUMP_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.double_bump), 33)));
		vibrations.add(new Vibration(Launcher.TRIPLE_STRONG_CLICK_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.triple_strong_click), 100)));
		vibrations.add(new Vibration(Launcher.TRIPLE_STRONG_CLICK_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.triple_strong_click), 66)));
		vibrations.add(new Vibration(Launcher.TRIPLE_STRONG_CLICK_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.triple_strong_click), 33)));
		vibrations.add(new Vibration(Launcher.TICK_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.tick), 100)));
		vibrations.add(new Vibration(Launcher.TICK_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.tick), 66)));
		vibrations.add(new Vibration(Launcher.TICK_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.tick), 33)));
		vibrations.add(new Vibration(Launcher.LONG_BUZZ_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_buzz), 100)));
		vibrations.add(new Vibration(Launcher.LONG_BUZZ_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_buzz), 66)));
		vibrations.add(new Vibration(Launcher.LONG_BUZZ_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_buzz), 33)));
		vibrations.add(new Vibration(Launcher.SHORT_BUZZ_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_buzz), 100)));
		vibrations.add(new Vibration(Launcher.SHORT_BUZZ_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_buzz), 66)));
		vibrations.add(new Vibration(Launcher.SHORT_BUZZ_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_buzz), 33)));
		vibrations.add(new Vibration(Launcher.LONG_TRANSITION_RAMP_UP_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_transition_ramp_up), 100)));
		vibrations.add(new Vibration(Launcher.LONG_TRANSITION_RAMP_UP_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_transition_ramp_up), 66)));
		vibrations.add(new Vibration(Launcher.LONG_TRANSITION_RAMP_UP_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_transition_ramp_up), 33)));
		vibrations.add(new Vibration(Launcher.SHORT_TRANSITION_RAMP_UP_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_transition_ramp_up), 100)));
		vibrations.add(new Vibration(Launcher.SHORT_TRANSITION_RAMP_UP_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_transition_ramp_up), 66)));
		vibrations.add(new Vibration(Launcher.SHORT_TRANSITION_RAMP_UP_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_transition_ramp_up), 33)));
		vibrations.add(new Vibration(Launcher.LONG_TRANSITION_RAMP_DOWN_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_transition_ramp_down), 100)));
		vibrations.add(new Vibration(Launcher.LONG_TRANSITION_RAMP_DOWN_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_transition_ramp_down), 66)));
		vibrations.add(new Vibration(Launcher.LONG_TRANSITION_RAMP_DOWN_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.long_transition_ramp_down), 33)));
		vibrations.add(new Vibration(Launcher.SHORT_TRANSITION_RAMP_DOWN_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_transition_ramp_down), 100)));
		vibrations.add(new Vibration(Launcher.SHORT_TRANSITION_RAMP_DOWN_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_transition_ramp_down), 66)));
		vibrations.add(new Vibration(Launcher.SHORT_TRANSITION_RAMP_DOWN_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.short_transition_ramp_down), 33)));
		vibrations.add(new Vibration(Launcher.FAST_PULSE_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.fast_pulse), 100)));
		vibrations.add(new Vibration(Launcher.FAST_PULSE_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.fast_pulse), 66)));
		vibrations.add(new Vibration(Launcher.FAST_PULSE_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.fast_pulse), 33)));
		vibrations.add(new Vibration(Launcher.FAST_PULSING_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.fast_pulsing), 100)));
		vibrations.add(new Vibration(Launcher.FAST_PULSING_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.fast_pulsing), 66)));
		vibrations.add(new Vibration(Launcher.FAST_PULSING_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.fast_pulsing), 33)));
		vibrations.add(new Vibration(Launcher.SLOW_PULSE_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.slow_pulse), 100)));
		vibrations.add(new Vibration(Launcher.SLOW_PULSE_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.slow_pulse), 66)));
		vibrations.add(new Vibration(Launcher.SLOW_PULSE_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.slow_pulse), 33)));
		vibrations.add(new Vibration(Launcher.SLOW_PULSING_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.slow_pulsing), 100)));
		vibrations.add(new Vibration(Launcher.SLOW_PULSING_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.slow_pulsing), 66)));
		vibrations.add(new Vibration(Launcher.SLOW_PULSING_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.slow_pulsing), 33)));
		vibrations.add(new Vibration(Launcher.TRANSITION_BUMP_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.transition_bump), 100)));
		vibrations.add(new Vibration(Launcher.TRANSITION_BUMP_66,Vibration.TYPE_SHORT, String.format(res.getString(R.string.transition_bump), 66)));
		vibrations.add(new Vibration(Launcher.TRANSITION_BUMP_33,Vibration.TYPE_SHORT, String.format(res.getString(R.string.transition_bump), 33)));
		vibrations.add(new Vibration(Launcher.TRANSITION_BOUNCE_100,Vibration.TYPE_SHORT, String.format(res.getString(R.string.transition_bounce), 100)));
		vibrations.add(new Vibration(Launcher.TRANSITION_BOUNCE_66,Vibration.TYPE_SHORT, String.format(res.getString(R.string.transition_bounce), 66)));
		vibrations.add(new Vibration(Launcher.TRANSITION_BOUNCE_33,Vibration.TYPE_SHORT, String.format(res.getString(R.string.transition_bounce), 33)));
		vibrations.add(new Vibration(Launcher.ALERT1, Vibration.TYPE_LONG, String.format(res.getString(R.string.alert), 1)));
		vibrations.add(new Vibration(Launcher.ALERT2, Vibration.TYPE_LONG, String.format(res.getString(R.string.alert), 2)));
		vibrations.add(new Vibration(Launcher.ALERT3, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.alert), 3)));
		vibrations.add(new Vibration(Launcher.ALERT4, Vibration.TYPE_LONG, String.format(res.getString(R.string.alert), 4)));
		vibrations.add(new Vibration(Launcher.ALERT5, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.alert), 5)));
		vibrations.add(new Vibration(Launcher.ALERT6, Vibration.TYPE_LONG, String.format(res.getString(R.string.alert), 6)));
		vibrations.add(new Vibration(Launcher.ALERT7, Vibration.TYPE_LONG, String.format(res.getString(R.string.alert), 7)));
		vibrations.add(new Vibration(Launcher.ALERT8, Vibration.TYPE_LONG, String.format(res.getString(R.string.alert), 8)));
		vibrations.add(new Vibration(Launcher.ALERT9, Vibration.TYPE_LONG, String.format(res.getString(R.string.alert), 9)));
		vibrations.add(new Vibration(Launcher.ALERT10, Vibration.TYPE_LONG, String.format(res.getString(R.string.alert), 10)));
		vibrations.add(new Vibration(Launcher.EXPLOSION1, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 1)));
		vibrations.add(new Vibration(Launcher.EXPLOSION2, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 2)));
		vibrations.add(new Vibration(Launcher.EXPLOSION3, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 3)));
		vibrations.add(new Vibration(Launcher.EXPLOSION4, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 4)));
		vibrations.add(new Vibration(Launcher.EXPLOSION5, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 5)));
		vibrations.add(new Vibration(Launcher.EXPLOSION6, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 6)));
		vibrations.add(new Vibration(Launcher.EXPLOSION7, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 7)));
		vibrations.add(new Vibration(Launcher.EXPLOSION8, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 8)));
		vibrations.add(new Vibration(Launcher.EXPLOSION9, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 9)));
		vibrations.add(new Vibration(Launcher.EXPLOSION10, Vibration.TYPE_SHORT, String.format(res.getString(R.string.explosion), 10)));
		vibrations.add(new Vibration(Launcher.WEAPON1, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.weapon), 1)));
		vibrations.add(new Vibration(Launcher.WEAPON2, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.weapon), 2)));
		vibrations.add(new Vibration(Launcher.WEAPON3, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.weapon), 3)));
		vibrations.add(new Vibration(Launcher.WEAPON4, Vibration.TYPE_SHORT, String.format(res.getString(R.string.weapon), 4)));
		vibrations.add(new Vibration(Launcher.WEAPON5, Vibration.TYPE_SHORT, String.format(res.getString(R.string.weapon), 5)));
		vibrations.add(new Vibration(Launcher.WEAPON6, Vibration.TYPE_SHORT, String.format(res.getString(R.string.weapon), 6)));
		vibrations.add(new Vibration(Launcher.WEAPON7, Vibration.TYPE_LONG, String.format(res.getString(R.string.weapon), 7)));
		vibrations.add(new Vibration(Launcher.WEAPON8, Vibration.TYPE_LONG, String.format(res.getString(R.string.weapon), 8)));
		vibrations.add(new Vibration(Launcher.WEAPON9, Vibration.TYPE_LONG, String.format(res.getString(R.string.weapon), 9)));
		vibrations.add(new Vibration(Launcher.WEAPON10, Vibration.TYPE_LONG, String.format(res.getString(R.string.weapon), 10)));
		vibrations.add(new Vibration(Launcher.IMPACT_WOOD_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_wood), 100)));
		vibrations.add(new Vibration(Launcher.IMPACT_WOOD_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_wood), 66)));
		vibrations.add(new Vibration(Launcher.IMPACT_WOOD_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_wood), 33)));
		vibrations.add(new Vibration(Launcher.IMPACT_METAL_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_metal), 100)));
		vibrations.add(new Vibration(Launcher.IMPACT_METAL_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_metal), 66)));
		vibrations.add(new Vibration(Launcher.IMPACT_METAL_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_metal), 33)));
		vibrations.add(new Vibration(Launcher.IMPACT_RUBBER_100, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_rubber), 100)));
		vibrations.add(new Vibration(Launcher.IMPACT_RUBBER_66, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_rubber), 66)));
		vibrations.add(new Vibration(Launcher.IMPACT_RUBBER_33, Vibration.TYPE_SHORT, String.format(res.getString(R.string.impact_rubber), 33)));
		vibrations.add(new Vibration(Launcher.TEXTURE1, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 1)));
		vibrations.add(new Vibration(Launcher.TEXTURE2, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 2)));
		vibrations.add(new Vibration(Launcher.TEXTURE3, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 3)));
		vibrations.add(new Vibration(Launcher.TEXTURE4, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 4)));
		vibrations.add(new Vibration(Launcher.TEXTURE5, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 5)));
		vibrations.add(new Vibration(Launcher.TEXTURE6, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 6)));
		vibrations.add(new Vibration(Launcher.TEXTURE7, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 7)));
		vibrations.add(new Vibration(Launcher.TEXTURE8, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 8)));
		vibrations.add(new Vibration(Launcher.TEXTURE9, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 9)));
		vibrations.add(new Vibration(Launcher.TEXTURE10, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.textture), 10)));
		vibrations.add(new Vibration(Launcher.ENGINE1_100, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine1), 100)));
		vibrations.add(new Vibration(Launcher.ENGINE1_66, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine1), 66)));
		vibrations.add(new Vibration(Launcher.ENGINE1_33, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine1), 33)));
		vibrations.add(new Vibration(Launcher.ENGINE2_100, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine2), 100)));
		vibrations.add(new Vibration(Launcher.ENGINE2_66, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine2), 66)));
		vibrations.add(new Vibration(Launcher.ENGINE2_33, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine2), 33)));
		vibrations.add(new Vibration(Launcher.ENGINE3_100, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine3), 100)));
		vibrations.add(new Vibration(Launcher.ENGINE3_66, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine3), 66)));
		vibrations.add(new Vibration(Launcher.ENGINE3_33, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine3), 33)));
		vibrations.add(new Vibration(Launcher.ENGINE4_100, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine4), 100)));
		vibrations.add(new Vibration(Launcher.ENGINE4_66, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine4), 66)));
		vibrations.add(new Vibration(Launcher.ENGINE4_33, Vibration.TYPE_INFINITY, String.format(res.getString(R.string.engine4), 33)));
	}

	private void loadIVTVibrations(Context _context) {
		// load from assets file
		AssetManager manager = _context.getAssets();
		InputStream is = null;
		ByteArrayOutputStream byteBuffer = null;

		try {
			is = manager.open(IVT_FILENAME);
			byteBuffer = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			/*
			 * we need to know how may bytes were read to write them to the
			 * byteBuffer
			 */
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, len);
			}
		} catch (IOException e) {
			/* doesn't matter */
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				/* doesn't matter */
			}

			try {
				byteBuffer.close();
			} catch (Exception e) {
				/* doesn't matter */
			}
		}

		ivtBuffer = new IVTBuffer(byteBuffer.toByteArray());

		// add to list
		vibrations.add(new IVTVibration(MAGIC_NUMBER, Vibration.TYPE_LONG, "Final Countdown", 0)); 
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 1, Vibration.TYPE_LONG, "Ghostbusters", 1));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 2, Vibration.TYPE_LONG, "Happy Birthday", 2));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 3, Vibration.TYPE_LONG, "Jingle Bells", 3));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 4, Vibration.TYPE_LONG, "La Cucaracha", 4));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 5, Vibration.TYPE_LONG, "Mission Impossible", 5));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 6, Vibration.TYPE_LONG, "Pink Panther", 6));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 7, Vibration.TYPE_LONG, "Satisfaction", 7));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 8, Vibration.TYPE_LONG, "Smoke On The Wather", 8));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 9, Vibration.TYPE_LONG, "Super Mario", 9));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 10, Vibration.TYPE_LONG, "Take A Look Around", 10));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 11, Vibration.TYPE_LONG, "Imperial March", 11));
		Resources res = context.getResources();
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 12, Vibration.TYPE_LONG, String.format(res.getString(R.string.ramp_up), 3), 25));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 13, Vibration.TYPE_LONG, String.format(res.getString(R.string.ramp_up), 5), 26));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 14, Vibration.TYPE_LONG, String.format(res.getString(R.string.ramp_up), 10), 27));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 15, Vibration.TYPE_LONG, String.format(res.getString(R.string.fast_periodic_ramp_up), 3), 28));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 16, Vibration.TYPE_LONG, String.format(res.getString(R.string.fast_periodic_ramp_up), 5), 29));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 17, Vibration.TYPE_LONG, String.format(res.getString(R.string.fast_periodic_ramp_up), 10), 30));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 18, Vibration.TYPE_LONG, String.format(res.getString(R.string.slow_periodic_ramp_up), 3), 31));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 19, Vibration.TYPE_LONG, String.format(res.getString(R.string.slow_periodic_ramp_up), 5), 32));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 20, Vibration.TYPE_LONG, String.format(res.getString(R.string.slow_periodic_ramp_up), 10), 33));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 21, Vibration.TYPE_INFINITY, res.getString(R.string.ramp_up_and_keep), 34));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 22, Vibration.TYPE_INFINITY, res.getString(R.string.fast_periodic_ramp_up_keep), 35));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 23, Vibration.TYPE_INFINITY, res.getString(R.string.slow_periodic_ramp_up_keep), 36));
		
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 24, Vibration.TYPE_LONG, String.format(res.getString(R.string.ramp_up_ramp_down), 3), 37));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 25, Vibration.TYPE_LONG, String.format(res.getString(R.string.ramp_up_ramp_down), 5), 38));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 26, Vibration.TYPE_LONG, String.format(res.getString(R.string.ramp_up_ramp_down), 10), 39));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 27, Vibration.TYPE_LONG, String.format(res.getString(R.string.fast_periodic_ramp_up_ramp_down), 3), 40));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 28, Vibration.TYPE_LONG, String.format(res.getString(R.string.fast_periodic_ramp_up_ramp_down), 5), 41));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 29, Vibration.TYPE_LONG, String.format(res.getString(R.string.fast_periodic_ramp_up_ramp_down), 10), 42));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 30, Vibration.TYPE_LONG, String.format(res.getString(R.string.slow_periodic_ramp_up_ramp_down), 3), 43));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 31, Vibration.TYPE_LONG, String.format(res.getString(R.string.slow_periodic_ramp_up_ramp_down), 5), 44));
		vibrations.add(new IVTVibration(MAGIC_NUMBER + 32, Vibration.TYPE_LONG, String.format(res.getString(R.string.slow_periodic_ramp_up_ramp_down), 10), 45));
	}

	private void loadUserVibrations(Context _context) {
		FileInputStream fin = null;
		InputStreamReader reader = null;
		BufferedReader bufferedReader = null;

		try {
			fin = _context.openFileInput(USER_FILENAME);
			reader = new InputStreamReader(fin);
			bufferedReader = new BufferedReader(reader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] items = line.split(",");
				int id = Integer.valueOf(items[0]);
				String name = items[1];
				int length = Integer.valueOf(items[2]);
				int size = (items.length - 3) / 2;
				VibrationElement[] elements = new VibrationElement[size];
				int j = 0;
				for (int i = 3; i < items.length; i += 2) {
					VibrationElement element = new VibrationElement(
							Integer.valueOf(items[i]),
							Integer.valueOf(items[i + 1]));
					elements[j++] = element;
				}
				UserVibration vibration = new UserVibration(id, name, length,
						elements);
				vibrations.add(vibration);
			}
		} catch (Exception e) {
			/* doesn't matter */
		} finally {
			try {
				bufferedReader.close();
			} catch (Exception e) {
				/* doesn't matter */
			}

			try {
				reader.close();
			} catch (Exception e) {
				/* doesn't matter */
			}

			try {
				fin.close();
			} catch (Exception e) {
				/* doesn't matter */
			}
		}
	}

	/**
	 * Store user vibrations to file
	 */
	public void storeUserVibrations() {
		FileOutputStream fout = null;
		OutputStreamWriter writer = null;
		StringBuilder builder = new StringBuilder();

		try {
			fout = context.openFileOutput(USER_FILENAME, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(fout);

			// for each vibration
			for (int i = 0; i < vibrations.size(); i++) {
				UserVibration vibration = (UserVibration) vibrations.get(i);
				if (vibration == null)
					continue;
				// if current is user vibration
				builder.append(vibration.id).append(',').append(vibration.name)
						.append(',').append(vibration.getLength()).append(',');
				// for each vibration element
				for (int j = 0; j < vibration.getElements().length; j++) {
					VibrationElement element = vibration.getElements()[j];
					builder.append(element.duration).append(',')
							.append(element.magnitude).append(',');
				}
				builder.append('\n');
			}
			writer.append(builder.toString());
			writer.flush();
			fout.flush();
		} catch (Exception e) {
			/* doesn't matter */
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				/* doesn't matter */
			}
			try {
				fout.close();
			} catch (Exception e) {
				/* doesn't matter */
			}
		}
	}

	public int getEmptyId() {
		int res = NO_VIBRATION_ID;
		for (Vibration vibration : vibrations) {
			if (vibration.id > res) {
				res = vibration.id;
			}
		}
		return res + 1;
	}

	public void add(UserVibration _vibration) {
		vibrations.add(_vibration);
	}

	public void remove(int _id) {
		for (int i = 0; i < vibrations.size(); i++) {
			if (vibrations.get(i).id == _id) {
				vibrations.remove(i);
				storeUserVibrations();
				break;
			}
		}
	}
}