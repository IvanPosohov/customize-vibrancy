package ru.ivanp.vibro.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/**
 * Checkable layout for SelectTriggerActivity list item
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private CheckedTextView checkedTextView;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		int childCount = getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View v = getChildAt(i);
			if (v instanceof CheckedTextView) {
				checkedTextView = (CheckedTextView) v;
			}
		}
	}

	@Override
	public boolean isChecked() {
		return checkedTextView != null ? checkedTextView.isChecked() : false;

	}

	@Override
	public void setChecked(boolean checked) {
		if (checkedTextView != null) {
			checkedTextView.setChecked(checked);
		}
	}

	@Override
	public void toggle() {
		if (checkedTextView != null) {
			checkedTextView.toggle();
		}
	}
}