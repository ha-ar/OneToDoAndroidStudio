package com.vector.onetodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.roomorma.caldroid.CaldroidFragment;
import com.roomorma.caldroid.CaldroidListener;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao.Properties;
import com.vector.onetodo.utils.Constants;
import com.vector.onetodo.utils.TypeFaces;

import net.simonvt.datepicker.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class Calender extends Fragment {

    private AQuery aq;
    private ListView taskList;
    private CaldroidFragment caldroidFragment;
    private TaskListAdapter adapter;
    private Dialog date_time_alert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_calender, container, false);
        setHasOptionsMenu(true);
        aq = new AQuery(getActivity(), view);
        aq.id(R.id.add_task_button).text("Add")
                .typeface(TypeFaces.get(getActivity(), Constants.ICON_FONT))
                .clicked(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddTask.class);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                        getActivity().overridePendingTransition(
                                R.anim.bottom_up, R.anim.bottom_down);
                    }
                });

        return view;
    }

    private ActionBar actionBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actionBar = ((ActionBarActivity) activity).getSupportActionBar();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar cal = Calendar.getInstance();
        caldroidFragment = new CaldroidFragment();

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        } else {
            Bundle args = new Bundle();
            args.putBoolean(CaldroidFragment.SHOW_NAVIGATION_ARROWS, false);
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            caldroidFragment.setArguments(args);
        }
        List<ToDo> toDoList = MainActivity.tododao.loadAll();
        int size = toDoList.size();
        for(int i = 0; i < size; i++){
            if(toDoList.get(i).getStart_date() != null)
                caldroidFragment.setBackgroundResourceForDate(R.drawable.round_gray_transparent, new Date(toDoList.get(i).getStart_date()));
        }


        // Attach to the activity
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.calendar_caldroid, caldroidFragment);
        t.commit();

        taskList = aq.id(R.id.task_list_view).getListView();
        adapter = new TaskListAdapter(getActivity(), particularDateQuery(
                cal.getTimeInMillis(),
                cal.getTimeInMillis() + Constants.DAY).list());
        taskList.setAdapter(adapter);


        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                adapter = new TaskListAdapter(getActivity(), particularDateQuery(
                        date.getTime(),
                        date.getTime() + Constants.DAY).list());
                taskList.setAdapter(adapter);
                if (!isNoWorkDay(date.getTime())) {
                    taskList.setVisibility(View.VISIBLE);
                    aq.id(R.id.no_events).gone();
                }else{
                    taskList.setVisibility(View.GONE);
                    aq.id(R.id.no_events).visible();
                }

            }

            @Override
            public void onChangeMonth(int month, int year) {
//				String text = "month: " + month + " year: " + year;
//				Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
//				Toast.makeText(getActivity(),
//						"Long click " + formatter.format(date),
//						Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {

                }
            }

        };
        caldroidFragment.setCaldroidListener(listener);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.calendar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_today:
                Date today = new Date(System.currentTimeMillis());
                caldroidFragment.moveToDate(today);
                caldroidFragment.setCalendarDate(today);
                return true;
            case R.id.action_goto:
                showNumberDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private QueryBuilder<ToDo> particularDateQuery(long fromDate, long toDate) {
        return MainActivity.tododao.queryBuilder().where(
                Properties.Start_date.between(fromDate, toDate));
    }

    private boolean isNoWorkDay(long  date) {
        if (particularDateQuery(date,
                date + Constants.DAY).list()
                .size() > 0)
            return false;
        else
            return true;
    }
    private void showNumberDialog(){
        LayoutInflater inflater4 = getActivity().getLayoutInflater();
        View dateTimePickerDialog = inflater4.inflate(
                R.layout.date_time_layout_dialog, null, false);
        final AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity());
        builder4.setView(dateTimePickerDialog);
        date_time_alert = builder4.create();
        date_time_alert.show();

        aq = new AQuery(dateTimePickerDialog);
        aq.id(R.id.date_time_title).text("Goto");
        final DatePicker dPicker = (DatePicker) aq.id(R.id.date_picker_dialog).getView();
        aq.id(R.id.set).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aq.id(R.id.date_picker_dialog);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, dPicker.getMonth());
                calendar.set(Calendar.YEAR, dPicker.getYear());
                Date date = calendar.getTime();
                caldroidFragment.moveToDate(date);
                date_time_alert.dismiss();
            }
        });
        aq.id(R.id.cancel).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_time_alert.dismiss();
            }
        });
    }

}
