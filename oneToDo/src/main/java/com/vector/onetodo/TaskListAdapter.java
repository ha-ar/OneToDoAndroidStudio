package com.vector.onetodo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vector.onetodo.db.gen.ToDo;

public class TaskListAdapter extends BaseAdapter {

	Context context;
	List<ToDo> listToShow;

	public TaskListAdapter(Context context, List<ToDo> whichList) {
		this.context = context;
		this.listToShow = whichList;
	}

	@Override
	public int getCount() {
		return listToShow.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int ID) {
		return ID;
	}

	@Override
	public View getView(int position, View view1, ViewGroup parent) {
		View view = view1;
		Holder holder = null;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item, parent, false);
			holder = new Holder();
			holder.icon = (TextView) view.findViewById(R.id.list_icon);
			holder.title = (TextView) view.findViewById(R.id.list_title);
			holder.location = (TextView) view.findViewById(R.id.list_location);
			holder.time = (TextView) view.findViewById(R.id.list_time);
			holder.time1 = (TextView) view.findViewById(R.id.list_time1);
			holder.type = (TextView) view.findViewById(R.id.type);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		if (listToShow.get(position).getTodo_type_id() == 1)
			holder.type.setText("TASK");
		else if (listToShow.get(position).getTodo_type_id() == 2)
			holder.type.setText("EVENT");
		else if (listToShow.get(position).getTodo_type_id() == 3)
			holder.type.setText("SCHEDULE");
		else if (listToShow.get(position).getTodo_type_id() == 4)
			holder.type.setText("APPOINMENT");
		else if (listToShow.get(position).getTodo_type_id() == 5)
			holder.type.setText("PROJECT");

		holder.title.setText(listToShow.get(position).getTitle());
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		Calendar calendar = Calendar.getInstance();
		Long startDate = listToShow.get(position).getStart_date();
		calendar.setTimeInMillis(startDate);

		holder.time.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.SHORT, Locale.US)
				+ " "
				+ calendar.get(Calendar.DAY_OF_MONTH)
				+ " "
				+ calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
						Locale.US) + " " + calendar.get(Calendar.YEAR));
		holder.time1.setText(String.format("%02d", calendar.get(Calendar.HOUR))
				+ ":"
				+ String.format("%02d", calendar.get(Calendar.MINUTE))
				+ " "
				+ calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT,
						Locale.US));
		holder.location.setText(listToShow.get(position).getLocation());

		return view;
	}

}

class Holder {
	TextView title, location, time, icon, time1, type;
}
