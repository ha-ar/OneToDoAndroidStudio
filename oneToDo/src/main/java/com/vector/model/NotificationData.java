package com.vector.model;

import java.util.ArrayList;

public class NotificationData {

	public static NotificationData notifications = null;


	public static NotificationData getInstance() {
        if(notifications == null) {
            notifications = new NotificationData();
		}
		return notifications;
	}


	public void setList(NotificationData obj) {
        notifications = obj;
	}

    public ArrayList<Result> result = new ArrayList<>();

    public class Result{

        public String id;
        public String user_id;
        public String sender_id;
        public String todo_id;
        public String type_of_notificationd;
        public String message;
        public String is_checked;
        public String date_created;
        public String first_name;
        public String last_name;
        public String profile_image;

    }
}