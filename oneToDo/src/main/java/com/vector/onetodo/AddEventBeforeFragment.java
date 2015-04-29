package com.vector.onetodo;

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
import com.vector.onetodo.utils.Constants;

import net.simonvt.numberpicker.NumberPicker;

public class AddEventBeforeFragment extends Fragment {

	private int position;

	private AQuery aq, aqd, aq_del, aq_edit;
	private TextView before;
	private Editor editor;
	private String padress = null, pname = null;
	private AlertDialog alert, location, location_del, location_edit;
	private View viewl;

	public static View viewP;

	View button = null;
	private static View previousSelected;
	private static View previousSelectedLocation;
    private boolean isEditMode = false;
    private String selectedItem;

	public static AddEventBeforeFragment newInstance(int position, boolean isEditMode, String selectedItem) {
		AddEventBeforeFragment myFragment = new AddEventBeforeFragment();
		Bundle args = new Bundle();
        args.putInt("position", position);
        args.putBoolean("isEditMode", isEditMode);
        args.putString("selectedItem", selectedItem);
		myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		editor = App.pref.edit();
		before = (TextView) getActivity().findViewById(R.id.before_event);
		position = getArguments().getInt("position", 0);
        isEditMode = getArguments().getBoolean("isEditMode", false);
        selectedItem = getArguments().getString("selectedItem", "");

		View view;
		if (position == 0)
			view = inflater.inflate(R.layout.add_event_before_grid, container,
					false);
		else
			view = inflater.inflate(R.layout.add_event_location, container,
					false);
		aq = new AQuery(getActivity(), view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LayoutInflater inflater5 = getActivity().getLayoutInflater();

		View dialogLayout7 = inflater5.inflate(R.layout.add_task_edit_delete,
				null, false);
		aq_del = new AQuery(dialogLayout7);
		AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
		builder7.setView(dialogLayout7);
		location_del = builder7.create();

		View dialogLayout6 = inflater5.inflate(R.layout.add_task_edit, null,
				false);
		aq_edit = new AQuery(dialogLayout6);
		AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
		builder6.setView(dialogLayout6);
		location_edit = builder6.create();

		if (position == 0) {
			aq.id(R.id.notification_radio_event).getCheckBox()
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (((CheckBox) arg0).isChecked()) {
								aq.id(R.id.notification_radio_event).textColor(

								getResources().getColor(R.color._4d4d4d));
							} else {

								aq.id(R.id.notification_radio_event).textColor(
										Color.parseColor("#bababa"));
							}
						}
					});

