package com.vector.onetodo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vector.onetodo.db.gen.ToDo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.feio.android.checklistview.utils.AlphaManager;

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
			holder.time = (TextView) view.findViewById(R.id.list_time);
			holder.time1 = (TextView) view.findViewById(R.id.list_time1);
			holder.type = (TextView) view.findViewById(R.id.type);
            holder.completeTask = (CheckBox) view.findViewById(R.id.completed_task);
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
        final Holder finalHolder = holder;

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
        TextView title, location, time, icon, time1, type;
        CheckBox completeTask;
    }
}