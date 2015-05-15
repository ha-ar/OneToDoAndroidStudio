package com.vector.onetodo;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
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

		switch (listToShow.get(position).getTodo_type_id()){
			case 1:  // for task
				holder.type.setText("Task");
				setStartTime(position, holder);
				if(dayPosition == 2) // for upcoming
					setStartDate(position, holder);
				showRepeatReminder(position, holder);
				break;
			case 2:  // for event
				holder.type.setText("Event");
				if(dayPosition == 0 || dayPosition == 1){
					setStartTime(position, holder);
					setEndTime(position, holder);
					if(!isOneDayEvent(position) && dayPosition == 0)
						setStartTimeWithStart(holder, "Start");
					else if(!isOneDayEvent(position) && dayPosition == 1){
						setStartTimeWithStart(holder, "All Day");
						holder.startTime.setVisibility(View.GONE);
						holder.endTime.setVisibility(View.GONE);
					}
					showRepeatReminder(position, holder);
				} else if (dayPosition == 2){
					if(!isOneDayEvent(position)){
						setStartTime(position, holder);
						setEndTime(position, holder);
						setStartDate(position, holder);
						setEndDate(position, holder);
						holder.reminder.setVisibility(View.GONE);
						holder.repeat.setVisibility(View.GONE);
					}else{
						setOneDayEventUpComing(position, holder);
					}
				}
				break;
			case 3:
				holder.type.setText("Schedule");
				if(dayPosition == 0 || dayPosition == 1){
					setStartTime(position, holder);
					setEndTime(position, holder);
					if(!isOneDayEvent(position) && dayPosition == 0)
						setStartTimeWithStart(holder, "Start");
					else if(!isOneDayEvent(position) && dayPosition == 1){
						setStartTimeWithStart(holder, "All Day");
						holder.startTime.setVisibility(View.GONE);
						holder.endTime.setVisibility(View.GONE);
					}
					showRepeatReminder(position, holder);
				} else if (dayPosition == 2){
					if(!isOneDayEvent(position)){
						setStartTime(position, holder);
						setEndTime(position, holder);
						setStartDate(position, holder);
						setEndDate(position, holder);
						holder.reminder.setVisibility(View.GONE);
						holder.repeat.setVisibility(View.GONE);
					}else{
						setOneDayEventUpComing(position, holder);
					}
				}
				break;
			case 4:
				holder.type.setText("Appointment");
				setStartTime(position, holder);
				if(dayPosition == 2) // for upcoming
					setStartDate(position, holder);
				showRepeatReminder(position, holder);
				break;
			case 5: // for project
				break;

		}

		view.setId((listToShow.get(position).getId().intValue()));
		holder.title.setText(listToShow.get(position).getTitle());

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

	private void setStartTimeWithStart(Holder holder, String keyword) {
		holder.startDate.setVisibility(View.VISIBLE);
		holder.startDate.setText(keyword);
	}

	class Holder {
		TextView title, location, startDate, endDate, icon, startTime, endTime, type;
		CheckBox completeTask;
		ImageView reminder, repeat;
	}

	private boolean isOneDayEvent(int position){
		Calendar calendar = Calendar.getInstance();
		Long startDate = listToShow.get(position).getStart_date();
		calendar.setTimeInMillis(startDate);

		Calendar calendarEnd = Calendar.getInstance();
		Long endDate = listToShow.get(position).getEnd_date();
		calendarEnd.setTimeInMillis(endDate);

		Log.e("start_day" + listToShow.get(position).getTitle(), calendar.get(Calendar.DATE) + "");
		Log.e("end_day"+ listToShow.get(position).getTitle() , calendarEnd.get(Calendar.DATE) + "");

		if(calendar.get(Calendar.DATE) == calendarEnd.get(Calendar.DATE))
			return true;
		else
			return false;

	}

	private void setStartDate(int position, Holder holder){
		Calendar calendar = Calendar.getInstance();
		Long startDate = listToShow.get(position).getStart_date();
		calendar.setTimeInMillis(startDate);

		holder.startDate.setVisibility(View.VISIBLE);
		holder.startDate.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.SHORT, Locale.getDefault())
				+ " "
				+ calendar.get(Calendar.DAY_OF_MONTH)
				+ " "
				+ calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
				Locale.getDefault()) + " " + calendar.get(Calendar.YEAR));
	}

	private void setStartTime(int position, Holder holder){
		Calendar calendar = Calendar.getInstance();
		Long startDate = listToShow.get(position).getStart_date();
		calendar.setTimeInMillis(startDate);

		holder.startTime.setVisibility(View.VISIBLE);
		holder.startTime.setText(String.format("%02d", calendar.get(Calendar.HOUR))
				+ ":"
				+ String.format("%02d", calendar.get(Calendar.MINUTE))
				+ " "
				+ calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT,
				Locale.getDefault()));

	}

	private void setEndDate(int position, Holder holder){
		Calendar calendarEnd = Calendar.getInstance();
		Long endDate = listToShow.get(position).getEnd_date();
		calendarEnd.setTimeInMillis(endDate);

		holder.endDate.setVisibility(View.VISIBLE);
		holder.endDate.setText(calendarEnd.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.SHORT, Locale.getDefault())
				+ " "
				+ calendarEnd.get(Calendar.DAY_OF_MONTH)
				+ " "
				+ calendarEnd.getDisplayName(Calendar.MONTH, Calendar.SHORT,
				Locale.getDefault()) + " " + calendarEnd.get(Calendar.YEAR));
	}

	private void setEndTime(int position, Holder holder){
		Calendar calendarEnd = Calendar.getInstance();
		Long endDate = listToShow.get(position).getEnd_date();
		calendarEnd.setTimeInMillis(endDate);

		holder.endTime.setVisibility(View.VISIBLE);
		holder.endTime.setText(String.format("%02d", calendarEnd.get(Calendar.HOUR))
				+ ":"
				+ String.format("%02d", calendarEnd.get(Calendar.MINUTE))
				+ " "
				+ calendarEnd.getDisplayName(Calendar.AM_PM, Calendar.SHORT,
				Locale.getDefault()));
	}

	private void setOneDayEventUpComing(int position, Holder holder){
		Calendar calendar = Calendar.getInstance();
		Long startDate = listToShow.get(position).getStart_date();
		calendar.setTimeInMillis(startDate);

		Calendar calendarEnd = Calendar.getInstance();
		Long endDate = listToShow.get(position).getEnd_date();
		calendarEnd.setTimeInMillis(endDate);

		holder.startDate.setVisibility(View.VISIBLE);
		holder.startDate.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
				+ " " + calendar.get(Calendar.DATE)
				+ " " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
				+ " " + calendar.get(Calendar.YEAR));

		holder.startTime.setVisibility(View.VISIBLE);
		holder.startTime.setText(String.format("%02d", calendar.get(Calendar.HOUR))
				+ ":" + String.format("%02d", calendar.get(Calendar.MINUTE))
				+ " " + calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault())
				+ "\u2015" + String.format("%02d", calendarEnd.get(Calendar.HOUR))
				+ ":" + String.format("%02d", calendarEnd.get(Calendar.MINUTE))
				+ " " + calendarEnd.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault()));

		showRepeatReminder(position, holder);

	}

	private void showRepeatReminder(int position, Holder holder){
		try {
			if (!(TextUtils.isEmpty(listToShow.get(position).getRepeat().getRepeat_interval()))
					&& !(listToShow.get(position).getRepeat().getRepeat_interval().equalsIgnoreCase("Never"))){
				holder.repeat.setVisibility(View.VISIBLE);
			}
		}catch (Exception e){
			holder.repeat.setVisibility(View.GONE);
		}
		try {
			if (listToShow.get(position).getReminder().getTime() != 0)
				holder.reminder.setVisibility(View.VISIBLE);
		}catch (Exception e){
			holder.reminder.setVisibility(View.GONE);
		}
	}

}