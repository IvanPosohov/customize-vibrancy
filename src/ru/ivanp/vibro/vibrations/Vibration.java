package ru.ivanp.vibro.vibrations;

/**
 * Base class for all vibration patterns
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class Vibration {
	// ========================================================================
	// CONSTANTS
	// ========================================================================
	/**
	 * Type of vibrations includes all other types
	 */
	public static final byte TYPE_ALL = 127;
	
	/**
	 * Type of NO_VIBRATION
	 */
	public static final byte TYPE_SERVICE = 1;
	
	/**
	 * Type of short vibrations, used for triggers like CallFinished etc.
	 */
	public static final byte TYPE_SHORT = 2;
	
	/**
	 * Type of long vibrations, used for triggers like IncommingCall etc.
	 */
	public static final byte TYPE_LONG = 4;
	
	/**
	 * Type of infinity or too long (more than 10sec) vibrations, used only for IncommingCall trigger.
	 */
	public static final byte TYPE_INFINITY = 8;
	
	// ========================================================================
	// FIELDS
	// ========================================================================
	/**
	 * Returns unique vibration identifier
	 */
	public final int id;

	/**
	 * Vibration type, for opportunity to separate list of vibration views
	 */
	public final byte type;
	
	/**
	 * Human-readable vibration name
	 */
	protected String name;

	// ========================================================================
	// GETTERS
	// ========================================================================
	/**
	 * @return human-readable vibration name
	 */
	public String getName() {
		return name;
	}

	// ========================================================================
	// SETTERS
	// ========================================================================
	/**
	 * Set human-readable vibration name
	 * 
	 * @param _name
	 *            vibration name
	 */
	public void setName(String _name) {
		name = _name;
	}

	// ========================================================================
	// CONSTRUCTOR
	// ========================================================================
	/**
	 * Creates new instance of Vibration class
	 * 
	 * @param _id
	 *            unique Vibration identifier
	 * @param _type
	 *            type of vibration
	 * @param _name
	 *            human-readable vibration name
	 */
	public Vibration(int _id, byte _type, String _name) {
		id = _id;
		type = _type;
		name = _name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}