package ru.ivanp.vibro.vibrations;

import java.util.ArrayList;

/**
 * Helper for Morse translate operations. Use static fields and methods
 * 
 * <p>
 * <b>Note:</b> I used an international Morse code <a
 * href="http://en.wikipedia.org/wiki/Morse_code">(More info)</a>, it is
 * composed of five elements:
 * <ul>
 * <li>short mark, dot (·) - "dot duration" is one unit long
 * <li>longer mark, dash (-) - three units long
 * <li>inter-element gap between the dots and dashes within a character - one
 * dot duration or one unit long
 * <li>short gap (between letters) - three units long
 * <li>medium gap (between words) - seven units long
 * </ul>
 * Length of "one unit long" can be specified in settings
 * </p>
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public final class Morse {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	/**
	 * Dot character
	 */
	public static final char DOT = '.';

	/**
	 * Dash character
	 */
	public static final char DASH = '-';

	/**
	 * Space character
	 */
	public static final char SPACE = ' ';

	/**
	 * Count of time units in one dot
	 */
	public static final int DOT_LENGTH = 1;

	/**
	 * Count of time units in one dash
	 */
	public static final int DASH_LENGTH = 3;

	/**
	 * Count of time units in inter-element gap between the dots and dashes
	 * within a character
	 */
	public static final int INNER_GAP_LENGTH = 1;

	/**
	 * Count of time units in short gap (between letters)
	 */
	public static final int SHORT_GAP_LENGTH = 3;

	/**
	 * Count of time units in medium gap (between words)
	 */
	public static final int MEDIUM_GAP_LENGTH = 7;

	/**
	 * Array of available Morse characters
	 */
	public final static MorseCharacter[] characters = {
			new MorseCharacter('A', ".-", 1 * DOT_LENGTH + 1 * DASH_LENGTH + 1 * INNER_GAP_LENGTH),
			new MorseCharacter('B', "-...", 3 * DOT_LENGTH + 1 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('C', "-.-.", 2 * DOT_LENGTH + 2 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('D', "-..", 2 * DOT_LENGTH + 1 * DASH_LENGTH + 2 * INNER_GAP_LENGTH),
			new MorseCharacter('E', ".", 1 * DOT_LENGTH + 0 * DASH_LENGTH + 0 * INNER_GAP_LENGTH),
			new MorseCharacter('F', "..-.", 3 * DOT_LENGTH + 1 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('G', "--.", 1 * DOT_LENGTH + 2 * DASH_LENGTH + 2 * INNER_GAP_LENGTH),
			new MorseCharacter('H', "....", 4 * DOT_LENGTH + 0 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('I', "..", 2 * DOT_LENGTH + 0 * DASH_LENGTH + 1 * INNER_GAP_LENGTH),
			new MorseCharacter('J', ".---", 1 * DOT_LENGTH + 3 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('K', "-.-", 1 * DOT_LENGTH + 2 * DASH_LENGTH + 2 * INNER_GAP_LENGTH),
			new MorseCharacter('L', ".-..", 3 * DOT_LENGTH + 1 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('M', "--", 0 * DOT_LENGTH + 2 * DASH_LENGTH + 1 * INNER_GAP_LENGTH),
			new MorseCharacter('N', "-.", 1 * DOT_LENGTH + 1 * DASH_LENGTH + 1 * INNER_GAP_LENGTH),
			new MorseCharacter('O', "---", 0 * DOT_LENGTH + 3 * DASH_LENGTH + 2 * INNER_GAP_LENGTH),
			new MorseCharacter('P', ".--.", 2 * DOT_LENGTH + 2 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('Q', "--.-", 1 * DOT_LENGTH + 3 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('R', ".-.", 2 * DOT_LENGTH + 1 * DASH_LENGTH + 2 * INNER_GAP_LENGTH),
			new MorseCharacter('S', "...", 3 * DOT_LENGTH + 0 * DASH_LENGTH + 2 * INNER_GAP_LENGTH),
			new MorseCharacter('T', "-", 0 * DOT_LENGTH + 1 * DASH_LENGTH + 0 * INNER_GAP_LENGTH),
			new MorseCharacter('U', "..-", 2 * DOT_LENGTH + 1 * DASH_LENGTH + 2 * INNER_GAP_LENGTH),
			new MorseCharacter('V', "...-", 3 * DOT_LENGTH + 1 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('W', ".--", 1 * DOT_LENGTH + 2 * DASH_LENGTH + 2 * INNER_GAP_LENGTH),
			new MorseCharacter('X', "-..-", 2 * DOT_LENGTH + 2 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('Y', "-.--", 1 * DOT_LENGTH + 3 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('Z', "--..", 2 * DOT_LENGTH + 2 * DASH_LENGTH + 3 * INNER_GAP_LENGTH),
			new MorseCharacter('0', "-----", 0 * DOT_LENGTH + 5 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('1', ".----", 1 * DOT_LENGTH + 4 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('2', "..---", 2 * DOT_LENGTH + 3 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('3', "...--", 3 * DOT_LENGTH + 2 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('4', "....-", 4 * DOT_LENGTH + 1 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('5', ".....", 5 * DOT_LENGTH + 0 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('6', "-....", 4 * DOT_LENGTH + 1 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('7', "--...", 3 * DOT_LENGTH + 2 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('8', "---..", 2 * DOT_LENGTH + 3 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH),
			new MorseCharacter('9', "----.", 1 * DOT_LENGTH + 4 * DASH_LENGTH + 4
					* INNER_GAP_LENGTH) };

	// ============================================================================================
	// METHODS
	// ============================================================================================
	/**
	 * Gets time units count in specified char
	 * 
	 * @param _char
	 *            one of Morse characters
	 * @return time units count in specified char
	 */
	private static int getTimeUnits(char _char) {
		for (int i = 0; i < characters.length; i++) {
			if (characters[i].character == _char)
				return characters[i].timeUnitCount;
		}
		throw new IllegalArgumentException("Character must be one of Morse permitted characters");
	}

	/**
	 * Gets dot/dash representation of specified char
	 * 
	 * @param _char
	 *            one of Morse characters
	 * @return dot/dash representation of specified char
	 */
	private static String getTranscription(char _char) {
		for (int i = 0; i < characters.length; i++) {
			if (characters[i].character == _char)
				return characters[i].translation;
		}
		throw new IllegalArgumentException("Character must be one of Morse permitted characters");
	}

	/**
	 * Check if character is one of Morse permitted characters
	 * 
	 * @param _char
	 *            character to check
	 * @return true if character is one of Morse permitted characters, false
	 *         otherwise
	 */
	public static boolean isAvailable(char _char) {
		for (int i = 0; i < characters.length; i++) {
			if (characters[i].character == _char)
				return true;
		}
		return false;
	}

	/**
	 * Calculate time units count in string
	 * 
	 * @param _string
	 *            string to calculate
	 * @return count of time units in the string
	 */
	public static int stringToTimeUnits(String _string) {
		int res = 0;
		for (int i = 0; i < _string.length(); i++) {
			if (i > 0) {
				// add space between letters
				res += SHORT_GAP_LENGTH;
			}
			char character = _string.charAt(i);
			// add space if need
			if (character == SPACE) {
				res += MEDIUM_GAP_LENGTH;
				continue;
			}
			// add letter
			res += getTimeUnits(character);
		}
		return res;
	}

	/**
	 * Translate text into vibration pattern
	 * 
	 * @param _text
	 *            translating text
	 * @param _intensity
	 *            intensity of pattern vibration
	 * @param _timeUnitLong
	 *            long of one time unit in milliseconds
	 * @return vibration pattern
	 */
	public static ArrayList<VibrationElement> translate(String _text, int _intensity,
			int _timeUnitLong) {
		ArrayList<VibrationElement> res = new ArrayList<VibrationElement>();
		// for each letter in text
		for (int i = 0; i < _text.length(); i++) {
			if (i > 0) {
				// add short gap (between letters)
				res.add(new VibrationElement(_timeUnitLong * SHORT_GAP_LENGTH, 0));
			}
			char character = _text.charAt(i);
			// add medium gap (between words)
			if (character == SPACE) {
				res.add(new VibrationElement(_timeUnitLong * MEDIUM_GAP_LENGTH, 0));
				continue;
			}
			// gets dot/dash representation of the letter
			String transcription = getTranscription(character);
			// for each dot/dash in letter
			for (int j = 0; j < transcription.length(); j++) {
				if (j > 0) {
					// add inner-element gap between the dots and dashes within
					// a
					// character
					res.add(new VibrationElement(_timeUnitLong * INNER_GAP_LENGTH, 0));
				}
				char element = transcription.charAt(j);
				// add dot or dash
				if (element == DOT) {
					res.add(new VibrationElement(_timeUnitLong * DOT_LENGTH, _intensity));
				} else if (element == DASH) {
					res.add(new VibrationElement(_timeUnitLong * DASH_LENGTH, _intensity));
				}
			}
		}
		return res;
	}

	// ============================================================================================
	// INNER CLASSES
	// ============================================================================================
	/**
	 * Contains dot/dash representation of character and count of time units in
	 * char
	 * 
	 * @author Posohov Ivan (posohof@gmail.com)
	 * 
	 */
	public static class MorseCharacter {
		public final char character;
		public final String translation;
		public final int timeUnitCount;

		/**
		 * Creates new instance of MorseLetter class
		 * 
		 * @param _character
		 *            character
		 * @param _translation
		 *            dot/dash representation of character
		 * @param _timeUnitCount
		 *            count of time units in char
		 */
		public MorseCharacter(char _character, String _translation, int _timeUnitCount) {
			character = _character;
			translation = _translation;
			timeUnitCount = _timeUnitCount;
		}
	}
}