package com.vector.onetodo.utils;

import java.util.List;
/*
import com.vector.onetodo.db.gen.LabelName;*/

public class Constants {
	
	
	public static String Google_APP_ID = "AIzaSyDTqjUgfxT3ysmPCX-tewvfDSfyWbf-WoI";
	public static String FB_APP_ID = "830264356984345";
	public final static String ICON_FONT = "onetodo.ttf";
	public static int user_id=-1;
	public static boolean date,time,week;
	public final static String MORE = "";

	public static String SENDER_ID="184279149655";
	public static String RegId="";
	public final static String[] CONTACTS_EVOKING_WORDS = { "call", "message",
			"sms", "text", "ring", "dial", "phone", "telephone", "mobile",
			"ping", "buzz", "email", "mail" };
	public static String NORMAL_TYPEFACE = "HelveticaNeue.otf";
	public static String LIGHT_TYPEFACE = "HelveticaNeue-Light.otf";
	public static String MED_TYPEFACE = "HelveticaNeue-Medium.otf";
	public static String BOLD_TYPEFACE = "HelveticaNeueLTStd-Bd.otf";
	public static String ROMAN_TYPEFACE = "HelveticaNeueLTStd-Roman.otf";
	public static int AlaramIndex = -1;
	public static int AlaramSize = 0;
	public static float density = 0, dp = 0;
	public static List<String> Name,Contact;
	
	public static String[] label_colors_dialog = { "#790000", "#005826", "#0D004C", "#ED145B", "#E0D400",
			"#0000FF", "#4B0049", "#005B7F", "#603913", "#005952" };
	
	public static String[] labels_array = new String[] { "Personal", "Home",
			"Work", "New", "New", "New", "New", "New", "New" };
	
	public static String[] repeatArray = new String[] { "Never", "Daily",
		"Weekly", "Monthly", "Yearly" };
	
	public static String[] label_colors = { "#AC7900", "#4D6600", "#5A0089" };
	
	public static String[] beforeArray = new String[] { "On Time", "15 Mins",
		"30 Mins", "2 Hours", "Custom" };
	
	public static String[] beforevalues = { "Mins", "Hours", "Days", "Weeks",
		"Months", "Years" };
	

	public static int Project_task_check=0;
	public static String[] state = { "Quick Access Tasks", "Settings", "Help",
			"About" };
	public static String[][] parent = {
			{ "Delayed", "Assigned", "Shared", "Completed", "Deleted", "None" },
			{ "Date Format", "Time Format", "Set Start of Week",
					"Unscheduled Tasks" }, {}, {} };

	public static String[][][] child = {
			{ {}, {}, {}, {}, {}, {} },
			{ { "DD/MM/YY", "MM/DD/YY" }, { "12H", "24H" },
					{ "Monday", "Sunday" }, {}, {} }, { {}, {}, {}, {}, {} },
			{ {}, {}, {}, {}, {} } };
}
