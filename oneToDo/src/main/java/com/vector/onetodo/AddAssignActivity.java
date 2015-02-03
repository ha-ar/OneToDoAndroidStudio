package com.vector.onetodo;

import android.app.Activity;
import android.os.Bundle;


public class AddAssignActivity extends Activity {

	public static final String TAG = AddAssignActivity.class.getSimpleName();


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task_assign);
	}

}
