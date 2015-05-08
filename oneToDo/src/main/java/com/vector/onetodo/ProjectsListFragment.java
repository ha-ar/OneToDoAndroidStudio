package com.vector.onetodo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;

import de.greenrobot.dao.query.QueryBuilder;

public class ProjectsListFragment extends Fragment {

    private TaskListAdapter taskAdapter;


    public static ProjectsListFragment newInstance(int position) {
		ProjectsListFragment myFragment = new ProjectsListFragment();
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
		View view = inflater
				.inflate(R.layout.tasks_list, container, false);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        int position = getArguments().getInt("position");
        ListView listView = (ListView) view.findViewById(R.id.task_list_view);
        if(position == 0){
            QueryBuilder<ToDo> projectsQuery = MainActivity.tododao.queryBuilder().where(
                    ToDoDao.Properties.Todo_type_id.eq(5));
            taskAdapter = new TaskListAdapter(getActivity(), projectsQuery.list(), 2);
            listView.setAdapter(taskAdapter);
        }

	}


}