package com.vector.onetodo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Fuji on 08/05/2015.
 */
public class NotificationButtonClickListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("Nbtlisstner", "got buttom click");
        NotificationHandler.cancelNotification();
        String action = intent.getStringExtra("dialogue");

        if(action.equals("rsvp") != false){
            Log.d("Nbtlisstner", "got RSVP click..................");
            Intent taskViewIntent = new Intent(context, TaskView.class);
            taskViewIntent.putExtra("todo_id", intent.getLongExtra("todo_id", -1));
            taskViewIntent.putExtra("is_assigned_task", true);
            taskViewIntent.putExtra("rsvp", true);
            
            taskViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(taskViewIntent);
        }

        if(action.equals("comment") != false){
            Log.d("Nbtlisstner", "got Comment click..................");
            Intent taskViewIntent = new Intent(context.getApplicationContext(), TaskView.class);
            taskViewIntent.putExtra("todo_id", intent.getLongExtra("todo_id", -1));
            taskViewIntent.putExtra("is_assigned_task", true);
            taskViewIntent.putExtra("commentFrg", true);

            taskViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(taskViewIntent);
        }
    }
}