			aq.id(R.id.email_radio_event).getCheckBox()
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (((CheckBox) arg0).isChecked()) {
								aq.id(R.id.email_radio_event).textColor(

								getResources().getColor(R.color._4d4d4d));
							} else {

								aq.id(R.id.email_radio_event).textColor(
										Color.parseColor("#bababa"));
							}
						}
					});

			aq.id(R.id.before_grid_view_event).getGridView()
					.setAdapter(
							new ArrayAdapter<String>(getActivity(),
									R.layout.grid_layout_textview,
									Constants.beforeArray) {

								@Override
								public View getView(int position,
										View convertView, ViewGroup parent) {

									TextView textView = (TextView) super
											.getView(position, convertView,
													parent);
                                    if(!isEditMode){
                                        if (textView.getText().toString()
                                                .equals("15 Mins")) {
                                            previousSelected = textView;
                                            textView.setBackgroundResource(R.drawable.round_buttons_blue);
                                            textView.setTextColor(Color.WHITE);
                                        } else
                                            textView.setTextColor(getResources().getColor(R.color._4d4d4d));
                                    }else{
                                        String[] tokenArray =  selectedItem.split(" ");
                                        String comparable = tokenArray[0]+" "+ tokenArray[1];
                                        if(textView.getText().toString()
                                                .equals(comparable)){
                                            previousSelected = textView;
                                            textView.setBackgroundResource(R.drawable.round_buttons_blue);
                                            textView.setTextColor(Color.WHITE);
                                        }
                                    }
									return textView;
								}

							});

			View dialogLayout = getActivity().getLayoutInflater().inflate(
					R.layout.custom_number_picker_dialog, null, false);

			final NumberPicker numberPicker = (NumberPicker) dialogLayout
					.findViewById(R.id.number_picker_dialog);
			numberPicker.setMinValue(0);
			numberPicker.setMaxValue(59);

			final NumberPicker customDays = (NumberPicker) dialogLayout
					.findViewById(R.id.days_picker_dialog);
			customDays.setMinValue(0);
			customDays.setMaxValue(Constants.beforevalues.length - 1);
			customDays.setDisplayedValues(Constants.beforevalues);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setView(dialogLayout);
			alert = builder.create();
			aq.id(R.id.before_grid_view_event).itemClicked(
					new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							if (((TextView) previousSelected).getText()
									.toString().equals("15 Mins")) {

								previousSelected.setBackgroundResource(R.drawable.round_buttons_white);
								((TextView) previousSelected).setTextColor(getResources().getColor(R.color._4d4d4d));
							} else if (previousSelected != null) {
								((TextView) previousSelected).setTextColor(getResources().getColor(R.color._4d4d4d));
							}
							if (((TextView) view).getText().toString()
									.equals("15 Mins")) {
								view.setBackgroundResource(R.drawable.round_buttons_blue);
							}
							((TextView) view).setTextColor(Color.WHITE);
							view.setSelected(true);
							previousSelected = view;
							if (Constants.beforeArray[position]
									.equals("Custom")) {
								alert.show();

							} else {

								if (Constants.beforeArray[position].equals("On Time")) {
									before.setText(Constants.beforeArray[position]);
								} else {
									before.setText(Constants.beforeArray[position]
											+ " Before");
								}
							}

						}

					});
			TextView cancelButton = (TextView) dialogLayout
					.findViewById(R.id.cencel);
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alert.cancel();
				}
			});
			TextView set = (TextView) dialogLayout.findViewById(R.id.set);
			set.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					numberPicker.clearFocus();
					customDays.clearFocus();
					TextView before = (TextView) AddEventFragment.allView
							.findViewById(R.id.before_event);
					before.setVisibility(View.VISIBLE);
					before.setText(numberPicker.getValue() + " "
							+ Constants.beforevalues[customDays.getValue()]
							+ " Before");
					numberPicker.getValue();
					alert.dismiss();
				}
			});

		} else {
			// ***************************location dialog

			set();
			LayoutInflater inflater6 = getActivity().getLayoutInflater();
			View dialoglayout5 = inflater6.inflate(R.layout.add_location_event,
					null, false);
			AutoCompleteTextView locationTextView2 = (AutoCompleteTextView) dialoglayout5
					.findViewById(R.id.adress);
			locationTextView2.setAdapter(new PlacesAutoCompleteAdapter(
					getActivity(),
					android.R.layout.simple_spinner_dropdown_item));
			AlertDialog.Builder builder5 = new AlertDialog.Builder(
					getActivity());

			builder5.setView(dialoglayout5);
			location = builder5.create();

			aqd = new AQuery(dialoglayout5);

			location.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					aqd.id(R.id.adress).text("");
					aqd.id(R.id.home).text("");
					aqd.id(R.id.home).getTextView().setFocusable(true);
				}
			});

			TextView save1 = (TextView) dialoglayout5.findViewById(R.id.save);
			save1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					((TextView) viewP).setTextColor(Color.parseColor("#000000"));
					// ((TextView)
					// viewP).setBackgroundColor(Color.parseColor("#999999"));
					aq.id(R.id.location_before_event).text(
							aqd.id(R.id.adress).getText());
					((TextView) viewP).setText(aqd.id(R.id.home).getText());
					viewP.setBackgroundResource(R.drawable.button_shadow);
					if (button != null) {
						button.setBackgroundResource(R.drawable.button_shadow2);
						viewP.setBackgroundResource(R.drawable.button_shadow);
						button = viewP;
					} else {

						button = viewP;
						viewP.setBackgroundResource(R.drawable.button_shadow);
					}
					save(viewP.getId(), aqd.id(R.id.home).getText().toString(),
							aqd.id(R.id.adress).getText().toString());

					aqd.id(R.id.adress).text("");
					aqd.id(R.id.home).text("");
					aqd.id(R.id.save).text("Set");
					location.dismiss();
				}
			});

			TextView cancel1 = (TextView) dialoglayout5
					.findViewById(R.id.cancel);
			cancel1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					aqd.id(R.id.add_location_title).text("Set location");
					aqd.id(R.id.save).text("Set");
					location.dismiss();
				}
			});

			aq.id(R.id.arrive_leave_checkbox_layout).visible();
			AutoCompleteTextView locationTextView = (AutoCompleteTextView) aq
					.id(R.id.location_before_event).getView();
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
									.findViewById(R.id.before_event);
							before.setVisibility(View.VISIBLE);
							before.setText("On "
									+ aq.id(checkedId).getText().toString());
							previousSelectedLocation = group
									.findViewById(checkedId);
						}
					});
			aq.id(R.id.pre_defined_21).getTextView()
					.setOnLongClickListener(new LocationEditClickListener());
			aq.id(R.id.pre_defined_31).getTextView()
					.setOnLongClickListener(new LocationEditClickListener());
			aq.id(R.id.pre_defined_41).getTextView()
					.setOnLongClickListener(new LocationEditClickListener());

			aq.id(R.id.pre_defined_11).getTextView()
					.setOnLongClickListener(new LocationEditClickListener());

			aq.id(R.id.pre_defined_11).clicked(new LocationTagClickListener());
			aq.id(R.id.pre_defined_21).clicked(new LocationTagClickListener());
			aq.id(R.id.pre_defined_31).clicked(new LocationTagClickListener());
			aq.id(R.id.pre_defined_41).clicked(new LocationTagClickListener());
			aq_del.id(R.id.edit_cencel).clicked(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					location_del.dismiss();
				}
			});

			aq_del.id(R.id.edit_del).clicked(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					((TextView) viewl).setText("New");
					((TextView) viewl).setTextColor(R.color.grey);
					viewl.setBackgroundResource(R.color.light_grey_color);
					remove(viewl.getId());
					aq.id(R.id.location_before_event).text("");
					location_del.dismiss();
				}
			});

			aq_edit.id(R.id.add_task_delete).clicked(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					aqd.id(R.id.adress).text("");
					aqd.id(R.id.home).text("");
					location_edit.dismiss();
					location_del.show();
				}
			});

			aq_edit.id(R.id.add_task_edit).clicked(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					aqd.id(R.id.add_location_title).text("Edit");
					aqd.id(R.id.save).text("Save");
					location_edit.dismiss();
					location.show();
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

					aq.id(R.id.location_before_event).text(padress);
					button.setBackgroundResource(R.drawable.button_shadow2);
					v.setBackgroundResource(R.drawable.button_shadow);
					button = v;
				} else {

					aq.id(R.id.location_before_event).text(padress);
					button = v;
					v.setBackgroundResource(R.drawable.button_shadow);
				}
			}

		}
	}

	private class LocationEditClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(final View view) {
			if (!((TextView) view).getText().toString().equals("New")) {
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

	void save(long id, String name, String location) {
		// 0 - for private mode
		editor.putString(2 + "key_name" + id, name); // Storing integer
		editor.putString(2 + "key_location" + id, location); // Storing float
		editor.commit();
	}

	void load(long id) {
		pname = App.pref.getString(2 + "key_name" + id, null); // getting
																	// String
		padress = App.pref.getString(2 + "key_location" + id, null); // getting
																			// String
	}

	void remove(long id) {
		editor.remove(2 + "key_name" + id); // will delete key name
		editor.remove(2 + "key_location" + id); // will delete key email
		editor.commit();
	}

	void set() {
		pname = null;
		pname = App.pref.getString(
				2 + "key_name" + aq.id(R.id.pre_defined_11).getView().getId(),
				null);
		if (pname != null) {
			aq.id(R.id.pre_defined_11).text(pname);
			aq.id(R.id.pre_defined_11).getTextView()
					.setTextColor(Color.parseColor("#000000"));
			aq.id(R.id.pre_defined_11).getTextView()
					.setBackgroundResource(R.drawable.button_shadow2);

		}
		pname = null;
		pname = App.pref.getString(
				2 + "key_name" + aq.id(R.id.pre_defined_21).getView().getId(),
				null);
		if (pname != null) {
			aq.id(R.id.pre_defined_21).text(pname);
			aq.id(R.id.pre_defined_21).getTextView()
					.setTextColor(Color.parseColor("#000000"));
			aq.id(R.id.pre_defined_21).getTextView()
					.setBackgroundResource(R.drawable.button_shadow2);

		}
		pname = null;
		pname = App.pref.getString(
				2 + "key_name" + aq.id(R.id.pre_defined_31).getView().getId(),
				null);
		if (pname != null) {
			aq.id(R.id.pre_defined_31).text(pname);
			aq.id(R.id.pre_defined_31).getTextView()
					.setTextColor(Color.parseColor("#000000"));
			aq.id(R.id.pre_defined_31).getTextView()
					.setBackgroundResource(R.drawable.button_shadow2);

		}
		pname = null;
		pname = App.pref.getString(
				2 + "key_name" + aq.id(R.id.pre_defined_41).getView().getId(),
				null);
		if (pname != null) {
			aq.id(R.id.pre_defined_41).text(pname);
			aq.id(R.id.pre_defined_41).getTextView()
					.setTextColor(Color.parseColor("#000000"));
			aq.id(R.id.pre_defined_41).getTextView()
					.setBackgroundResource(R.drawable.button_shadow2);

		}
		pname = null;
	}

}
