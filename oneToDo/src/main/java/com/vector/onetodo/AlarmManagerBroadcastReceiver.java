package com.vector.onetodo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.vector.onetodo.db.gen.DaoMaster;
import com.vector.onetodo.db.gen.DaoSession;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.db.gen.ToDoDao.Properties;
import com.vector.onetodo.utils.Constants;

import de.greenrobot.dao.query.QueryBuilder;



public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";
	public  DaoSession daoSession;
	public  DaoMaster daoMaster;
	public  List<ToDo> todos;
	SQLiteDatabase db;
	ToDoDao tododao;

	static List<ToDo> todo_obj,todo_Qlist;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
         db_initialize(context);
 		 todo_obj=tododao.loadAll();
         //Acquire the lock
         wl.acquire();

         //You can do the processing here update the widget/remote views.
         Bundle extras = intent.getExtras();
         StringBuilder msgStr = new StringBuilder();
         
         if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
        	 msgStr.append("One time Timer : ");
         }
         
         
         
         //************ NOTIFICATIONS
         NotificationHandler nHandler;
 	   	 nHandler = NotificationHandler.getInstance(context);
 		 nHandler.createSimpleNotification(context);
 		//*********SET ALARM FOR NEXT TIME
 		 SetAlarm(context);
 		
 		
         //Release the lock
         wl.release();
         
	}
	
	public void SetAlarm(Context context){
		AlarmManager mgrAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        Constants.AlaramIndex=Constants.AlaramIndex+1;
        if(Constants.AlaramSize>Constants.AlaramIndex){
			 {
		           Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		           PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
		           mgrAlarm.set(AlarmManager.RTC_WAKEUP, 
		                        todo_Qlist.get(Constants.AlaramIndex).getStart_date() , 
		                        pendingIntent); 

		           intentArray.add(pendingIntent);
		        }
		}
	}
	
	public void SetcustAlarm(Context context)
    {
		db_initialize(context);
		Calendar tempCal = Calendar.getInstance();
		long date=tempCal.getTimeInMillis();
   
		
		Log.v("Current date", date+"  "+tempCal);

		QueryBuilder<ToDo> todo_queary;
		todo_queary=tododao.queryBuilder().where(Properties.Start_date.ge(date)).orderAsc(Properties.Start_date);
		
		todo_Qlist=todo_queary.list(); 
		Constants.AlaramSize=todo_Qlist.size();
		AlarmManager mgrAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        for(int i=0;i<todo_Qlist.size();i++){
		Toast.makeText(context, ""+todo_Qlist.get(i).getTitle(), Toast.LENGTH_LONG).show();
		}

		if(Constants.AlaramSize>0)
		{
			Constants.AlaramIndex=0;
			 {
		           Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		           // Loop counter `i` is used as a `requestCode`
		           PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
		           // Single alarms in 1, 2, ..., 10 minutes (in `i` minutes)
		           mgrAlarm.set(AlarmManager.RTC_WAKEUP, 
		                        todo_Qlist.get(Constants.AlaramIndex).getStart_date() , 
		                        pendingIntent); 

		           intentArray.add(pendingIntent);
		        }
		}
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    public void setOnetimeTimer(Context context){
    	AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP,MainActivity.todo_obj.get(0).getStart_date(), pi);
    }
    
    public void db_initialize(Context context) {
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
				"OneTodo-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		tododao = daoSession.getToDoDao();
	}
}