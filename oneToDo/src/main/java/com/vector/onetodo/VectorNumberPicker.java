package com.vector.onetodo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class VectorNumberPicker extends android.widget.DatePicker {

	public VectorNumberPicker(Context context) {
		super(context);
	}

	public VectorNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VectorNumberPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		super.addView(child);
		updateView(child);
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		super.addView(child);
		updateView(child);
	}

	private void updateView(View view) {
		if (view instanceof EditText) {
			((EditText) view).setTextSize(25);
			((EditText) view).setTextColor(R.color.light_grey_color);
		}
	}

}
