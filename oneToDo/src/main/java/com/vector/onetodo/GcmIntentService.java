package com.vector.onetodo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.vector.model.AssignedTaskData;
import com.vector.onetodo.db.gen.CheckList;
import com.vector.onetodo.db.gen.Reminder;
import com.vector.onetodo.db.gen.Repeat;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private String notificationtype;
    private String notificationtMessage;
    private Bitmap userImgBitmap = null;
    NotificationCompat.Builder builder;
    AQuery aq;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        aq = new AQuery(this);

        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

                NotificationHandler nHandler;
                nHandler = NotificationHandler.getInstance(getApplicationContext());
//                nHandler.createSimpleNotification2(getApplicationContext(), "test", "test", 12);
                notificationtype = extras.getString("type");
                notificationtMessage = extras.getString("message");
                getAssignedTaskData(extras.getString("todo_id"));
                // Post notification of received message.
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)

                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void createSimpleNotification3(Context context,String title,String message) {
        // Creates an explicit intent for an Activity

        Intent resultIntent = new Intent(context, MainActivity.class);

        // Creating a artifical activity stack for the notification activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        // Pending intent to the notification manager
        PendingIntent resultPending = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Building the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher) // notification icon
                .setContentTitle(title)
                .setContentText(message) // notification text
                .setContentIntent(resultPending); // notification intent

        // mId allows you to update the notification later on.
        mNotificationManager.notify(10, mBuilder.build());
    }
    private void getAssignedTaskData( final String todoId){
        aq.ajax("http://api.heuristix.net/one_todo/v1/task/" + todoId, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String json,
                                 AjaxStatus status) {
                try {
                    Log.e("task", json);
                    Gson gson = new Gson();
                    AssignedTaskData.getInstance().setList(gson.fromJson(json, AssignedTaskData.class));
                    saveDataToDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveDataToDB(){
        ToDo todo = new ToDo();
        todo.setTitle(AssignedTaskData.getInstance().task.get(0).title);
        todo.setStart_date(Utils.convertToLocalTime(AssignedTaskData.getInstance().task.get(0).startDate));
        todo.setEnd_date(Utils.convertToLocalTime(AssignedTaskData.getInstance().task.get(0).endDate));
        todo.setTodo_server_id(Integer.valueOf(AssignedTaskData.getInstance().task.get(0).id));
        todo.setLocation(AssignedTaskData.getInstance().task.get(0).todoLocation);
        todo.setNotes(AssignedTaskData.getInstance().task.get(0).notes);
        todo.setTodo_type_id(Integer.valueOf(AssignedTaskData.getInstance().task.get(0).todoTypeId));
        todo.setIs_assigned_task(true);
        todo.setUser_id(Integer.valueOf(AssignedTaskData.getInstance().task.get(0).id));
        todo.setUser_name(AssignedTaskData.getInstance().task.get(0).first_name + " " + AssignedTaskData.getInstance().task.get(0).last_name);


        Reminder reminder = new Reminder();
        reminder.setLocation(AssignedTaskData.getInstance().task.get(0).time);
        App.daoSession.getReminderDao().insert(reminder);
        todo.setReminder(reminder);

        Repeat repeat = new Repeat();
        repeat.setIs_forever(false);
        repeat.setRepeat_interval(AssignedTaskData.getInstance().task.get(0).repeatInterval);
        App.daoSession.getRepeatDao().insert(repeat);
        todo.setRepeat(repeat);

        CheckList checkList = new CheckList();
        checkList.setTitle(AssignedTaskData.getInstance().task.get(0).checkListData);
        App.daoSession.getCheckListDao().insert(checkList);
        todo.setCheckList(checkList);

        App.daoSession.getToDoDao().insert(todo);

        List<ToDo> addedTodo = App.daoSession.getToDoDao().queryBuilder().orderDesc(ToDoDao.Properties.Id).list();
        NotificationHandler nHandler;
        nHandler = NotificationHandler.getInstance(getApplicationContext());
        String first_name = AssignedTaskData.getInstance().task.get(0).first_name;
        String last_name = AssignedTaskData.getInstance().task.get(0).last_name;
        String ImgUri = AssignedTaskData.getInstance().task.get(0).profile_image;

        if(aq.getCachedImage(ImgUri) != null){
            userImgBitmap = aq.getCachedImage(ImgUri);
        }else{
            try {
                URL url = new URL(ImgUri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                userImgBitmap = BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();

            }
        }



        nHandler.createSimpleNotification2(getApplicationContext(), addedTodo.get(0).getTitle(),addedTodo.get(0).getStart_date(), addedTodo.get(0).getLocation(),notificationtype,notificationtMessage,first_name,last_name,userImgBitmap ,addedTodo.get(0).getId());
    }
}