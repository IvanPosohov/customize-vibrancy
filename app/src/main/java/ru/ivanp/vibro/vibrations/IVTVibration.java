package ru.ivanp.vibro.vibrations;

/**
 * Vibration pattern stored in IVT file in assets
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class IVTVibration extends Vibration {
	// ============================================================================================
	// FIELDS
	// ============================================================================================
	/**
	 * Identifier of effect in IVT file
	 */
	public final int ivtID;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	/**
	 * Creates new instance of Vibration class
	 * 
	 * @param _id
	 *            unique vibration identifier
	 * @param _type
	 *            type of vibration
	 * @param _name
	 *            human-readable vibration name
	 * @param _ivtID
	 *            vibration identifier in IVT file
	 */
	public IVTVibration(int _id, byte _type, String _name, int _ivtID) {
		super(_id, _type, _name);
		ivtID = _ivtID;
	}
}