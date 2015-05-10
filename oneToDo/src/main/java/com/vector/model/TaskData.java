package com.vector.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

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

    public void clearList() {
        taskdata = null;
    }

    public String notification_count;

    public HashMap<Integer, String> labels = new HashMap<>();


        @SerializedName("result")
        public Result result = new Result();

        public class Result{
            @SerializedName("todos")
            public ArrayList<Todos> todos = new ArrayList<>();
        }

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
			public ArrayList<Comments> todo_comment;

            @SerializedName("todo_attachment")
			public ArrayList<String> todo_attachment;

            @SerializedName("invitee_list")
            public Invitee invitee_list;

	}

    public class Comments{
        public String comment;
        public String name;
        public String time;
    }

    public class Invitee{
        public ArrayList<Pending> pending = new ArrayList<>();
        public ArrayList<Accepted> accepted = new ArrayList<>();
    }
    public class Pending{
        public String user_id;
        public String name;
    }

    public class Accepted{
        public String user_id;
        public String name;
    }
}