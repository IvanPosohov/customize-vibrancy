package ru.ivanp.vibro.vibrations;

/**
 * Trigger it is an event (e.g. incoming call, change of Internet available
 * etc.) on which application may react(vibrate).
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class Trigger {
	// ==============================================================================================
	// CONSTANTS
	// ==============================================================================================
	// Call
	/**
	 * Occurs when we got new incoming call
	 */
	public static final int INCOMING_CALL = 0;
	/**
	 * Occurs when other side pick up the phone (on outgoing call)
	 */
	public static final int PICK_UP_THE_PHONE = 1;
	/**
	 * Occurs when incoming or outgoing call ends
	 */
	public static final int CALL_FINISHED = 2;
	/**
	 * Occurs after specified interval each minute of the call
	 */
	public static final int CALL_TIME_INTERVAL = 3;
	/**
	 * Occurs after specified interval when we got missing call
	 */
	// public static final int MISSED_CALL = 4;
	// SMS
	/**
	 * Occurs when we got new incoming SMS
	 */
	public static final int INCOMING_SMS = 4;
	/**
	 * Occurs after specified interval when we got missing SMS
	 */
	// public static final int MISSED_SMS = 6;
	// Internet
	/**
	 * Occurs when Internet became unavailable
	 */
	public static final int INTERNET_UNAVAILABLE = 5;
	/**
	 * Occurs when Internet became available via Wi-Fi
	 */
	public static final int INTERNET_AVAILABLE_WIFI = 6;
	/**
	 * Occurs when Internet became available via GSM/3G etc.
	 */
	public static final int INTERNET_AVAILABLE_MOBILE = 7;

	// ==============================================================================================
	// FIELDS
	// ==============================================================================================
	/**
	 * Trigger identifier, one of this class constants
	 */
	public final int id;

	/**
	 * Human readable trigger name
	 */
	public final String name;

	/**
	 * Pattern identifier matched to trigger
	 */
	public int vibrationID;

	// ==============================================================================================
	// CONSTRUCTOR
	// ==============================================================================================
	/**
	 * Creates new instance of Trigger class
	 * 
	 * @param _id
	 *            Trigger identifier, one of this class constants
	 * @param _name
	 *            Human readable trigger name
	 * @param _vibrationID
	 *            Vibration identifier matched to trigger
	 */
	public Trigger(int _id, String _name, int _vibrationID) {
		id = _id;
		name = _name;
		vibrationID = _vibrationID;
	}
}