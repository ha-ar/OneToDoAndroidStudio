package com.vector.onetodo;

import net.simonvt.numberpicker.NumberPicker;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.androidquery.AQuery;

public class AddAppoinmentBeforeFragment extends Fragment {

	int position;
	AQuery aq, aqd, aq_edit, aq_del;
	TextView before;
	Editor editor;
	AlertDialog alert, location, label,location_edit ,location_del;
	static final String[] beforeArray = new String[] { "On Time", "15 Mins",
			"30 Mins", "2 Hours", "Custom" };
	static final String[] values = { "Mins", "Hours", "Days", "Weeks",
			"Months", "Years" };
	static View viewP, viewl, button = null, temp;
	String Title, pname = null, padress = null;
	private static View previousSelected;
	private static View previousSelectedLocation;

	public static AddAppoinmentBeforeFragment newInstance(int position) {
		AddAppoinmentBeforeFragment myFragment = new AddAppoinmentBeforeFragment();

		Bundle args = new Bundle();
		args.putInt("position", position);
		myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		editor = AddTask.pref.edit();
		position = getArguments().getInt("position", 0);
		before = (TextView) getActivity().findViewById(R.id.before_appoinment);
		View view;
		if (position == 0)
			view = inflater.inflate(R.layout.add_appoinment_before_grid,
					container, false);
		else
			view = inflater.inflate(R.layout.add_appoinment_location,
					container, false);
		aq = new AQuery(getActivity(), view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setRetainInstance(true);
		LayoutInflater inflater5 = getActivity().getLayoutInflater();

		View dialoglayout6 = inflater5.inflate(R.layout.add_task_edit, null,
				false);
		aq_edit = new AQuery(dialoglayout6);
		AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
		builder6.setView(dialoglayout6);
		location_edit = builder6.create();

		View dialoglayout7 = inflater5.inflate(R.layout.add_task_edit_delete,
				null, false);
		aq_del = new AQuery(dialoglayout7);
		AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
		builder7.setView(dialoglayout7);
		location_del = builder7.create();

		if (position == 0) {

			aq.id(R.id.notification_radio_appoin).getCheckBox()
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (((CheckBox) arg0).isChecked()) {
								aq.id(R.id.notification_radio_appoin).textColor(
										getResources()
												.getColor(R.color._4d4d4d));
							} else {

								aq.id(R.id.notification_radio_appoin).textColor(
										Color.parseColor("#bababa"));
							}
						}
					});

