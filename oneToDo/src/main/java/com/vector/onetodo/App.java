package com.vector.onetodo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
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

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static SharedPreferences pref, label, attach, comment;

    
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
        gpsTracker = new GPSTracker(this);

        pref = this.getSharedPreferences("Location", 0);
        label = this.getSharedPreferences("Label", 0);
        attach = this.getSharedPreferences("Attach", 0);
        comment = this.getSharedPreferences("Comment", 0);

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
                            Log.v("JSON APP TAKS", json.toString());
                            Gson gson = new Gson();
                            TaskData obj = gson.fromJson(json.toString(),
                                    TaskData.class);
                            TaskData.getInstance().setList(obj);

                        }
                    }
                });
    }
    public static void uploadAttachments(AQuery aq, byte[] byteArray) {

        HttpEntity entity = null;
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("image", encoded));

        try {
            entity = new UrlEncodedFormEntity(pairs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, HttpEntity> param = new HashMap<>();
        param.put(AQuery.POST_ENTITY, entity);

        aq.ajax("http://api.heuristix.net/one_todo/v1/upload.php", param,
                JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json,
                                         AjaxStatus status) {
                        String path = null;
                        try {

                            JSONObject obj1 = new JSONObject(json.toString());
                            path = obj1.getString("path");
                        } catch (Exception e) {
                        }

                    }
                });
    }
}
