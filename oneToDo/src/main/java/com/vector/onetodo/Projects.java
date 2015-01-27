package com.vector.onetodo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.astuetz.PagerSlidingTabStrip;
import com.vector.onetodo.utils.Utils;

public class Projects extends Fragment implements ProjectsScrollHolder {

	AQuery aq;
	TextView title;
	private ViewPager pager;
	private TabPagerAdapterpro tabPagerAdapter;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.projects, container, false);
		aq=new AQuery(getActivity(), view); 
		
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		aq.id(R.id.organizer).text(
				Html.fromHtml("<i><small><font color=\"#c5c5c5\">"
						+ "Competitor ID: " + "</font></small></i>"
						+ "<font color=\"#47a842\">" + "compID" + "</font>"));

		pager = (ViewPager) getActivity().findViewById(R.id.pager_projects);

		tabPagerAdapter = new TabPagerAdapterpro(getChildFragmentManager());
		tabPagerAdapter.setTabHolderScrollingContent(new ProjectsScrollHolder() {
			
	 
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount, int pagePosition) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void adjustScroll(int scrollHeight) {
				// TODO Auto-generated method stub
				
			}
		});

		pager.setAdapter(tabPagerAdapter);

		// Bind the tabs to the ViewPager
		final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) aq.id(
				R.id.tabs_projects).getView();
	 
		tabs.setShouldExpand(false);
		tabs.setDividerColorResource(android.R.color.transparent); 
		tabs.setUnderlineColorResource(android.R.color.transparent);
		tabs.setTextSize(Utils.getPxFromDp(getActivity(), 14));
		tabs.setIndicatorHeight(Utils.getPxFromDp(getActivity(), 3));

		tabs.setMinimumWidth(Utils.getPxFromDp(getActivity(), 200));

		tabs.setIndicatorColor(Color.parseColor("#ffffff"));
		tabs.setTextColor(Color.parseColor("#ffffff"));
		tabs.setSmoothScrollingEnabled(true);
		tabs.setShouldExpand(false); 
		tabs.setAllCaps(false);
		tabs.setTypeface(null, Typeface.NORMAL); 

		tabs.setViewPager(pager);
		tabPagerAdapter.notifyDataSetChanged();
	}
	
	
	
	public class TabPagerAdapterpro extends FragmentPagerAdapter {

		private SparseArrayCompat<ProjectsScrollHolder> mScrollTabHolders;
		private ProjectsScrollHolder mListener;

		public TabPagerAdapterpro(FragmentManager fm) {
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
				return "MY PROJECTS";
			case 1:
				return "SHARED PROJECTS";
			default:
				return "";// not the case

			}

		}

	

		public SparseArrayCompat<ProjectsScrollHolder> getScrollTabHolders() {
			return mScrollTabHolders;
		}

		@Override
		public Fragment getItem(int position) {
			ProjectsTabHolder fragment = 
					(ProjectsTabHolder) ProjectsListFragment.newInstance(position);

			mScrollTabHolders.put(position, fragment);
			if (mListener != null) {
				fragment.setScrollTabHolder(mListener);
			}
			return fragment;
		}

	}

	@Override
	public void adjustScroll(int scrollHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int pagePosition) {
		// TODO Auto-generated method stub
		
	}

}
