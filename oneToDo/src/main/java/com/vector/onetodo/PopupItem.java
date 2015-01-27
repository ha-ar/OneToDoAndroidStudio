package com.vector.onetodo;

public class PopupItem {
	private int itemId;
	private String titleText;
	private Class<?> activityClassName;
	
	public PopupItem(int itemId, String titleText, Class<?> activityClassName) {
		super();
		this.itemId = itemId;
		this.titleText = titleText;
		this.activityClassName = activityClassName;
	}
 
	public int getItemId() {
		return itemId;
	}
 
	public String getTitleText() {
		return titleText;
	}
 
	public Class<?> getActivityClassName() {
		return activityClassName;
	}
}
