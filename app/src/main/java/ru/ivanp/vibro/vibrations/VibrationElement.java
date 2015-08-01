package ru.ivanp.vibro.vibrations;

/**
 * Contains information about vibration duration and strength. Each pattern
 * contains one or several VibrationElements
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class VibrationElement {
	// ============================================================================================
	// FIELDS
	// ============================================================================================
	/**
	 * Element duration in millisecond
	 */
	public final int duration;

	/**
	 * Element magnitude (strength of vibration)
	 */
	public final int magnitude;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	/**
	 * Create a VibrationElement instance
	 * 
	 * @param _duration
	 *            element duration in millisecond
	 * @param _magnitude
	 *            element magnitude (strength of vibration)
	 */
	public VibrationElement(int _duration, int _magnitude) {
		duration = _duration;
		magnitude = _magnitude;
	}

	/**
	 * Create a VibrationElement instance with null magnitude
	 * 
	 * @param _duration
	 *            element duration in millisecond
	 */
	public VibrationElement(int _duration) {
		duration = _duration;
		magnitude = 0;
	}

	@Override
	public String toString() {
		return String.format("%d,%d", duration, magnitude);
	}
}