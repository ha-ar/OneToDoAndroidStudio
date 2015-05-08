package com.vector.onetodo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vector.onetodo.db.gen.ToDo;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.feio.android.checklistview.utils.AlphaManager;

public class TaskListAdapter extends BaseAdapter {

	private Context context;
	private List<ToDo> listToShow;
	private int dayPosition;

	public TaskListAdapter(Context context, List<ToDo> whichList, int dayPosition) {
		this.context = context;
		this.listToShow = whichList;
		this.dayPosition = dayPosition;
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
	public View getView(final int position, View view1, ViewGroup parent) {
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
			holder.startDate = (TextView) view.findViewById(R.id.list_start_date);
			holder.startTime = (TextView) view.findViewById(R.id.list_start_time);
			holder.endDate = (TextView) view.findViewById(R.id.list_end_date);
			holder.endTime = (TextView) view.findViewById(R.id.list_end_time);
			holder.type = (TextView) view.findViewById(R.id.type);
			holder.completeTask = (CheckBox) view.findViewById(R.id.completed_task);
			holder.reminder = (ImageView) view.findViewById(R.id.reminder_icon);
			holder.repeat = (ImageView) view.findViewById(R.id.repeat_icon);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		if (listToShow.get(position).getTodo_type_id() == 1)
			holder.type.setText("Task");
		else if (listToShow.get(position).getTodo_type_id() == 2)
			holder.type.setText("Event");
		else if (listToShow.get(position).getTodo_type_id() == 3)
			holder.type.setText("Schedule");
		else if (listToShow.get(position).getTodo_type_id() == 4)
			holder.type.setText("Appointment");
		else if (listToShow.get(position).getTodo_type_id() == 5)
			holder.type.setText("Project");

		holder.title.setText(listToShow.get(position).getTitle());
		Calendar calendar = Calendar.getInstance();
		Long startDate = listToShow.get(position).getStart_date();
		calendar.setTimeInMillis(startDate);

		if(dayPosition != 0) {
			holder.startDate.setVisibility(View.VISIBLE);
		}
		else holder.startDate.setVisibility(View.INVISIBLE);

		holder.startDate.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.SHORT, Locale.US)
				+ " "
				+ calendar.get(Calendar.DAY_OF_MONTH)
				+ " "
				+ calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
				Locale.US) + " " + calendar.get(Calendar.YEAR));

		holder.startTime.setText(String.format("%02d", calendar.get(Calendar.HOUR))
				+ ":"
				+ String.format("%02d", calendar.get(Calendar.MINUTE))
				+ " "
				+ calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT,
				Locale.US));

		boolean eventOrSchedule = isEventOrSchedule(listToShow.get(position).getTodo_type_id());
		if(eventOrSchedule){
			holder.startDate.setVisibility(View.VISIBLE);
		}
		if(listToShow.get(position).getRepeat() != null && !eventOrSchedule){
			holder.repeat.setVisibility(View.VISIBLE);
		}

		if(listToShow.get(position).getReminder() != null && !eventOrSchedule){
			holder.reminder.setVisibility(View.VISIBLE);
		}
		if(eventOrSchedule){
			Calendar calendarEnd = Calendar.getInstance();
			Long endDate = listToShow.get(position).getEnd_date();
			calendarEnd.setTimeInMillis(endDate);

			holder.endDate.setText(calendarEnd.getDisplayName(Calendar.DAY_OF_WEEK,
					Calendar.SHORT, Locale.getDefault())
					+ " "
					+ calendar.get(Calendar.DAY_OF_MONTH)
					+ " "
					+ calendarEnd.getDisplayName(Calendar.MONTH, Calendar.SHORT,
					Locale.getDefault()) + " " + calendarEnd.get(Calendar.YEAR));

			holder.endTime.setText(String.format("%02d", calendarEnd.get(Calendar.HOUR))
					+ ":"
					+ String.format("%02d", calendarEnd.get(Calendar.MINUTE))
					+ " "
					+ calendarEnd.getDisplayName(Calendar.AM_PM, Calendar.SHORT,
					Locale.getDefault()));
		}

		if(!listToShow.get(position).getLocation().isEmpty()) {
			holder.location.setText(listToShow.get(position).getLocation());
			holder.location.setVisibility(View.VISIBLE);
		}

		final Holder finalHolder = holder;

		if (listToShow.get(position).getIs_done() == null)
			listToShow.get(position).setIs_done(false);

		if(listToShow.get(position).getIs_done()){
			holder.completeTask.setChecked(true);
			finalHolder.title.setPaintFlags(finalHolder.title.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			AlphaManager.setAlpha(finalHolder.title, 0.4F);
		}

		holder.completeTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				ToDo obj = MainActivity.tododao.load(listToShow.get(position).getId());
				if (b) {
					finalHolder.title.setPaintFlags(finalHolder.title.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
					AlphaManager.setAlpha(finalHolder.title, 0.4F);
					obj.setIs_done(true);
				} else {
					finalHolder.title.setPaintFlags(finalHolder.title.getPaintFlags()
							& (~Paint.STRIKE_THRU_TEXT_FLAG));
					AlphaManager.setAlpha(finalHolder.title, 1F);
					obj.setIs_done(false);
				}
				MainActivity.tododao.update(obj);
			}
		});

		return view;
	}
	class Holder {
		TextView title, location, startDate, endDate, icon, startTime, endTime, type;
		CheckBox completeTask;
		ImageView reminder, repeat;
	}

	private boolean isEventOrSchedule(int todoTypeId){
		return todoTypeId == 2 || todoTypeId == 3;

	}
}