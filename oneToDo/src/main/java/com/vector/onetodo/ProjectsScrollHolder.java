package com.vector.onetodo;

import android.widget.AbsListView;

public interface ProjectsScrollHolder {

	void adjustScroll(int scrollHeight);

	void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount, int pagePosition);
}
