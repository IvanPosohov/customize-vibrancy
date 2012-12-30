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
	public static final int TYPE_ALL = 0;
	
	/**
	 * Type of short vibrations, used for triggers like CallFinished etc.
	 */
	public static final int TYPE_SHORT = 1;
	
	/**
	 * Type of long vibrations, used for triggers like IncommingCall etc.
	 */
	public static final int TYPE_LONG = 2;
	
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
	public final int type;
	
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
	public Vibration(int _id, int _type, String _name) {
		id = _id;
		type = _type;
		name = _name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}