package com.vector.onetodo;

import android.support.v4.app.Fragment;
import android.widget.AbsListView;

public abstract class ProjectsTabHolder extends Fragment implements
		ProjectsScrollHolder {

	protected ProjectsScrollHolder mScrollTabHolder;

	public void setScrollTabHolder(ProjectsScrollHolder scrollTabHolder) {
		mScrollTabHolder = scrollTabHolder;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int pagePosition) {
		// nothing
	}

}