package com.vector.onetodo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

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
	public void createSimpleNotification2(Context context,String title,String message, long todo_id) {
		// Creates an explicit intent for an Activity
	
		Intent resultIntent = new Intent(context, TaskView.class);

		// Creating a artifical activity stack for the notification activity
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
        resultIntent.putExtra("todo_id", todo_id);
        resultIntent.putExtra("is_assigned_task", true);
		// Pending intent to the notification manager
		PendingIntent resultPending = stackBuilder
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
 

		// Building the notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSound(Uri.parse("android.resource://com.vector.onetodo/raw/onetodo_notification"))
				.setSmallIcon(R.drawable.ic_launcher) // notification icon
				.setContentTitle("Event Invitation")
				.setContentText("IT-workshop") // notification text
                .setContentText("Date")
                .addAction(0, "View", resultPending)
                .addAction(0, "RSVP", resultPending)
				.setContentIntent(resultPending); // notification intent

		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());

	}
}
