package com.vector.onetodo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.vector.model.TaskComments;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AddTaskComment extends Fragment {

	private AQuery aq;
	public static List<String> commment, date, time, comment, commenttime;
	private String hms;
	private comment_A adapter;
	private SharedPreferences sprefrences;
	private Editor editor;
	private int MaxId, serverID;
	private boolean isAssignedTask;
	private long todoId;

	public static AddTaskComment newInstance(boolean isAssignedTask, long todoId) {
		AddTaskComment myFragment = new AddTaskComment();
		Bundle args = new Bundle();
		args.putBoolean("is_assigned_task", isAssignedTask);
		args.putLong("todo_id", todoId);
		myFragment.setArguments(args);
		return myFragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		setRetainInstance(true);
		View view = inflater.inflate(R.layout.comment, container, false);
		isAssignedTask = getArguments().getBoolean("is_assigned_task");
		todoId = getArguments().getLong("todo_id", -1);
		commment = new ArrayList<>();
		time = new ArrayList<>();
		date = new ArrayList<>();
		aq = new AQuery(getActivity(), view);
		sprefrences = getActivity().getSharedPreferences("Comment", 0);
		editor = sprefrences.edit();
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AddTaskFragment.FragmentCheck = 0;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

//		Loadcommentmax();
		comment = new ArrayList<>();
		commenttime = new ArrayList<>();

		adapter = new comment_A(getActivity());

//		if ((todoId == -1) && (MaxId > 0)) {
//			for (int i = 1; i <= MaxId; i++) {
//				commment.add(sprefrences.getString(1 + "comment" + i, null));
//				time.add(sprefrences.getString(1 + "time" + i, null));
//
//				aq.id(R.id.nocooment_layout).visibility(View.GONE);
//				aq.id(R.id.comment_list).visibility(View.VISIBLE);
//				adapter.notifyDataSetChanged();
//				aq.id(R.id.comment_list).getListView().setAdapter(adapter);
//				aq.id(R.id.comment).text("");
//			}
//			aq.id(R.id.nocooment_layout).visibility(View.GONE);
//			aq.id(R.id.comment_list).visibility(View.VISIBLE);
//			adapter.notifyDataSetChanged();
//			aq.id(R.id.comment_list).getListView().setAdapter(adapter);
//			aq.id(R.id.comment).text("");
//		}else
//
		ToDo obj;
		if(todoId != -1){
			ToDoDao toDoDao = App.daoSession.getToDoDao();
			obj = toDoDao.load(todoId);
			serverID = obj.getTodo_server_id();
			getComments(obj.getTodo_server_id());
		}

		aq.id(R.id.comment_attachment).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				AddTaskFragment.attach.show();
			}
		});

		aq.id(R.id.imageView3).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getFragmentManager().popBackStack();
			}
		});

		aq.id(R.id.comment_send).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
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
//					Loadcommentmax();
//					MaxId = MaxId + 1;
//					Savecomment(MaxId,
//							aq.id(R.id.comment).getText().toString(), hms);
					String dateTime = Utils.getCurrentYear(1) + "-"
							+ Utils.getCurrentMonthDigit(1) + "-"
							+ Utils.getCurrentDayDigit(1) + " " + hms;
					time.add(hms);
					date.add(dateTime);

					comment.add(aq.id(R.id.comment).getText()
							.toString());

					commenttime.add(Utils.getCurrentYear(1) + "-"
							+ Utils.getCurrentMonthDigit(1) + "-"
							+ Utils.getCurrentDayDigit(1) + " " + hms);
					aq.id(R.id.nocooment_layout).visibility(View.GONE);
					aq.id(R.id.comment_list).visibility(View.VISIBLE);
					commment.add(aq.id(R.id.comment).getText().toString());


					TaskComments.getInstance().addComment(aq.id(R.id.comment).getText().toString(), App.prefs.getUserName(), hms);
					sendComments(aq.id(R.id.comment).getText().toString(),dateTime);
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
			return commment.size();
		}

		@Override
		public java.lang.Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public class Holder {
			ImageView img;
			TextView comment, name, date, initials;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
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
				holder.initials = (TextView) row.findViewById(R.id.initials);
				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();
			}
			String userName = "", initials= "";

			if(todoId == -1) {
				userName = App.prefs.getUserName();
				initials = App.prefs.getInitials();
			} else{
				userName = TaskComments.getInstance().comments.get(position).name;
				String[] temp = TaskComments.getInstance().comments.get(position).name.split(" ");
				try {
					initials = Utils.getInitials(temp[0], temp[1]);
				}catch (Exception e){
					initials = "GU";
				}
			}

			holder.name.setText(userName);
			holder.date.setText(time.get(position));
			holder.comment.setText(commment.get(position));
			holder.initials.setText(initials);
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


	private void getComments(int todoID){
		aq.progress(aq.id(R.id.dialog).getProgressBar()).ajax("http://api.heuristix.net/one_todo/v1/comment/"
						+ todoID, JSONObject.class,
				new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject json,
										 AjaxStatus status) {
						if (json != null) {
							Gson gson = new Gson();
							TaskComments obj = gson.fromJson(json.toString(),
									TaskComments.class);
							TaskComments.getInstance().setObj(obj);
							for (int i = 0; i < TaskComments.getInstance().comments.size(); i++) {
								commment.add(TaskComments.getInstance().comments.get(i).comment);
								time.add(TaskComments.getInstance().comments.get(i).time);
							}
							aq.id(R.id.nocooment_layout).visibility(View.GONE);
							aq.id(R.id.comment_list).visibility(View.VISIBLE);
							adapter.notifyDataSetChanged();
							aq.id(R.id.comment_list).getListView().setAdapter(adapter);
							aq.id(R.id.comment).text("");
						}

					}
				});
	}
	private void sendComments(String comment, String dateTime){
		if(todoId == -1)
			return;

		Map<String, String> params = new HashMap<>();
		params.put("todo_id", String.valueOf(serverID));
		params.put("user_id", String.valueOf(App.prefs.getUserId()));
		params.put("comment", comment);
		params.put("date_time", dateTime);
		aq.ajax("http://api.heuristix.net/one_todo/v1/comment", params, JSONObject.class,
				new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject json,
										 AjaxStatus status) {
						if (json != null) {
							Log.e("Comment added", json.toString());
						}

					}
				});
	}

}