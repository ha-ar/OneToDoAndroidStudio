package com.vector.onetodo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.vector.onetodo.utils.Utils;

public class NotificationHandler {
	private static NotificationHandler nHandler;
	private static NotificationManager mNotificationManager;

	private NotificationHandler () {}
	/**
	 * Singleton pattern implementation
	 * @return
	 */
	public static  NotificationHandler getInstance(Context context) {
		if(nHandler == null) {
			nHandler = new NotificationHandler();
			mNotificationManager =
					(NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		}

		return nHandler;
	}


	/**
	 * Shows a simple notification
	 * @param context application context
	 */
	
    public void createNotification(Context context, Intent intent) {
        // Creates an explicit intent for an Activity

		long todoId = intent.getExtras().getLong("id");
        Intent resultIntent = new Intent(context, TaskView.class);
        resultIntent.putExtra("todo_id", todoId);
		boolean isAllDay = App.daoSession.getToDoDao().load(todoId).getIs_allday();

        // Creating a artificial activity stack for the notification activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        // Pending intent to the notification manager
        PendingIntent resultPending = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Building the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setPriority(Notification.PRIORITY_MAX)
                .setSound(Uri.parse("android.resource://com.vector.onetodo/raw/onetodo_notification"))
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(intent.getExtras().getString("title"))
				.setContentText(intent.getExtras().getString("location")) // notification text
				.addAction(0, "Snooze", resultPending)
				.addAction(0, "Done", resultPending)
				.setOngoing(isAllDay)
				.setContentIntent(resultPending); // notification intent

        // mId allows you to update the notification later on.
//        mBuilder.addflags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(0, mBuilder.build());

    }
	public void createSimpleNotification2(Context context,String title,long date,String location, String type,String message,long todo_id) {
		// Creates an explicit intent for an Activity

            Intent mainIntent = new Intent(context, TaskView.class);
            mainIntent.putExtra("todo_id", todo_id);
            mainIntent.putExtra("is_assigned_task", true);

            // Creating a artifical activity stack for the notification activity
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(TaskView.class);
            stackBuilder.addNextIntent(mainIntent);
            PendingIntent resultPending = stackBuilder
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            // Pending intent to the notification manager


            //getting Custom layout
                    RemoteViews expandedView = new RemoteViews(context.getApplicationContext().getPackageName(),R.layout.custom_notification);
        expandedView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
        if(type.equals("assigned_task") != false){ //Simple assigned task notification

            Intent commentIntent = new Intent(context, NotificationButtonClickListener.class);
            commentIntent.putExtra("todo_id", todo_id);
            commentIntent.putExtra("dialogue", "comment");
            PendingIntent commentPending = PendingIntent.getBroadcast(context, 102, commentIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            expandedView.setOnClickPendingIntent(R.id.action_right, commentPending);

            expandedView.setTextViewText(R.id.notification_heading, "Assined task");
            expandedView.setTextViewText(R.id.title, title);
//            expandedView.setTextViewText(R.id.date, Utils.getDate(date, "e d m Y"));
            expandedView.setTextViewText(R.id.action_left, "View");
            expandedView.setTextViewText(R.id.action_right, "Comment");


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setTicker("Task Assigned to you !")
                    .setSound(Uri.parse("android.resource://com.vector.onetodo/raw/onetodo_notification"))
                    .setSmallIcon(R.drawable.ic_launcher) // notification icon
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentText(message) // notification text
                    .setContentIntent(resultPending); // notification intent

            Notification notification = mBuilder.build();

            //And now finally attach the Remote view to the notification
            notification.bigContentView = expandedView;
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, notification);

        }else if(type.equals("event_invitation")){ //Event Invitiation Notification



            Intent rsvpIntent = new Intent(context, NotificationButtonClickListener.class);
            rsvpIntent.putExtra("todo_id", todo_id);
            rsvpIntent.putExtra("dialogue", "rsvp");
            PendingIntent rsvPending = PendingIntent.getBroadcast(context.getApplicationContext(), 102, rsvpIntent, 0);
            expandedView.setOnClickPendingIntent(R.id.action_right, rsvPending);
//            expandedView.setOnClickPendingIntent(R.id.action_left, resultPending);
            expandedView.setTextViewText(R.id.notification_heading, "Event Invitation");
            expandedView.setTextViewText(R.id.title, title);
//            expandedView.setTextViewText(R.id.date, Utils.getDate(date, "e d m Y"));
            expandedView.setTextViewText(R.id.location, location);
            expandedView.setTextViewText(R.id.action_left, "View");
            expandedView.setTextViewText(R.id.action_right, "RSVP");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setTicker("Event invitation")
                    .setSound(Uri.parse("android.resource://com.vector.onetodo/raw/onetodo_notification"))
                    .setSmallIcon(R.drawable.ic_launcher) // notification icon
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentText(message) // notification text
                    .setContentIntent(resultPending); // notification intent

            Notification notification = mBuilder.build();

            //And now finally attach the Remote view to the notification
            notification.bigContentView = expandedView;
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, notification);

        }else { //Comment Notification

            Intent commentIntent = new Intent(context, NotificationButtonClickListener.class);
            commentIntent.putExtra("todo_id", todo_id);
            commentIntent.putExtra("dialogue", "comment");
            PendingIntent commentPending = PendingIntent.getBroadcast(context, 102, commentIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            expandedView.setOnClickPendingIntent(R.id.action_right, commentPending);

            expandedView.setTextViewText(R.id.notification_heading, "Name");
            expandedView.setTextViewText(R.id.title, title);
            expandedView.setTextViewText(R.id.location, message);
            expandedView.setTextViewText(R.id.action_left, "View");
            expandedView.setTextViewText(R.id.action_right, "Comment");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setTicker(message.toString().toLowerCase().substring(0, 10))
                    .setSound(Uri.parse("android.resource://com.vector.onetodo/raw/onetodo_notification"))
                    .setSmallIcon(R.drawable.ic_launcher) // notification icon
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentText(message) // notification text
                    .setContentIntent(resultPending); // notification intent

            Notification notification = mBuilder.build();

            //And now finally attach the Remote view to the notification
            notification.bigContentView = expandedView;
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, notification);
        }
            // Building the notification






	}

    public static void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }
}
