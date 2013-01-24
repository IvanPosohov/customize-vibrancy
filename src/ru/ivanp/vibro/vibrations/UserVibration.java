package ru.ivanp.vibro.vibrations;

import java.util.ArrayList;

/**
 * User-recorded vibration pattern. This vibration type is TYPE_LONG by default
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class UserVibration extends Vibration {
	// ============================================================================================
	// FIELDS
	// ============================================================================================
	/**
	 * Return pattern length in millisecond
	 */
	private int length;

	/**
	 * Array of vibration elements
	 */
	private VibrationElement[] elements;

	// ============================================================================================
	// GETTERS
	// ============================================================================================
	/**
	 * Return pattern length in millisecond
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Return array of vibration elements
	 */
	public VibrationElement[] getElements() {
		return elements;
	}

	// ============================================================================================
	// SETTERS
	// ============================================================================================
	/**
	 * Set array of vibration elements
	 */
	public void setElements(ArrayList<VibrationElement> _elements) {
		length = findLength(_elements);
		elements = _elements.toArray(new VibrationElement[_elements.size()]);
	}

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	/**
	 * Creates new instance of BasePattern
	 * 
	 * @param _id
	 *            unique vibration identifier
	 * @param _name
	 *            human-readable vibration name
	 * @param _length
	 *            vibration length in milliseconds
	 * @param _elements
	 *            array of vibration elements
	 */
	public UserVibration(int _id, String _name, int _length, VibrationElement[] _elements) {
		super(_id, Vibration.TYPE_LONG, _name);
		length = _length;
		elements = _elements;
	}

	/**
	 * Creates new instance of BasePattern
	 * 
	 * @param _id
	 *            unique vibration identifier
	 * @param _name
	 *            human-readable vibration name
	 */
	public UserVibration(int _id) {
		super(_id, Vibration.TYPE_LONG, "");
	}

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	/**
	 * Counts vibration length
	 * 
	 * @param _elements
	 *            array of vibration elements
	 * @return pattern length
	 */
	public static int findLength(ArrayList<VibrationElement> _elements) {
		int res = 0;
		for (VibrationElement patternElement : _elements) {
			res += patternElement.duration;
		}
		return res;
	}
}