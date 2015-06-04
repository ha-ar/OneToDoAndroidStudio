package com.vector.onetodo;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.astuetz.PagerSlidingTabStrip;
import com.vector.onetodo.utils.Utils;


public class Invitations extends Fragment{

	private ViewPager pager;
	private TabPagerAdapter1 tabPagerAdapter;
	private AQuery aq;
	private Display display;
	private Point size;

	public static Invitations newInstance() {
		return new Invitations();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.invitation, container, false);
		display = getActivity().getWindowManager().getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		aq = new AQuery(getActivity(), view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		aq.id(R.id.header_logo_inivit).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getFragmentManager().popBackStack();
			}
		});
		aq.id(R.id.organizer).text(
				Html.fromHtml("<i><small><font color=\"#c5c5c5\">"
						+ "Competitor ID: " + "</font></small></i>"
						+ "<font color=\"#47a842\">" + "compID" + "</font>"));

		pager = (ViewPager) getActivity().findViewById(R.id.pager_invit);

		tabPagerAdapter = new TabPagerAdapter1(getChildFragmentManager());
		pager.setAdapter(tabPagerAdapter);

		// Bind the tabs to the ViewPager
		final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) aq.id(
				R.id.tabs_invit).getView();
	 
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

	public class TabPagerAdapter1 extends FragmentPagerAdapter {


		public TabPagerAdapter1(FragmentManager fm) {
			super(fm);
		}


		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "RECEIVED";
			case 1:
				return "SENT";
			default:
				return "";// not the case

			}

		}

		@Override
		public Fragment getItem(int position) {
			return InvitationFragment.newInstance(position);
		}

	}


}