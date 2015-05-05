package com.vector.onetodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao.Properties;
import com.vector.onetodo.utils.Constants;
import com.vector.onetodo.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

public class TaskListFragment extends Fragment{

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
        String date_string;
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
        setAdapter(getActivity(), position, MainActivity.currentCondition);

    }
    public static void todayQuery() {
        todayQuery = App.daoSession.getToDoDao().queryBuilder()
                .whereOr(Properties.Start_date.between(currentDate[0], currentDate[1]),
                        Properties.Start_date.between(currentDate[0], currentDate[1]))
                .orderAsc(Properties.Start_date);
    }

    public static void tomorrowQuery() {
        tomorrowQuery = App.daoSession.getToDoDao().queryBuilder()
                .whereOr(Properties.Start_date.between(currentDate[1], currentDate[2]),
                        Properties.End_date.between(currentDate[1], currentDate[2]))
                .orderAsc(Properties.Start_date);
    }

    public static void upComingQuery() {
        upcomingQuery = App.daoSession.getToDoDao().queryBuilder().where(
                Properties.Start_date.gt(currentDate[2]))
                .orderAsc(Properties.Start_date);
    }

    private static void filteredQuery(WhereCondition properties, int day){

        switch (day){
            case Constants.TODAY:
                todayQuery =  App.daoSession.getToDoDao().queryBuilder().where(
                        Properties.Start_date.between(currentDate[0], currentDate[1]), properties)
                        .orderAsc(Properties.Start_date);
            case Constants.TOMORROW:
                tomorrowQuery = App.daoSession.getToDoDao().queryBuilder().where(
                        Properties.Start_date.between(currentDate[1], currentDate[2]), properties)
                        .orderAsc(Properties.Start_date);
            case Constants.UPCOMING:
                upcomingQuery = App.daoSession.getToDoDao().queryBuilder().where(
                        Properties.Start_date.gt(currentDate[2]), properties)
                        .orderAsc(Properties.Start_date);

        }
    }


    public static void setAdapter(Context context, int position, WhereCondition property) {

        switch (position) {
            case 0:
                if (property == null)
                    todayQuery();
                else filteredQuery(property, position);

                todayAdapter = new TaskListAdapter(context, todayQuery.list());
                listView[0].setAdapter(todayAdapter);
                todayAdapter.notifyDataSetChanged();
                break;
            case 1:
                if (property == null)
                    tomorrowQuery();
                else filteredQuery(property,position);
                tomorrowAdapter = new TaskListAdapter(context,
                        tomorrowQuery.list());
                listView[1].setAdapter(tomorrowAdapter);
                tomorrowAdapter.notifyDataSetChanged();
                break;
            case 2:
                if(property == null)
                    upComingQuery();
                else filteredQuery(property,position);

                upComingAdapter = new TaskListAdapter(context,
                        upcomingQuery.list());
                listView[2].setAdapter(upComingAdapter);
                upComingAdapter.notifyDataSetChanged();
                break;
            default:

                break;
        }
    }

}