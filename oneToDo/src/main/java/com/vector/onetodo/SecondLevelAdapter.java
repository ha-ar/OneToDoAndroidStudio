package com.vector.onetodo;

import java.util.Arrays;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SecondLevelAdapter extends BaseExpandableListAdapter {

	public Object child;
	Context mContext;
	LayoutInflater inflater;

	String[] quickAccess = { "Delayed", "Assigned", "Shared", "Completed",
			"Deleted", "None" };
	String[] thirdLevelFormat = { "DD/MM/YY", "MM/DD/YY" };
	String[] timeFormat = { "12H", "24H" };
	String[] weekStart = { "Monday", "Sunday" };

	public SecondLevelAdapter(Object child, Context context) {
		this.child = child;
		this.mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return child.children.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// third level
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View layout = convertView;
		final Object item = (Object) getChild(groupPosition, childPosition);

		final ChildViewHolder holder;

		if (layout == null) {
			layout = inflater.inflate(R.layout.menu_items_second_child, parent,
					false);

			holder = new ChildViewHolder();
			holder.title = (TextView) layout.findViewById(R.id.itemParentTitleSecond);
			holder.checkBox = (CheckBox) layout
					.findViewById(R.id.itemParentImageSecond);
			layout.setTag(holder);
		} else {
			holder = (ChildViewHolder) layout.getTag();
		}

		holder.title.setText(item.title.trim());
		holder.checkBox.setTag(item.title);

		if (Arrays.asList(thirdLevelFormat).contains(item.title)) {
			holder.checkBox.setVisibility(View.VISIBLE);
 
		} else if (Arrays.asList(timeFormat).contains(item.title)) {
			holder.checkBox.setVisibility(View.VISIBLE);
 
		} else if (Arrays.asList(weekStart).contains(item.title)) {
			holder.checkBox.setVisibility(View.VISIBLE);
 
		}

		return layout;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return child.children.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return child.children.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return child.children.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	// Second level
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View layout = convertView;
		final ViewHolder holder;

		final Object item = (Object) getGroup(groupPosition);

		if (layout == null) {
			layout = inflater.inflate(R.layout.menu_items_child, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) layout.findViewById(R.id.itemParentTitleFirst);
			holder.checkBox = (CheckBox) layout
					.findViewById(R.id.itemParentImageFirst);
			layout.setTag(holder);
		} else {
			holder = (ViewHolder) layout.getTag();
		}

		holder.title.setText(item.title.trim());
		if (Arrays.asList(quickAccess).contains(item.title)) {
			holder.checkBox.setVisibility(View.VISIBLE);
			holder.checkBox.setTag(item.title);
			holder.checkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) { 

				}
			});
		}

		return layout;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		super.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		Log.d("SecondLevelAdapter", "Unregistering observer");
		if (observer != null) {
			super.unregisterDataSetObserver(observer);
		}
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private static class ViewHolder {
		TextView title;
		CheckBox checkBox;
	}

	private static class ChildViewHolder {
		TextView title;
		CheckBox checkBox;
	}

}