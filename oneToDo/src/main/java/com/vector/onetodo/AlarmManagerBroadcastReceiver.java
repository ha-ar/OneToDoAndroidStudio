package com.vector.onetodo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Utils;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    private ToDoDao tododao;
    private String TAG = "com.vector.onetodo.notification";
    static List<ToDo> todo_obj, todoList;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        dbInit();
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        NotificationHandler nHandler;
        nHandler = NotificationHandler.getInstance(context);

        if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
            msgStr.append("One time Timer : ");
            nHandler.createNotification(context, intent);
        }else{
            //************ NOTIFICATIONS
            nHandler.createNotification(context, intent);
            ToDoDao tododao = App.daoSession.getToDoDao();
            ToDo obj = tododao.load(intent.getExtras().getLong("id"));
            if(!TextUtils.isEmpty(obj.getRepeat().getRepeat_interval())){
                Long updatedDate = obj.getStart_date() + Utils.getRepeatTime(obj.getRepeat().getRepeat_interval());
                obj.setStart_date(updatedDate);
                tododao.update(obj);
            }
//            obj.setIs_checked(true);
            SetNormalAlarm(context);
        }

        //*********SET ALARM FOR NEXT TIME
        //Release the lock
        wl.release();

    }

    void dbInit(){
        tododao = App.daoSession.getToDoDao();
        todo_obj = tododao.loadAll();
        QueryBuilder<ToDo> todo_query;
        todo_query = tododao.queryBuilder().where(ToDoDao.Properties.Is_checked.eq(false)).orderAsc(ToDoDao.Properties.Start_date);
        todoList = todo_query.list();
    }


    public void SetNormalAlarm(Context context){
        AlarmManager mgrAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        dbInit();
        if(todoList.isEmpty())
            return;

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("title", todoList.get(0).getTitle());
        intent.putExtra("location", todoList.get(0).getLocation());
        intent.putExtra("id", todoList.get(0).getId());
        // Loop counter `i` is used as a `requestCode`
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Single alarms in 1, 2, ..., 10 minutes (in `i` minutes)
        mgrAlarm.set(AlarmManager.RTC_WAKEUP,
                todoList.get(0).getStart_date() ,
                pendingIntent);
    }
    public void setRepeatAlarm(Context context, long interval){
        AlarmManager mgrAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        dbInit();
        if(todoList.isEmpty())
            return;

        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("title", todoList.get(0).getTitle());
        intent.putExtra("location", todoList.get(0).getLocation());
        intent.putExtra("id", todoList.get(0).getId());
        // Loop counter `i` is used as a `requestCode`
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, todoList.get(0).getId().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Single alarms in 1, 2, ..., 10 minutes (in `i` minutes)
        mgrAlarm.setRepeating(AlarmManager.RTC_WAKEUP,
                todoList.get(0).getStart_date() , interval,
                pendingIntent);
        Log.e("date", todoList.get(0).getStart_date()+"");
    }

    public void cancelAlarm(Context context, int id){
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, 0);
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

    public void setReminderAlarm(Context context, long reminderTime, String title, String location) {

        final int _id = (int) System.currentTimeMillis();
        AlarmManager mgrAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra("title","Reminder for " + title);
        intent.putExtra("location", location);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mgrAlarm.set(AlarmManager.RTC_WAKEUP, reminderTime,
                pendingIntent);
    }
}