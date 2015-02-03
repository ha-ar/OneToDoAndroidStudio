package com.vector.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class TaskData {

	public static TaskData taskdata = null;


	public static TaskData getInstance() {
		if (taskdata == null) {
			taskdata = new TaskData();
		}
		return taskdata;
	}

	public void setList(TaskData obj) {
		taskdata = obj;
	}

	
		@SerializedName("todos")
		public ArrayList<Todos> todos=new ArrayList<TaskData.Todos>();

		public class Todos {
			@SerializedName("id")
			public String id;
			@SerializedName("user_id")
			public String user_id;
			@SerializedName("todo_type_id")
			public String todo_type_id;
			@SerializedName("parent")
			public String parent;
			@SerializedName("title")
			public String title;
			@SerializedName("start_date")
			public String start_date;
			@SerializedName("end_date")
			public String end_date;
			@SerializedName("priority")
			public String priority;
			@SerializedName("notes")
			public String notes;
			@SerializedName("type")
			public String type;
			@SerializedName("time")
			public String time;
			@SerializedName("location")
			public String location;
			@SerializedName("location_tag")
			public String location_tag;
			@SerializedName("location_type")
			public String location_type;
			@SerializedName("repeat_interval")
			public String repeat_interval;
			@SerializedName("repeat_until")
			public String repeat_until;
			@SerializedName("checklist_data")
			public String checklist_data;
			@SerializedName("todo_comment")
			public String[] todo_comment;
			@SerializedName("todo_attachment")
			public String[] todo_attachment;
		
	}
}