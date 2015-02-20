package com.vector.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ContactsData {

	public static ContactsData contacts = null;


	public static ContactsData getInstance() {
		if (contacts == null) {
			contacts = new ContactsData();
		}
		return contacts;
	}

	public void setList(ContactsData obj) {
		contacts = obj;
	}

	
		@SerializedName("result")
		public ArrayList<Contacts> contactsList = new ArrayList<Contacts>();

		public class Contacts {
			@SerializedName("id")
			public String id;
			@SerializedName("first_name")
			public String firstName;
			@SerializedName("last_name")
			public String lastName;
			@SerializedName("profile_image")
			public String profileImage;
			@SerializedName("mobile_no")
			public String number;
		
	}
}