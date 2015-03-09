package com.vector.onetodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao.Properties;
import com.vector.onetodo.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.dao.query.QueryBuilder;

public class TaskListFragment extends ScrollTabHolderFragment implements
        OnScrollListener {

    public static ListView[] listView = new ListView[3];
    public static QueryBuilder<ToDo> todayQuery, tomorrowQuery, upcomingQuery;
    public static TaskListAdapter todayAdapter, tomorrowAdapter,
            upComingAdapter;
    public static int position;
    private static long[] currentDate;

    public static TaskListFragment newInstance(int position) {
        TaskListFragment myFragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_list, container, false);
        position = getArguments().getInt("position");
        listView[position] = (ListView) view.findViewById(R.id.task_list_view);
        listView[position].setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adpater, View view, int pos,
                                    long _id) {
                int id = 0;
                switch (MainActivity.pager.getCurrentItem()) {
                    case 0:
                        id = todayQuery.list().get(pos).getId().intValue();
                        break;
                    case 1:
                        id = tomorrowQuery.list().get(pos).getId().intValue();
                        break;
                    case 2:
                        id = upcomingQuery.list().get(pos).getId().intValue();
                        break;

                }
                Intent intent = new Intent(getActivity(), TaskView.class);
                intent.putExtra("todo_id", (long) id);
                startActivity(intent);
            }

        });

        currentDate = new long[3];
        String date_string = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i <= 2; i++) {
            date_string = Utils.getCurrentYear(i) + "-"
                    + (Utils.getCurrentMonthDigit(i) + 1) + "-"
                    + Utils.getCurrentDayDigit(i);
            try {
                Date mDate = sdf.parse(date_string);
                currentDate[i] = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdapter(getActivity(), position);

    }
    public static void todayQuery() {
        todayQuery = MainActivity.tododao.queryBuilder().where(
                Properties.Start_date.between(currentDate[0], currentDate[1]));
    }

    public static void tomorrowQuery() {
        tomorrowQuery = MainActivity.tododao.queryBuilder().where(
                Properties.Start_date.between(currentDate[1], currentDate[2]));
    }

    public static void upComingQuery() {
        upcomingQuery = MainActivity.tododao.queryBuilder().where(
                Properties.Start_date.gt(currentDate[2]));
    }

    public static void setAdapter(Context context, int position) {

        switch (position) {
            case 0:
                todayQuery();
                todayAdapter = new TaskListAdapter(context, todayQuery.list());
                listView[0].setAdapter(todayAdapter);
                break;
            case 1:
                tomorrowQuery();
                tomorrowAdapter = new TaskListAdapter(context,
                        tomorrowQuery.list());
                listView[1].setAdapter(tomorrowAdapter);
                break;
            case 2:
                upComingQuery();
                upComingAdapter = new TaskListAdapter(context,
                        upcomingQuery.list());
                listView[2].setAdapter(upComingAdapter);
                break;
            default:
                // nothing
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }
}