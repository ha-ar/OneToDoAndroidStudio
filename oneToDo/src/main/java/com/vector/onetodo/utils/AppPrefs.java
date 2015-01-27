package com.vector.onetodo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPrefs {
	private SharedPreferences appSharedPrefs;
	private SharedPreferences.Editor prefsEditor;

	private String PREFS_NAME = "pref";
	private String SHARED_NAME = "com.vector.onetodo";
	private String GCMID = "gcmid";
	private String USER_ID = "user_id";
	private String INITIALS = "initials";
	private String USER_NAME = "user_name";
	private String USER_NUMBER = "user_number";
	private String USER_EMAIL = "user_email";
	private String DATE_FORMAT = "date_format";
	private String TIME_FORMAT = "time_format";
	private String STARTING_DAY = "starting_day";
	private String COUNTRY_CODE = "country_code";

	public AppPrefs(Context context) {
		this.appSharedPrefs = context.getSharedPreferences(SHARED_NAME,
				Activity.MODE_PRIVATE);
		this.prefsEditor = appSharedPrefs.edit();
	}

	public void clearSharedPrefs() {
		prefsEditor.clear().commit();
	}

	/**
	 * Function takes shared preference title/key and returns saved preference
	 * value
	 * 
	 * @param mContext
	 * @param sharedPrefTitle
	 * @return
	 */
	public String getSharedPrefValue(Context mContext, String sharedPrefTitle) {
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME,
				0);
		return settings.getString(sharedPrefTitle, null);
	}

	/**
	 * Save preference value against given key
	 * 
	 * @param mContext
	 * @param sharedPrefTitle
	 * @param sharedPrefValue
	 */
	public void saveSharedPrefValue(Context mContext, String sharedPrefTitle,
			String sharedPrefValue) {
		try {
			SharedPreferences settings = mContext.getSharedPreferences(
					PREFS_NAME, 0);
			final SharedPreferences.Editor editor = settings.edit();
			editor.putString(sharedPrefTitle, sharedPrefValue);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removing saved preference value by providing its key.
	 * 
	 * @param mContext
	 * @param sharedPrefTitle
	 */
	public void removeSharedPrefValue(Context mContext, String sharedPrefTitle) {
		try {
			SharedPreferences settings = mContext.getSharedPreferences(
					PREFS_NAME, 0);
			Editor editor = settings.edit();
			editor.remove(sharedPrefTitle);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getGcmid() {
		return appSharedPrefs.getString(GCMID, null);
	}

	public void setGcmid(String _gcmid) {
		prefsEditor.putString(GCMID, _gcmid).commit();
	}

	public int getUserId() {
		return appSharedPrefs.getInt(USER_ID, -1);
	}

	public void setUserId(int _user_id) {
		prefsEditor.putInt(USER_ID, _user_id).commit();
	}

	public void setInitials(String initial) {
		prefsEditor.putString(INITIALS, initial).commit();
	}

	public String getInitials() {
		return appSharedPrefs.getString(INITIALS, "GU");
	}

	public void setUserName(String userName) {
		prefsEditor.putString(USER_NAME, userName).commit();
	}

	public String getUserName() {
		return appSharedPrefs.getString(USER_NAME, "Guest User");
	}

	public void setUserNumber(String number) {
		prefsEditor.putString(USER_NUMBER, number).commit();
	}

	public String getUserNumber() {
		return appSharedPrefs.getString(USER_NUMBER, "Set phone number");
	}

	public void setUserEmail(String email) {
		prefsEditor.putString(USER_EMAIL, email).commit();
	}

	public String getUserEmail() {
		return appSharedPrefs.getString(USER_EMAIL, "Set email");
	}

	public void setStartingWeekDay(String day) {
		prefsEditor.putString(STARTING_DAY, day).commit();
	}
	
	public String getStartingWeekDay() {
		return appSharedPrefs.getString(STARTING_DAY, "Monday");
	}
	
	public void setDateFromat(String dateFormat) {
		prefsEditor.putString(DATE_FORMAT, dateFormat).commit();
	}
	
	public String getDateFormat() {
		return appSharedPrefs.getString(DATE_FORMAT, "DD.MM.YYYY");
	}
	
	public void setTimeFormat(String timeFormat) {
		prefsEditor.putString(TIME_FORMAT, timeFormat).commit();
	}
	
	public String getTimeFormat() {
		return appSharedPrefs.getString(TIME_FORMAT, "12 H");
	}
	public void setCountryCode(String countryCode) {
		prefsEditor.putString(COUNTRY_CODE, countryCode).commit();
	}
	
	public String getCountryCode() {
		return appSharedPrefs.getString(COUNTRY_CODE, "+");
	}

}
