package com.vector.onetodo.utils;

import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

public class ScaleAnimToShow extends ScaleAnimation {

	private View mView;

	private LayoutParams mLayoutParams;

	private int mMarginBottomFromY, mMarginBottomToY;

	private boolean mVanishAfter = false;

	public ScaleAnimToShow(float toX, float fromX, float toY, float fromY,
			int duration, View view, boolean vanishAfter) {
		super(fromX, toX, fromY, toY);
//		openLayout = view;
		setDuration(duration);
		mView = view;
		mVanishAfter = vanishAfter;
		mLayoutParams = (LayoutParams) view.getLayoutParams();
		mView.setVisibility(View.VISIBLE);
		int height = mView.getHeight();

		mMarginBottomFromY = 0;
		mMarginBottomToY = height;

	}

	@Override
	protected void applyTransformation(float interpolatedTime,
			Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			int newMarginBottom = (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime)
					- mMarginBottomToY;
			mLayoutParams.setMargins(mLayoutParams.leftMargin,
					mLayoutParams.topMargin, mLayoutParams.rightMargin,
					newMarginBottom);
			mView.getParent().requestLayout();
			// Log.v("CZ","newMarginBottom..." + newMarginBottom +
			// " , mLayoutParams.topMargin..." + mLayoutParams.topMargin);
		}
	}

}