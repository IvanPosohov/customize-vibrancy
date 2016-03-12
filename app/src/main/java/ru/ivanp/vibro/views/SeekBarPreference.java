package ru.ivanp.vibro.views;

import ru.ivanp.vibro.R;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.content.Context;
import android.content.res.TypedArray;

/**
 * SeekBar preference widget
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 * 
 */
public class SeekBarPreference extends Preference {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	private static final int DEFAULT_MIN_VALUE = 1;
	private static final int DEFAULT_MAX_VALUE = 100;

	// ============================================================================================
	// FIELDS
	// ============================================================================================
    private OnSeekBarChangeListener outerListener;
	private int minValue;
	private int maxValue;
	private int defaultValue;
	private String currentValueText;
	private int value;

    private TextView valueTextView;

	// ============================================================================================
	// SETTERS
	// ============================================================================================
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener _listener) {
        outerListener = _listener;
    }

	public void setMinValue(int _value) {
		minValue = _value;
	}

	public void setMaxValue(int _value) {
		maxValue = _value;
	}

	public void setDefaultValue(int _value) {
		defaultValue = _value;
	}

	public void setCurrentValueText(String _value) {
		currentValueText = _value;
	}

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	public SeekBarPreference(Context context) {
		super(context);
		init(context, null);
	}

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context, attrs);
	}

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWidgetLayoutResource(R.layout.seekbar_preference);
        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
            minValue = arr.getInt(R.styleable.SeekBarPreference_minValue, DEFAULT_MIN_VALUE);
            maxValue = arr.getInt(R.styleable.SeekBarPreference_maxValue, DEFAULT_MAX_VALUE);
            currentValueText = arr.getString(R.styleable.SeekBarPreference_currentValueText);
            arr.recycle();
            defaultValue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "defaultValue", DEFAULT_MAX_VALUE);
        }
    }

	// ============================================================================================
	// LIFECYCLE
	// ============================================================================================
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        if (holder.itemView instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) holder.itemView;
            ll.setOrientation(LinearLayout.VERTICAL);
        }

        View frame = holder.findViewById(android.R.id.widget_frame);
        if (frame != null) {
            ViewGroup.LayoutParams lp = frame.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        value = getPersistedInt(defaultValue);
        persistInt(value);

        // setup value text view

        valueTextView = (TextView) holder.findViewById(R.id.text_value);
        valueTextView.setText(String.format(currentValueText, value));

        // setup minimum and maximum text views
        TextView text_min = (TextView) holder.findViewById(R.id.text_min);
        text_min.setText(String.valueOf(minValue));

        TextView text_max = (TextView) holder.findViewById(R.id.text_max);
        text_max.setText(String.valueOf(maxValue));

        // setup SeekBar
        SeekBar seekbar = (SeekBar) holder.findViewById(R.id.skbr);
        seekbar.setMax(maxValue - minValue);
        seekbar.setProgress(value - minValue);
        seekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    private final OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                value = progress + minValue;
                persistInt(value);
                valueTextView.setText(String.format(currentValueText, getPersistedInt(maxValue)));
            }
            if (outerListener != null) {
                outerListener.onProgressChanged(seekBar, progress, fromUser);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (outerListener != null) {
                outerListener.onStartTrackingTouch(seekBar);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (outerListener != null) {
                outerListener.onStopTrackingTouch(seekBar);
            }
        }
    };
}