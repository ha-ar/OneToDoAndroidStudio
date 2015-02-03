package com.vector.onetodo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import org.jetbrains.annotations.NotNull;

public class CustomScrollView extends ScrollView {

    private boolean enableScrolling = true;

    public boolean isEnableScrolling() {
        return enableScrolling;
    }

    public void setEnableScrolling(boolean enableScrolling) {
        this.enableScrolling = enableScrolling;
    }

    public CustomScrollView(@NotNull Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomScrollView(@NotNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(@NotNull Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(@NotNull MotionEvent ev) {

        if (isEnableScrolling()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }
}
