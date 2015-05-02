package com.vector.onetodo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao.Properties;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class TaskByLabelFragment extends Fragment{

    public static ListView[] listView;
    public static TaskListAdapter[] labelAdapter;
    public static int position, size;
    private static String labelName;

    public static TaskByLabelFragment newInstance(int position, String labelName, int size) {
        TaskByLabelFragment myFragment = new TaskByLabelFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("size", size);
        args.putString("label_name", labelName);
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
        labelName = getArguments().getString("label_name");
        if(labelName == null)
            labelName = "No Label";
        listView = new ListView[size];
        labelAdapter = new TaskListAdapter[size];

//        listView[position] = (ListView) view.findViewById(R.id.task_list_view);
//        listView[position].setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adpater, View view, int pos,
//                                    long _id) {
//                int id = 0;
//                switch (MainActivity.pager.getCurrentItem()) {
//                    case 0:
//                        id = filteredQuery().list().get(pos).getId().intValue();
//                        break;
//                    case 1:
//                        id = filteredQuery().list().get(pos).getId().intValue();
//                        break;
//                    case 2:
//                        id = filteredQuery().list().get(pos).getId().intValue();
//                        break;
//
//                }
//                Intent intent = new Intent(getActivity(), TaskView.class);
//                intent.putExtra("todo_id", (long) id);
//                startActivity(intent);
//            }
//
//        });


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdapter(getActivity(), position);

    }

    private static QueryBuilder<ToDo> filteredQuery(){
        return App.daoSession.getToDoDao()
                .queryBuilder()
                .orderAsc(Properties.Start_date);
    }


    public static void setAdapter(Context context, int position) {
        List<ToDo> list = filteredQuery().list();
        for(ToDo todo : filteredQuery().list()){
            if(todo.getLabel().getLabel_name() != null)
                if(todo.getLabel().getLabel_name().equals(labelName))
                    list.add(todo);
        }
        labelAdapter[position] = new TaskListAdapter(context, list);
        listView[position].setAdapter(labelAdapter[position]);
        labelAdapter[0].notifyDataSetChanged();
    }

}