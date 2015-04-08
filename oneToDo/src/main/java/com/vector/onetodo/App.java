package com.vector.onetodo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.vector.model.TaskData;
import com.vector.onetodo.db.gen.DaoMaster;
import com.vector.onetodo.db.gen.DaoMaster.DevOpenHelper;
import com.vector.onetodo.db.gen.DaoSession;
import com.vector.onetodo.utils.AppPrefs;
import com.vector.onetodo.utils.Constants;
import com.vector.onetodo.utils.GPSTracker;

import org.json.JSONObject;

public class App extends Application{
	
	/**
     * Application preferences *
     */
    public static AppPrefs prefs;
    public static DevOpenHelper ex_database_helper_obj;
	public static SQLiteDatabase ex_db;
	public static DaoSession daoSession;
	public static DaoMaster daoMaster;
    public static GPSTracker gpsTracker;
    public static SharedPreferences pref, label, attach;

    
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
        gpsTracker = new GPSTracker(getApplicationContext());

        pref = this.getSharedPreferences("Location", 0);
        label = this.getSharedPreferences("Label", 0);
        attach = this.getSharedPreferences("Attach", 0);

    }

    public static void updateTaskList(Context ctx){
        AQuery aq = new AQuery(ctx);
        aq.ajax("http://api.heuristix.net/one_todo/v1/tasks/"
                        + Constants.user_id, JSONObject.class,
                new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json,
                                         AjaxStatus status) {
                        if (json != null) {
                            Gson gson = new Gson();
                            TaskData obj = gson.fromJson(json.toString(),
                                    TaskData.class);
                            TaskData.getInstance().setList(obj);
                            Log.v("JSON",
                                    TaskData.getInstance().result.todos.get(0).notes
                                            + "");
                        }
                    }
                });
    }
}
