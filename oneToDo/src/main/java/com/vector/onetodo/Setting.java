package com.vector.onetodo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.vector.onetodo.utils.Utils;

public class Setting extends Fragment {

	private AQuery aq, aqd;
	private AlertDialog alert;
	private int check = -1;
	private ToggleButton toggle;
	private RadioButton RB, RB1;
	private ActionBar actionBar;
	private RadioGroup radioGroup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings, container, false);
		toggle = (ToggleButton) view.findViewById(R.id.switch_event);
		aq = new AQuery(getActivity(), view);
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
		if (toolbar != null)
			((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
		actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		actionBar.setTitle("Settings");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.plain, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().getFragmentManager().popBackStack();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		LayoutInflater inf = getActivity().getLayoutInflater();
		View dialog = inf
				.inflate(R.layout.time_week_format_dialog, null, false);
		aqd = new AQuery(dialog);
		RB = (RadioButton) dialog.findViewById(R.id.radio_1);
		RB1 = (RadioButton) dialog.findViewById(R.id.radio_2);
//		radioGroup = (RadioGroup) view.findViewById(R.id.radio_time_date);
//		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				
//			}
//		});

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(dialog);
		alert = builder.create();
		aq.id(R.id.dateformat2).text(App.prefs.getDateFormat());
		aq.id(R.id.timeformat2).text(App.prefs.getTimeFormat());
		aq.id(R.id.weekstart2).text(App.prefs.getStartingWeekDay());

		aq.id(R.id.dateformat_lay).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				check = 1;
				aqd.id(R.id.radio_1).text("     DD.MM.YYYY");
				aqd.id(R.id.radio_2).text("     MM.DD.YYYY");
				aqd.id(R.id.time_date_title).text("Date format");
				if (App.prefs.getDateFormat().equals(RB.getText().toString().trim())) {
					RB.setChecked(true);
					RB1.setChecked(false);
				} else {
					RB1.setChecked(true);
					RB.setChecked(false);
				}
				alert.show();
			}
		});
		aq.id(R.id.timeformat_lay).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				check = 2;
				aqd.id(R.id.time_date_title).text("Time format");
				aqd.id(R.id.radio_1).text("     12 H");
				aqd.id(R.id.radio_2).text("     24 H");
				if (App.prefs.getTimeFormat().equals(RB.getText().toString().trim())) {
					RB.setChecked(true);
					RB1.setChecked(false);
				} else {
					RB1.setChecked(true);
					RB.setChecked(false);
				}
				alert.show();
			}
		});
		aq.id(R.id.weekstart_lay).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				check = 3;
				aqd.id(R.id.time_date_title).text("Week start");
				aqd.id(R.id.radio_1).text("     Monday");
				aqd.id(R.id.radio_2).text("     Saturday");
				if (App.prefs.getStartingWeekDay().equals(RB.getText().toString().trim())) {
					RB.setChecked(true);
					RB1.setChecked(false);
				} else {
					RB1.setChecked(true);
					RB.setChecked(false);
				}
				alert.show();
			}
		});

		aqd.id(R.id.cancel).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				check = -1;
				alert.dismiss();
			}
		});

		aqd.id(R.id.ok).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (check == 1) {
					App.prefs.setDateFromat(RB.getText().toString().trim());
					aq.id(R.id.dateformat2).text(RB.getText().toString().trim());
				} else if (check == 2) {
					App.prefs.setTimeFormat(RB.getText().toString().trim());
					aq.id(R.id.timeformat2).text(RB.getText().toString().trim());
				} else if (check == 3) {
					App.prefs.setStartingWeekDay(RB.getText().toString().trim());
						aq.id(R.id.weekstart2).text(RB.getText().toString().trim());
				}
				check = -1;
				alert.dismiss();
			}
		});

		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked == true) {
					aq.id(R.id.sound).textColorId(R.color._4d4d4d);
					aq.id(R.id.sound1).textColorId(R.color._777777);
					aq.id(R.id.vibrate).textColorId(R.color._4d4d4d);
					aq.id(R.id.dailyreview).textColorId(R.color._4d4d4d);
					aq.id(R.id.dailyreview1).textColorId(R.color._777777);

				} else {

					aq.id(R.id.sound).textColorId(R.color.hint_color);
					aq.id(R.id.sound1).textColorId(R.color.hint_color);
					aq.id(R.id.vibrate).textColorId(R.color.hint_color);
					aq.id(R.id.dailyreview).textColorId(R.color.hint_color);
					aq.id(R.id.dailyreview1).textColorId(R.color.hint_color);

				}
			}
		});

		setFont();
	}

	public void setFont() {

		Utils.RobotoRegular(getActivity(), aqd.id(R.id.cancel).getTextView());
		Utils.RobotoRegular(getActivity(), aqd.id(R.id.ok).getTextView());
		Utils.RobotoRegular(getActivity(), aqd.id(R.id.time_date_title)
				.getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.general).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.dateformat).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.timeformat).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.weekstart).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.notification)
				.getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.appnotification)
				.getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.sound).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.vibrate).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.dailyreview)
				.getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.dateformat2).getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.timeformat2).getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.weekstart2).getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.appnotification1)
				.getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.sound1).getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.dailyreview1)
				.getTextView());
	}

}