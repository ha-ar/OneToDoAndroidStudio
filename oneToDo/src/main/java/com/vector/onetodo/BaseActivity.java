package com.vector.onetodo;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.androidquery.AQuery;
import com.vector.onetodo.db.gen.DaoMaster;
import com.vector.onetodo.db.gen.DaoSession;
/*
import com.vector.onetodo.db.gen.LabelName;
import com.vector.onetodo.db.gen.LabelNameDao;*/

public abstract class BaseActivity extends ActionBarActivity {

	public static AQuery aq, aqd, aq_menu;
	public static HashMap<Integer, String> pageName = new HashMap<Integer, String>(),
			pagename2 = new HashMap<Integer, String>(),
			typeName = new HashMap<Integer, String>();
	public static int ONE_DAY = 1000 * 60 * 60 * 24;
	public static int TWO_DAYS = 2 * ONE_DAY;
	public static final int TODAY = 0, TOMORROW = 1, UPCOMING = 2, Work = 0,
			Home = 1, Personal = 2, Studies = 3, Meetups = 4, Games = 5;

 
	Long id = null;
	static int check = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		aq = new AQuery(this);
 
		pageName.put(TODAY, "TODAY");
		pageName.put(TOMORROW, "TOMORROW");
		pageName.put(UPCOMING, "UPCOMING");

		typeName.put(0, "My Todo's");
		typeName.put(1, "Assigned Tasks");
		typeName.put(2, "Shared Tasks");
		typeName.put(3, "Delayed Tasks");
	}
}