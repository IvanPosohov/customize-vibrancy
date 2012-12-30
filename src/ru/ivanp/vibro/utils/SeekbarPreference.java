package ru.ivanp.vibro.utils;

import ru.ivanp.vibro.R;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;

/**
 * SeekBar preference widget <br>
 * Main idea http://habrahabr.ru/post/114733/ with some my changes
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 *
 */
public class SeekbarPreference extends Preference implements
		OnSeekBarChangeListener {
	// ========================================================================
	// CONSTANTS
	// ========================================================================
	private static final int DEFAULT_MIN_VALUE = 1;
	private static final int DEFAULT_MAX_VALUE = 100;

	// ========================================================================
	// FIELDS
	// ========================================================================
	private int minValue;
	private int maxValue;
	private int defValue;
	private String currentValueText;

	private int value;

	private SeekBar seekbar;
	private TextView text_value;
	private OnSeekBarChangeListener outerListener;

	// ========================================================================
	// SETTERS
	// ========================================================================
	public void setMinValue(int _value) {
		minValue = _value;
	}

	public void setMaxValue(int _value) {
		maxValue = _value;
	}
	
	public void setDefaultValue(int _value) {
		defValue = _value;
	}

	public void setCurrentValueText(String _value) {
		currentValueText = _value;
	}

	// ========================================================================
	// CONSTRUCTOR
	// ========================================================================
	public SeekbarPreference(Context context) {
		super(context);
		setWidgetLayoutResource(R.layout.seekbar_preference);
	}

	public SeekbarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray arr = context.obtainStyledAttributes(attrs,
				R.styleable.ru_ivanp_vibro_SeekbarPreference);
		minValue = arr.getInt(
				R.styleable.ru_ivanp_vibro_SeekbarPreference_minValue,
				DEFAULT_MIN_VALUE);
		maxValue = arr.getInt(
				R.styleable.ru_ivanp_vibro_SeekbarPreference_maxValue,
				DEFAULT_MAX_VALUE);
		currentValueText = arr
				.getString(R.styleable.ru_ivanp_vibro_SeekbarPreference_currentValueText);
		arr.recycle();

		defValue = attrs.getAttributeIntValue(
				"http://schemas.android.com/apk/res/android", "defaultValue",
				DEFAULT_MAX_VALUE);
		setWidgetLayoutResource(R.layout.seekbar_preference);
	}

	// ========================================================================
	// OVERRIDDEN
	// ========================================================================
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		if (view instanceof LinearLayout) {
			LinearLayout ll = (LinearLayout) view;
			ll.setOrientation(LinearLayout.VERTICAL);
		}

		View frame = view.findViewById(android.R.id.widget_frame);
		if (frame != null) {
			ViewGroup.LayoutParams lp = frame.getLayoutParams();
			lp.width = ViewGroup.LayoutParams.FILL_PARENT;
		}

		value = getPersistedInt(defValue);
		persistInt(value);

		// setup value text view

		text_value = (TextView) view.findViewById(R.id.text_value);
		text_value.setText(String.format(currentValueText, value));

		// setup minimum and maximum text views
		TextView text_min = (TextView) view.findViewById(R.id.text_min);
		text_min.setText(String.valueOf(minValue));

		TextView text_max = (TextView) view.findViewById(R.id.text_max);
		text_max.setText(String.valueOf(maxValue));

		// setup SeekBar
		seekbar = (SeekBar) view.findViewById(R.id.skbr);
		seekbar.setMax(maxValue - minValue);
		seekbar.setProgress(value - minValue);
		seekbar.setOnSeekBarChangeListener(this);
	}

	public void onProgressChanged(SeekBar _seek, int _value, boolean _fromTouch) {
		if (_fromTouch) {
			value = _value + minValue;
			persistInt(value);
			text_value.setText(String.format(currentValueText,
					getPersistedInt(maxValue)));
		}
		if (outerListener != null) {
			outerListener.onProgressChanged(_seek, _value, _fromTouch);
		}
	}

	public void onStartTrackingTouch(SeekBar seek) {
		if (outerListener != null) {
			outerListener.onStartTrackingTouch(seek);
		}
	}

	public void onStopTrackingTouch(SeekBar seek) {
		if (outerListener != null) {
			outerListener.onStopTrackingTouch(seek);
		}
	}

	// ========================================================================
	// METHODS
	// ========================================================================
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
		outerListener = l;
	}
}