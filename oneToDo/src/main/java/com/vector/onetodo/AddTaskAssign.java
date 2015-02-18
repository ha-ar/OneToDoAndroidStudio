package com.vector.onetodo;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.androidquery.AQuery;
import com.astuetz.PagerSlidingTabStrip;
import com.vector.onetodo.utils.Utils;

public class AddTaskAssign extends Fragment {

	AlertDialog dialog,Invite_selection, Invite;
	AQuery  aq, aq_menu,aq_selection,aq_invite;
	public int check = 0, position = 0;
    private PopupWindow popupWindowTask;
	
	public static AddTaskAssign newInstance(int position) {
		AddTaskAssign myFragment = new AddTaskAssign();
		Bundle args = new Bundle();
		args.putInt("position", position);
		myFragment.setArguments(args);
		return myFragment;
	}
 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		View view = inflater
				.inflate(R.layout.add_task_assign, container, false); 
		aq = new AQuery(getActivity(), view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		final View viewMenu = getActivity().getLayoutInflater().inflate(
				R.layout.landing_menu, null, false);
		aq_menu = new AQuery(getActivity(), viewMenu);
		aq_menu.id(R.id.menu_item1).text("Invite contacts");
		aq_menu.id(R.id.menu_item2).text("Search");

		position = getArguments().getInt("position");
		//******************** Dialogs
		View selection = getActivity().getLayoutInflater().inflate(
				R.layout.add_task_assign_email, null, false);
		aq_selection = new AQuery(selection);
		AlertDialog.Builder builderLabel = new AlertDialog.Builder(
				getActivity());
		builderLabel.setView(selection);
		Invite_selection = builderLabel.create();
		
		View invite = getActivity().getLayoutInflater().inflate(
				R.layout.add_task_invite, null, false);
		aq_invite = new AQuery(invite);
		AlertDialog.Builder builderLabel1 = new AlertDialog.Builder(
				getActivity());
		builderLabel1.setView(invite);
		Invite = builderLabel1.create();
		aq_invite.id(R.id.cancel).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Invite.dismiss();
			}
		});
		
		aq_selection.id(R.id.email).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				Invite.show();
				Invite_selection.dismiss();
			}
		});
		
		//******************** POPUP WINDOW
		popupWindowTask = new PopupWindow(viewMenu, Utils.getDpValue(200,
				getActivity()), WindowManager.LayoutParams.WRAP_CONTENT, true);

		popupWindowTask.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.dialog_holo_light_frame));
		popupWindowTask.setOutsideTouchable(true); 

		aq.id(R.id.assign_menu).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (popupWindowTask.isShowing()) {
					popupWindowTask.dismiss();

				} else { 
					popupWindowTask.showAsDropDown(aq.id(R.id.assign_menu)
							.getView(), 5, 10);
					aq.id(R.id.assign_menu).image(R.drawable.ic_show_black);
				}
			}
		});

		aq_menu.id(R.id.menu_item1).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindowTask.dismiss();
				Invite_selection.show();

			}
		});
		
		popupWindowTask.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				aq.id(R.id.assign_menu).image(R.drawable.ic_show_white);
			}
		});
		

		aq.id(R.id.assign_back).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
		
		
		//****************** PAGER BINDE WITH TAB
        ViewPager pager = (ViewPager) getActivity().findViewById(R.id.assign_pager);
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
		pager.setAdapter(tabPagerAdapter);

		// Bind the tabs to the ViewPager
		final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) aq.id(
				R.id.assing_tabs).getView();

		tabs.setShouldExpand(false);
		tabs.setDividerColorResource(android.R.color.transparent);
		tabs.setUnderlineColorResource(android.R.color.transparent);
		tabs.setTextSize(Utils.getPxFromDp(getActivity(), (int) 17.78));
		tabs.setIndicatorHeight(Utils.getPxFromDp(getActivity(), 3));
		tabs.setMinimumWidth(Utils.getPxFromDp(getActivity(), 100));
		tabs.setIndicatorColor(Color.parseColor("#ffffff"));
		tabs.setTextColor(Color.parseColor("#ffffff"));
		tabs.setSmoothScrollingEnabled(true);
		tabs.setShouldExpand(true);
		tabs.setAllCaps(false);
		tabs.setTypeface(null, Typeface.NORMAL);
		tabs.setViewPager(pager);
		tabPagerAdapter.notifyDataSetChanged();

	}

	 

	public class TabPagerAdapter extends FragmentPagerAdapter {

		private SparseArrayCompat<ProjectsScrollHolder> mScrollTabHolders;
		private ProjectsScrollHolder mListener;

		public TabPagerAdapter(FragmentManager fm) {
			super(fm);
			mScrollTabHolders = new SparseArrayCompat<ProjectsScrollHolder>();
		}

		public void setTabHolderScrollingContent(ProjectsScrollHolder listener) {
			mListener = listener;
		}

		@Override
		public int getCount() {
			return 2;
			// return 3; // no. of tabs are Today, Tomorrow & Upcoming
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "RECENT";
			case 1:
				return "CONTACTS";
			default:
				return "";// not the case
			}

		}

		public SparseArrayCompat<ProjectsScrollHolder> getScrollTabHolders() {
			return mScrollTabHolders;
		}

		@Override
		public Fragment getItem(int position) {
			ProjectsTabHolder fragment;
			if(AddTaskAssign.this.position == 1 || AddTaskAssign.this.position == 4){
				fragment = AssignMultipleFragment
						.newInstance(position);
			}else
				fragment = AssignListFragment
					.newInstance(position);

			mScrollTabHolders.put(position, fragment);
			if (mListener != null) {
				fragment.setScrollTabHolder(mListener);
			}
			return fragment;
		}
	}
	
}