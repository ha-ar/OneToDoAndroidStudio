package com.vector.onetodo;

import android.app.Fragment;

//import android.app.Activity;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//
//import com.androidquery.AQuery;
//import com.vector.calendar.CollapseCalendarView;
//import com.vector.calendar.manager.CalendarManager;
//import com.vector.onetodo.db.gen.ToDo;
//import com.vector.onetodo.db.gen.ToDoDao.Properties;

public class Calender extends Fragment {
//        implements
//		CollapseCalendarView.OnDateSelect {

//	private AQuery aq;
//	private ListView taskList;
//	private QueryBuilder<ToDo> currentDayQuery;
//	private CollapseCalendarView collapseCalendar;
//	private TaskListAdapter adapter;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.task_calender, container, false);
//		aq = new AQuery(getActivity(), view);
//
//		return view;
//	}
//
//	private ActionBar actionBar;
//	private CalendarManager manager;
//
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		actionBar = ((ActionBarActivity) activity).getSupportActionBar();
//
//	}
//
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		actionBar.setTitle("Calendar");
//		setHasOptionsMenu(true);
//		manager = new CalendarManager(LocalDate.now(),
//				CalendarManager.State.MONTH, LocalDate.now(), LocalDate.now()
//						.plusYears(1));
//
//		taskList = (ListView) view.findViewById(R.id.task_list_view);
//		LocalDate date = manager.getSelectedDay();
//		Long selectedDate = date.toDateTimeAtStartOfDay().getMillis();
//		Long toDate = date.plusDays(1).toDateTimeAtStartOfDay().getMillis();
//		adapter = new TaskListAdapter(getActivity(), particularDateQuery(
//				selectedDate, toDate).list());
//		taskList.setAdapter(adapter);
//
//		if (!isNoWorkDay(date)) {
//			taskList.setVisibility(View.VISIBLE);
//			aq.id(R.id.no_events).gone();
//		}else{
//			taskList.setVisibility(View.GONE);
//			aq.id(R.id.no_events).visible();
//		}
//
//		collapseCalendar = (CollapseCalendarView) view
//				.findViewById(R.id.calendar);
//		collapseCalendar.init(manager);
//		collapseCalendar.setListener(this);
//
//	}
//
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		super.onCreateOptionsMenu(menu, inflater);
//		menu.clear();
//		inflater.inflate(R.menu.calendar, menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.action_search:
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	private QueryBuilder<ToDo> particularDateQuery(long fromDate, long toDate) {
//		return MainActivity.tododao.queryBuilder().where(
//				Properties.Start_date.between(fromDate, toDate));
//	}
//
//	@Override
//	public void onDateSelected(LocalDate date) {
//		adapter = new TaskListAdapter(getActivity(), particularDateQuery(
//				date.toDateTimeAtStartOfDay().getMillis(),
//				date.plusDays(1).toDateTimeAtStartOfDay().getMillis()).list());
//		taskList.setAdapter(adapter);
//		adapter.notifyDataSetChanged();
//
//		if (!isNoWorkDay(date)) {
//			taskList.setVisibility(View.VISIBLE);
//			aq.id(R.id.no_events).gone();
//		}else{
//			taskList.setVisibility(View.GONE);
//			aq.id(R.id.no_events).visible();
//		}
//	}
//
//	private boolean isNoWorkDay(LocalDate date) {
//		if (particularDateQuery(date.toDateTimeAtStartOfDay().getMillis(),
//				date.plusDays(1).toDateTimeAtStartOfDay().getMillis()).list()
//				.size() > 0)
//			return false;
//		else
//			return true;
//	}

}
