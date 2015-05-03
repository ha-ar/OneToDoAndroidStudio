package com.vector.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AssignedTaskData {

	public static AssignedTaskData contacts = null;


	public static AssignedTaskData getInstance() {
		if (contacts == null) {
			contacts = new AssignedTaskData();
		}
		return contacts;
	}

	public void setList(AssignedTaskData obj) {
		contacts = obj;
	}


	@SerializedName("task")
	public Task task = new Task();

	public class Task {

		@SerializedName("0")
		public MTask mTask;
		@SerializedName("todo_comment")
		public ArrayList<Comment> comments;
		@SerializedName("todo_attachment")
		public ArrayList<String> attachments;

	}

	public class MTask{
		@SerializedName("id")
		public String id;
		@SerializedName("first_name")
		public String first_name;
		@SerializedName("last_name")
		public String last_name;
		@SerializedName("user_id")
		public String senderId;
		@SerializedName("type")
		public String type;
		@SerializedName("todo_type_id")
		public String todoTypeId;
		@SerializedName("time")
		public String time;
		@SerializedName("title")
		public String title;
		@SerializedName("start_date")
		public String startDate;
		@SerializedName("end_date")
		public String endDate;
		@SerializedName("location")
		public String location;
		@SerializedName("repeat_until")
		public String repeatUntil;
		@SerializedName("repeat_interval")
		public String repeatInterval;
		@SerializedName("checklist_data")
		public String checkListData;
		@SerializedName("notes")
		public String  notes;
	}

	public class Comment{
		public String comment;
		public String name;
		public String time;
	}
}