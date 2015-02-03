package com.vector.onetodo;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.vector.onetodo.db.gen.DaoMaster;
import com.vector.onetodo.db.gen.DaoSession;
import com.vector.onetodo.db.gen.DaoMaster.DevOpenHelper;
import com.vector.onetodo.utils.AppPrefs;

public class App extends Application{
	
	/**
     * Application preferences *
     */
    public static AppPrefs prefs;
    public static DevOpenHelper ex_database_helper_obj;
	public static SQLiteDatabase ex_db;
	public static DaoSession daoSession;
	public static DaoMaster daoMaster;
    
    @Override
    public void onCreate() {
        super.onCreate();
        /* initialize preferences for future use */
        prefs = new AppPrefs(this);
        
        ex_database_helper_obj = new DaoMaster.DevOpenHelper(this,
				"onetodo.sqlite", null);
		ex_db = ex_database_helper_obj.getWritableDatabase();
		daoMaster = new DaoMaster(ex_db);
		daoSession = daoMaster.newSession();
    }
    
}
