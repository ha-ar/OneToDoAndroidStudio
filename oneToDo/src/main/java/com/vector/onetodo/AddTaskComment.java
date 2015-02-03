package com.vector.onetodo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.vector.onetodo.utils.Utils;

public class AddTaskComment extends Fragment {

	AQuery aq;
	static int check = 0;
	int count;
	List<String> commment, date, time;
	String hms;
	comment_A adapter;
	SharedPreferences sprefrences;
	Editor editor;
	int MaxId, MinId;
	Boolean bool = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		View view = inflater.inflate(R.layout.comment, container, false);
		commment = new ArrayList<String>();
		time = new ArrayList<String>();
		date = new ArrayList<String>();
		aq = new AQuery(getActivity(), view);
		sprefrences = getActivity().getSharedPreferences("Comment", 0);
		editor = sprefrences.edit();
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AddTaskFragment.FragmentCheck = 0;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		Loadcommentmax();
		MinId = MaxId;
		AddTask.comment = new ArrayList<String>();
		AddTask.commenttime = new ArrayList<String>();

		aq.id(R.id.textView1).text(AddTaskFragment.title);
		adapter = new comment_A(getActivity());

		if (MaxId != 0) {
			for (int i = 1; i <= MaxId; i++) {
				commment.add(sprefrences.getString(1 + "comment" + i, null));
				time.add(sprefrences.getString(1 + "time" + i, null));

				aq.id(R.id.nocooment_layout).visibility(View.GONE);
				aq.id(R.id.comment_list).visibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
				aq.id(R.id.comment_list).getListView().setAdapter(adapter);
				aq.id(R.id.comment).text("");
			}
		}

		aq.id(R.id.comment_attachment).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AddTaskFragment.attach.show();
			}
		});

		aq.id(R.id.imageView3).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});

		aq.id(R.id.comment_send).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!(aq.id(R.id.comment).getText().toString().equals(""))) {

					long millis = System.currentTimeMillis();
					hms = String.format(
							"%02d:%02d:%02d",
							(TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS
									.toHours(TimeUnit.MILLISECONDS
											.toDays(millis))),
							TimeUnit.MILLISECONDS.toMinutes(millis)
									- TimeUnit.HOURS
											.toMinutes(TimeUnit.MILLISECONDS
													.toHours(millis)),
							TimeUnit.MILLISECONDS.toSeconds(millis)
									- TimeUnit.MINUTES
											.toSeconds(TimeUnit.MILLISECONDS
													.toMinutes(millis)));
					Loadcommentmax();
					MaxId = MaxId + 1;
					Savecomment(MaxId,
							aq.id(R.id.comment).getText().toString(), hms);
					time.add(hms);
					date.add(Utils.getCurrentYear(1) + "-"
							+ Utils.getCurrentMonthDigit(1) + "-"
							+ Utils.getCurrentDayDigit(1) + " " + hms);

					AddTask.comment.add(aq.id(R.id.comment).getText()
							.toString());

					AddTask.commenttime.add(Utils.getCurrentYear(1) + "-"
							+ Utils.getCurrentMonthDigit(1) + "-"
							+ Utils.getCurrentDayDigit(1) + " " + hms);
					aq.id(R.id.nocooment_layout).visibility(View.GONE);
					aq.id(R.id.comment_list).visibility(View.VISIBLE);
					commment.add(aq.id(R.id.comment).getText().toString());
					int size = commment.size();
					size = size + 1 - 1;
					adapter.notifyDataSetChanged();
					aq.id(R.id.comment_list).getListView().setAdapter(adapter);
					aq.id(R.id.comment).text("");
				}
			}
		}); 
	}

	public class comment_A extends BaseAdapter {

		Context context;

		public comment_A(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return commment.size();
		}

		@Override
		public java.lang.Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public class Holder {
			ImageView img;
			TextView comment, name, date;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View row = arg1;
			Holder holder = null;
			if (row == null) {
				LayoutInflater inflate = ((Activity) context)
						.getLayoutInflater();
				row = inflate.inflate(R.layout.add_comment_list_item, arg2,
						false);
				holder = new Holder();
				holder.comment = (TextView) row
						.findViewById(R.id.comment_comment);
				holder.name = (TextView) row.findViewById(R.id.comment_name);
				holder.date = (TextView) row.findViewById(R.id.comment_date);
				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();
			}
			holder.name.setText("Usman ameer");
			holder.date.setText("Today, " + time.get(position));
			holder.comment.setText(commment.get(position));
			return row;
		}
	}
 
	public void Savecomment(int id, String comment, String time) {
		// 0 - for private mode
		editor.putInt("cMax", id);
		editor.putString(1 + "comment" + id, comment);
		editor.putString(1 + "time" + id, time); // Storing float
		editor.commit();
	}

	public void Loadcommentmax() {
		MaxId = sprefrences.getInt("cMax", 0);
	}

	public void Loadcomment(int id) {
		sprefrences.getString(1 + "comment" + id, null);
		sprefrences.getString(1 + "time" + id, null); // getting String
	}

	public void Removecomment(int id) {
		editor.remove(1 + "comment" + id); // will delete key name
		editor.remove(1 + "time" + id); // will delete key email
		editor.commit();
	}

}