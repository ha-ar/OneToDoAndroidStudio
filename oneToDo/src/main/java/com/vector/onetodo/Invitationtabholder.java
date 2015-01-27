package com.vector.onetodo;

import android.support.v4.app.Fragment;
import android.widget.AbsListView;


public abstract class Invitationtabholder extends Fragment implements
		InvitationScrollHolder {

	protected InvitationScrollHolder mScrollTabHolder;

	public void setScrollTabHolder(InvitationScrollHolder scrollTabHolder) {
		mScrollTabHolder = scrollTabHolder;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int pagePosition) {
		// nothing
	}

}