package com.vector.onetodo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class CheckListEditText extends EditText {

	public CheckListEditText(Context context) {
        super(context);
        init();
    }
 
    public CheckListEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
 
    public CheckListEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
     
    void init(){
    	
    }
}
