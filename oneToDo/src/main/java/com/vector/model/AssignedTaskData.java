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
		public ArrayList<Task> task = new ArrayList<>();

		public class Task {
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
            @SerializedName("checklist_data")
            public String checkListData;
            @SerializedName("notes")
            public String  notes;
        }
}