package com.vector.onetodo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vector.onetodo.utils.Utils;

public class InvitationFragment extends Invitationtabholder implements
		OnScrollListener {

	private ListView listView;
	private int position;

	private static long[] Currentdate;
 

	public static InvitationFragment newInstance(int position) {
		InvitationFragment myFragment = new InvitationFragment();
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
				.inflate(R.layout.invitation_list, container, false);
		listView = (ListView) view.findViewById(R.id.invitation_list_view);

		Currentdate = new long[3];
		String date_string = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i <= 2; i++) {
			date_string = Utils.getCurrentYear(i) + "-"
					+ (Utils.getCurrentMonthDigit(i) + 1) + "-"
					+ Utils.getCurrentDayDigit(i);
			try {
				Date mDate = sdf.parse(date_string);
				Currentdate[i] = mDate.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		position = getArguments().getInt("position");
		setadapter(getActivity(), position);
		listView.setOnScrollListener(this);
		listView.setAdapter(new LandingAdapter(getActivity()));

	}

 

	public class LandingAdapter extends BaseAdapter {

		Context context;

		public LandingAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int ID) {
			return ID;
		}

		@Override
		public View getView(int position, View view1, ViewGroup parent) {
			View view = view1;

			Holder holder = null;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.invitation_layout, parent,
						false);
				holder = new Holder();
				holder.Details_layout = (RelativeLayout) view
						.findViewById(R.id.details_layout);
				holder.title = (TextView) view
						.findViewById(R.id.invit_list_title);
				holder.ShowDetails = (TextView) view
						.findViewById(R.id.showdetails);
				view.setTag(holder);
			} else {
				holder = (Holder) view.getTag();
			}
			holder.title.setText("Ttile " + position + 1);

			holder.ShowDetails.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub 
					Toast.makeText(getActivity(), "asda", Toast.LENGTH_LONG)
							.show();
				}
			});

			return view;
		}
	}

	class Holder {
		TextView title, ShowDetails, time, icon;
		RelativeLayout Details_layout;
	}

	@Override
	public void adjustScroll(int scrollHeight) {
		if (scrollHeight == 0 && listView.getFirstVisiblePosition() >= 1) {
			return;
		}

		listView.setSelectionFromTop(1, scrollHeight);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mScrollTabHolder != null)
			mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount, position);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// nothing
	}

	private void setadapter(Context context, int position) { 
		LandingAdapter adapter = new LandingAdapter(getActivity());
		listView.setAdapter(adapter);
	}
}