			aq.id(R.id.email_radio_appoin).getCheckBox()
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (((CheckBox) arg0).isChecked()) {
								aq.id(R.id.email_radio_appoin).textColor(
										getResources()
												.getColor(R.color._4d4d4d));
							} else {

								aq.id(R.id.email_radio_appoin).textColor(
										Color.parseColor("#bababa"));
							}
						}
					});
			aq.id(R.id.before_grid_view)
					.getGridView()
					.setAdapter(
							new ArrayAdapter<String>(getActivity(),
									R.layout.grid_layout_textview, beforeArray) {

								@Override
								public View getView(int position,
										View convertView, ViewGroup parent) {

									TextView textView = (TextView) super
											.getView(position, convertView,
													parent);
									if (textView.getText().toString()
											.equals("15 Mins")) {

										previousSelected = textView;
										((TextView) textView)
												.setBackgroundResource(R.drawable.round_buttons_blue);
										((TextView) textView)
												.setTextColor(Color.WHITE);

									} else
										((TextView) textView)
												.setTextColor(getResources()
														.getColor(
																R.color._4d4d4d));
									return textView;
								}

							});

			View dialoglayout = getActivity().getLayoutInflater().inflate(
					R.layout.custom_number_picker_dialog, null, false);

			final NumberPicker numberPicker = (NumberPicker) dialoglayout
					.findViewById(R.id.number_picker_dialog);
			numberPicker.setMinValue(0);
			numberPicker.setMaxValue(59);

			final NumberPicker customDays = (NumberPicker) dialoglayout
					.findViewById(R.id.days_picker_dialog);
			customDays.setMinValue(0);
			customDays.setMaxValue(values.length - 1);
			customDays.setDisplayedValues(values);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setView(dialoglayout);
			alert = builder.create();
			aq.id(R.id.before_grid_view).itemClicked(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					if (((TextView) previousSelected).getText().toString()
							.equals("15 Mins")) {

						((TextView) previousSelected)
								.setBackgroundResource(R.drawable.round_buttons_white);
						((TextView) previousSelected)
								.setTextColor(getResources().getColor(
										R.color._4d4d4d));
					} else if (previousSelected != null) {
						((TextView) previousSelected)
								.setTextColor(getResources().getColor(
										R.color._4d4d4d));
					}
					if (((TextView) view).getText().toString()
							.equals("15 Mins")) {
						((TextView) view)
								.setBackgroundResource(R.drawable.round_buttons_blue);
					}
					((TextView) view).setTextColor(Color.WHITE);
					view.setSelected(true);
					previousSelected = view;
					if (beforeArray[position].equals("Custom")) {
						alert.show();

					} else {

						if (beforeArray[position] == "On Time") {
							before.setText(beforeArray[position]);
						} else {
							before.setText("Reminde before "
									+ beforeArray[position]);
						}
					
					}
				}

			});
			TextView cancelButton = (TextView) dialoglayout
					.findViewById(R.id.cencel);
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alert.cancel();
				}
			});
			TextView set = (TextView) dialoglayout.findViewById(R.id.set);
			set.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					numberPicker.clearFocus();
					customDays.clearFocus();
					TextView before = (TextView) getActivity().findViewById(
							R.id.before_appoinment);
					before.setText("Reminde before " + numberPicker.getValue()
							+ " " + values[customDays.getValue()]);
					
					numberPicker.getValue();
					alert.dismiss();
				}
			});
		} else {
			set();
			// ***************************location dialog

			AutoCompleteTextView locationTextView2 = (AutoCompleteTextView) AddTask.dialoglayout5
					.findViewById(R.id.adress);
			locationTextView2.setAdapter(new PlacesAutoCompleteAdapter(
					getActivity(),
					android.R.layout.simple_spinner_dropdown_item));
			AlertDialog.Builder builder5 = new AlertDialog.Builder(
					getActivity());

			builder5.setView(AddTask.dialoglayout5);
			location = builder5.create();

			location.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub

					aqd.id(R.id.adress).text("");
					aqd.id(R.id.home).text("");
					aqd.id(R.id.home).getTextView().setFocusable(true);
				}
			});
			aqd = new AQuery(AddTask.dialoglayout5);

			TextView save1 = (TextView) AddTask.dialoglayout5
					.findViewById(R.id.save);
			save1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (!(aqd.id(R.id.adress).getText().toString().equals("") && aqd
							.id(R.id.home).getText().toString().equals(""))) {

						((TextView) viewP).setTextColor(Color
								.parseColor("#000000"));
						aq.id(R.id.location_before_appoin).text(
								aqd.id(R.id.adress).getText());
						((TextView) viewP).setText(aqd.id(R.id.home).getText());

						if (button != null) {
							button.setBackgroundResource(R.drawable.button_shadow2);
							viewP.setBackgroundResource(R.drawable.button_shadow);
							button = viewP;
						} else {

							button = viewP;
							viewP.setBackgroundResource(R.drawable.button_shadow);
						}
						save(viewP.getId(), aqd.id(R.id.home).getText()
								.toString(), aqd.id(R.id.adress).getText()
								.toString());

						aqd.id(R.id.adress).text("");
						aqd.id(R.id.home).text("");
						aqd.id(R.id.save).text("Set");
						location.dismiss();
					}
				}
			});

			TextView cancel1 = (TextView) AddTask.dialoglayout5
					.findViewById(R.id.cancel);
			cancel1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					aqd.id(R.id.add_location_title).text("Set location");
					aqd.id(R.id.save).text("Set");
					location.dismiss();
				}
			});

			aq.id(R.id.pre_defined_1).getTextView()
					.setOnLongClickListener(new LocationEditClickListener());
			aq.id(R.id.pre_defined_2).getTextView()
					.setOnLongClickListener(new LocationEditClickListener());
			aq.id(R.id.pre_defined_3).getTextView()
					.setOnLongClickListener(new LocationEditClickListener());
			aq.id(R.id.pre_defined_4).getTextView()
					.setOnLongClickListener(new LocationEditClickListener());

			temp = aq.id(R.id.pre_defined_1).getView();
			aq.id(R.id.pre_defined_1).clicked(new LocationTagClickListener());
			aq.id(R.id.pre_defined_2).clicked(new LocationTagClickListener());
			aq.id(R.id.pre_defined_3).clicked(new LocationTagClickListener());
			aq.id(R.id.pre_defined_4).clicked(new LocationTagClickListener());

			aq_del.id(R.id.edit_cencel).clicked(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					location_del.dismiss();
				}
			});

			aq_del.id(R.id.edit_del).clicked(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					((TextView) viewl).setText("New");
					((TextView) viewl).setTextColor(R.color.grey);
					((TextView) viewl)
							.setBackgroundResource(R.color.light_grey_color);
					remove(viewl.getId());
					aq.id(R.id.location_before).text("");
					location_del.dismiss();
				}
			});

			aq_edit.id(R.id.add_task_delete).clicked(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					aqd.id(R.id.adress).text("");
					aqd.id(R.id.home).text("");
					location_edit.dismiss();
					location_del.show();
				}
			});

			aq_edit.id(R.id.add_task_edit).clicked(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub=
					aqd.id(R.id.add_location_title).text("Edit");
					aqd.id(R.id.save).text("Save");
					location_edit.dismiss();
					location.show();
				}
			});

			aq.id(R.id.arrive_leave_checkbox_layout).visible();
			AutoCompleteTextView locationTextView = (AutoCompleteTextView) aq
					.id(R.id.location_before_appoin).getView();
			locationTextView.setAdapter(new PlacesAutoCompleteAdapter(
					getActivity(),
					android.R.layout.simple_spinner_dropdown_item));
			((RadioGroup) aq.id(R.id.leave_arrive_radio).getView())
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							if (previousSelectedLocation != null) {
								((RadioButton) previousSelectedLocation)
										.setTextColor(getResources().getColor(
												R.color._4d4d4d));
							}
							((RadioButton) group.findViewById(checkedId))
									.setTextColor(Color.WHITE);
							TextView before = (TextView) getActivity()
									.findViewById(R.id.before_appoinment);
							before.setText("On "
									+ aq.id(checkedId).getText().toString());
							previousSelectedLocation = group
									.findViewById(checkedId);
							
						}
					});
		}
	}

	private class LocationTagClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			load(v.getId());

			viewP = v;

			if (((TextView) v).getText().toString().equals("New")) {
				location.show();
			} else {
				if (button != null) {
					aq.id(R.id.location_before_appoin).text(padress);
					button.setBackgroundResource(R.drawable.button_shadow2);
					v.setBackgroundResource(R.drawable.button_shadow);
					button = v;
				} else {
					aq.id(R.id.location_before_appoin).text(padress);
					button = v;
					v.setBackgroundResource(R.drawable.button_shadow);
				}
			}

		}
	}

	private class LocationEditClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(final View view) {
			// TODO Auto-generated method stu
			if (((TextView) view).getText().toString().equals("New")) {

			} else {
				load(view.getId());
				aqd.id(R.id.adress).text(padress);
				aqd.id(R.id.home).text(((TextView) view).getText().toString());
				aq_del.id(R.id.body).text(
						"Location tag "
								+ ((TextView) view).getText().toString()
								+ " will be deleted");
				aq_edit.id(R.id.add_task_edit_title).text(
						"Location tag:"
								+ ((TextView) view).getText().toString());
				viewl = view;
				location_edit.show();
			}
			return false;
		}

	}

	public void save(long id, String name, String location) {
		// 0 - for private mode
		editor.putString(4 + "key_name" + id, name); // Storing integer
		editor.putString(4 + "key_location" + id, location); // Storing float
		editor.commit();
	}

	public void load(long id) {
		pname = AddTask.pref.getString(4 + "key_name" + id, null); // getting
																	// String
		padress = AddTask.pref.getString(4 + "key_location" + id, null); // getting
																			// String
	}

	public void remove(long id) {
		editor.remove(4 + "key_name" + id); // will delete key name
		editor.remove(4 + "key_location" + id); // will delete key email
		editor.commit();
	}

	public void set() {
		pname = null;
		pname = AddTask.pref.getString(
				4 + "key_name" + aq.id(R.id.pre_defined_1).getView().getId(),
				null);
		if (pname != null) {
			aq.id(R.id.pre_defined_1).text(pname);
			aq.id(R.id.pre_defined_1).getTextView()
					.setTextColor(Color.parseColor("#000000"));
			aq.id(R.id.pre_defined_1).getTextView()
					.setBackgroundResource(R.drawable.button_shadow2);

		}
		pname = null;
		pname = AddTask.pref.getString(
				4 + "key_name" + aq.id(R.id.pre_defined_2).getView().getId(),
				null);
		if (pname != null) {
			aq.id(R.id.pre_defined_2).text(pname);
			aq.id(R.id.pre_defined_2).getTextView()
					.setTextColor(Color.parseColor("#000000"));
			aq.id(R.id.pre_defined_2).getTextView()
					.setBackgroundResource(R.drawable.button_shadow2);

		}
		pname = null;
		pname = AddTask.pref.getString(
				4 + "key_name" + aq.id(R.id.pre_defined_3).getView().getId(),
				null);
		if (pname != null) {
			aq.id(R.id.pre_defined_3).text(pname);
			aq.id(R.id.pre_defined_3).getTextView()
					.setTextColor(Color.parseColor("#000000"));
			aq.id(R.id.pre_defined_3).getTextView()
					.setBackgroundResource(R.drawable.button_shadow2);

		}
		pname = null;
		pname = AddTask.pref.getString(
				4 + "key_name" + aq.id(R.id.pre_defined_4).getView().getId(),
				null);
		if (pname != null) {
			aq.id(R.id.pre_defined_4).text(pname);
			aq.id(R.id.pre_defined_4).getTextView()
					.setTextColor(Color.parseColor("#000000"));
			aq.id(R.id.pre_defined_4).getTextView()
					.setBackgroundResource(R.drawable.button_shadow2);

		}
		pname = null;
	}

}
