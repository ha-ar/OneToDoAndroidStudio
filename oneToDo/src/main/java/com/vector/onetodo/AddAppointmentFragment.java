package com.vector.onetodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.location.Geofence;
import com.vector.model.TaskAdded;
import com.vector.onetodo.db.gen.CheckList;
import com.vector.onetodo.db.gen.CheckListDao;
import com.vector.onetodo.db.gen.Label;
import com.vector.onetodo.db.gen.LabelDao;
import com.vector.onetodo.db.gen.Reminder;
import com.vector.onetodo.db.gen.ReminderDao;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.interfaces.onTaskAdded;
import com.vector.onetodo.utils.Constants;
import com.vector.onetodo.utils.ScaleAnimToHide;
import com.vector.onetodo.utils.ScaleAnimToShow;
import com.vector.onetodo.utils.TypeFaces;
import com.vector.onetodo.utils.Utils;

import net.simonvt.datepicker.DatePicker;
import net.simonvt.datepicker.DatePicker.OnDateChangedListener;
import net.simonvt.timepicker.TimePicker;
import net.simonvt.timepicker.TimePicker.OnTimeChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;

public class AddAppointmentFragment extends Fragment implements onTaskAdded {
	private AQuery aq, aqd, aq_del, aq_edit;

	private int Label_postion = -1;
	private ImageView last;
	private String plabel = null;
	private int pposition = -1;
	private int itempos = -1;
	public static  EditText taskTitle;
    private String title= null;
	private Editor editor;
	private View label_view, viewl;

	private int dayPosition;

	static int currentHours, currentMin, currentDayDigit, currentYear,
			currentMonDigit;

	private int[] collapsingViews = { R.id.label_grid_view3,
			R.id.before_grid_view_linear_appoinment,
			R.id.date_time_include_appoinment };

	private String currentDay, currentMon;
	private int[] allViews = { R.id.spinner_label_layout,
			R.id.before_appoinment_lay, R.id.time_date_appoinment,
			R.id.appoinment_title };

	private static HashMap<Integer, Integer> inflatingLayouts = new HashMap<>();

	private final String[] labels_array = new String[] { "Personal", "Home",
			"Work", "New", "New", "New", "New", "New", "New" };

	private AlertDialog add_new_label_alert,location_del,label_edit;

	public static View allView;

	public static Activity act;
    private ToDoDao tododao;
    private CheckListDao checklistdao;
    private LabelDao labeldao;
    private ReminderDao reminderdao;

	private ToDo todo;
	private boolean isEditMode = false;
	private long todoId;
	private String labelColor;
	private Geofences geoFence;
	private AlarmManagerBroadcastReceiver alarm;


    public static AddAppointmentFragment newInstance(int position, boolean isEditMode, long todoId) {
		AddAppointmentFragment myFragment = new AddAppointmentFragment();
		Bundle args = new Bundle();
		args.putInt("position", position);
		args.putLong("todoId", todoId);
		args.putBoolean("isEditMode", isEditMode);
		myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.appoinment_fragment, container,
				false);

		aq = new AQuery(getActivity(), view);
		act = getActivity();
		editor = App.label.edit();
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
		if (toolbar != null)
			((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
		initActionBar();
		db_initialize();
		isEditMode = getArguments().getBoolean("isEditMode");
		todoId = getArguments().getLong("todoId");
		setRetainInstance(true);
		return view;
	}
    private void initActionBar(){
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Appointment");
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.todo_menu_appointment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.action_save_new:
                return true;

            case R.id.action_accept:
                saveAppointment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		editor = App.label.edit();
		dayPosition = getArguments().getInt("dayPosition", 0);
		currentDay = Utils.getCurrentDay(dayPosition, Calendar.SHORT);
		currentYear = Utils.getCurrentYear(dayPosition);
		currentMonDigit = Utils.getCurrentMonthDigit(dayPosition);
		currentDayDigit = Utils.getCurrentDayDigit(dayPosition);
		currentDay = Utils.getCurrentDay(dayPosition, Calendar.SHORT);
		currentMon = Utils.getCurrentMonth(dayPosition, Calendar.SHORT);
		currentHours = Utils.getCurrentHours();
		currentMin = Utils.getCurrentMins();

		allView = getView();

		inflatingLayouts.put(0, R.layout.add_appoinment_title);
		inflatingLayouts.put(1, R.layout.appoinment_date);
		inflatingLayouts.put(2, R.layout.add_appoinment_before);
		inflatingLayouts.put(3, R.layout.add_appoinment_label);
		inflatingLayouts.put(4, R.layout.add_appoinment_notes);

		inflateLayouts();

		main();

		taskTitle = (EditText) aq.id(R.id.appoinment_title).getView();

		taskTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

//				if (taskTitle.getText().length() > 0)
//					AddTask.btn.setAlpha(1);

				aq.id(R.id.completed_appoinment).textColorId(R.color.active);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	void main() {
		LayoutInflater inflater5 = getActivity().getLayoutInflater();

		View dialoglayout6 = inflater5.inflate(R.layout.add_task_edit, null,
				false);
		aq_edit = new AQuery(dialoglayout6);
		AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
		builder6.setView(dialoglayout6);
		label_edit = builder6.create();

		View dialoglayout7 = inflater5.inflate(R.layout.add_task_edit_delete,
				null, false);
		aq_del = new AQuery(dialoglayout7);
		AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
		builder7.setView(dialoglayout7);
		location_del = builder7.create();
		// ****************Title
		aq.id(R.id.appoinment_title)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());
		// *********************End Title

		// ******************date Time Picker

		aq.id(R.id.time_date_appoinment)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());

		// Date picker implementation
		DatePicker dPicker = (DatePicker) aq.id(R.id.date_picker).getView();
		int density = getResources().getDisplayMetrics().densityDpi;
		showRightDateAndTime();
		dPicker.init(currentYear, currentMonDigit, currentDayDigit,
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						dayPosition = 3;
						currentYear = year;
						currentMonDigit = monthOfYear;
						currentDayDigit = dayOfMonth;

						Calendar cal = Calendar.getInstance();
						cal.set(year, monthOfYear, dayOfMonth);
						currentDay = cal.getDisplayName(Calendar.DAY_OF_WEEK,
								Calendar.SHORT, Locale.US);
						currentMon = cal.getDisplayName(Calendar.MONTH,
								Calendar.SHORT, Locale.US);
						showRightDateAndTime();
					}

				});

		// Time picker implementation
		TimePicker tPicker = (TimePicker) aq.id(R.id.time_picker).getView();
		tPicker.setIs24HourView(true);
		tPicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				currentHours = hourOfDay;
				currentMin = minute;
				aq.id(R.id.time_field_appoinment).visible();
				showRightDateAndTime();
			}
		});

		if (density == DisplayMetrics.DENSITY_HIGH) {
			aq.id(R.id.date_picker).margin(-50, -20, -60, -40);
			aq.id(R.id.time_picker).margin(0, -36, -40, -40);
			dPicker.setScaleX(0.7f);
			dPicker.setScaleY(0.7f);
			tPicker.setScaleX(0.7f);
			tPicker.setScaleY(0.7f);
		}

		// ***********************END DATE TIME

		aq.id(R.id.before_appoinment_lay)
				.clicked(new GeneralOnClickListner());
		/**
		 * View pager for before and location
		 * 
		 */
		ViewPager pager = (ViewPager) aq.id(R.id.add_appoinment_before_pager)
				.getView();

		pager.setAdapter(new Appoinmentbeforefragment(getActivity()
				.getSupportFragmentManager()));

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) aq.id(
				R.id.add_appoinment_before_tabs).getView();
		tabs.setDividerColorResource(android.R.color.transparent);
		tabs.setIndicatorColorResource(R.color._4d4d4d);
		tabs.setUnderlineColorResource(android.R.color.transparent);
		tabs.setTextSize(Utils.getPxFromDp(getActivity(), 16));
		tabs.setIndicatorHeight(Utils.getPxFromDp(getActivity(), 1));
		tabs.setTextColorResource(R.color._4d4d4d);
		tabs.setAllCaps(false);
		tabs.setTypeface(
				TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE),
				Typeface.NORMAL);
		tabs.setShouldExpand(true);
		tabs.setViewPager(pager);

		GridView gridView;

		View vie = getActivity().getLayoutInflater().inflate(
				R.layout.add_label, null, false);

		aqd = new AQuery(vie);
		final TextView label_text = (TextView) vie
				.findViewById(R.id.add_label_text);
		gridView = (GridView) vie.findViewById(R.id.add_label_grid);

		gridView.setAdapter(new LabelImageAdapter(getActivity()));

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ImageView img = (ImageView) view;
				if (last != null) {
					// Getting the Country TextView

					last.setImageResource(R.color.transparent);
				}
				last = img;

				img.setImageResource(R.drawable.select_white);

				Label_postion = position;
			}
		});

		AlertDialog.Builder builderLabel = new AlertDialog.Builder(
				getActivity());
		builderLabel.setView(vie);
		add_new_label_alert = builderLabel.create();

		add_new_label_alert.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {

				label_text.setText("");
			}
		});
		TextView saveLabel = (TextView) vie.findViewById(R.id.save);
		saveLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				add_new_label_alert.dismiss();
				if (!(label_text.getText().toString().equals(""))) {
					if (Label_postion != -1) {
						GradientDrawable mDrawable = (GradientDrawable) getResources()
								.getDrawable(R.drawable.label_background);
						if (mDrawable != null) {
							mDrawable.setColor(Color
                                    .parseColor(Constants.label_colors_dialog[Label_postion]));
						}
						Save(label_view.getId() + "" + itempos, label_text
								.getText().toString(), Label_postion);
						Label_postion = -1;
						label_view.setBackground(mDrawable);
						((TextView) label_view).setText(label_text.getText()
								.toString());
						((TextView) label_view).setTextColor(Color.WHITE);

						aq.id(R.id.spinner_labels_appoin).text(
								((TextView) label_view).getText().toString());
						aq.id(R.id.spinner_labels_appoin).getTextView()
								.setBackground(label_view.getBackground());
						aq.id(R.id.spinner_labels_appoin).getTextView()
								.setTextColor(Color.WHITE);
					}
				}
			}
		});

		TextView cancelLabel = (TextView) vie.findViewById(R.id.cancel);
		cancelLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				add_new_label_alert.dismiss();
			}
		});

		// Init labels adapter
		final String[] colors = { "#AC7900", "#4D6600", "#5A0089" };
		aq.id(R.id.label_grid_view)
				.getGridView()
				.setAdapter(
						new ArrayAdapter<String>(getActivity(),
								R.layout.grid_layout_label_text_view,
								labels_array) {

							@Override
							public View getView(int position, View convertView,
									ViewGroup parent) {
								TextView textView = (TextView) super.getView(
										position, convertView, parent);
								Load(textView.getId() + "" + position);

								if (!textView.getText().toString()
										.equals("New")) {
									textView.setTextColor(Color.WHITE);
									GradientDrawable mDrawable = (GradientDrawable) getResources()
											.getDrawable(
													R.drawable.label_background);
									if (mDrawable != null) {
										mDrawable.setColor(Color
                                                .parseColor(colors[position]));
									}
									textView.setBackground(mDrawable);
								}
								if (plabel != null) {
									textView.setTextColor(Color.WHITE);
									textView.setText(plabel);
									GradientDrawable mDrawable = (GradientDrawable) getResources()
											.getDrawable(
													R.drawable.label_background);
									if (mDrawable != null) {
										mDrawable.setColor(Color
                                                .parseColor(Constants.label_colors_dialog[pposition]));
									}
									textView.setBackground(mDrawable);
								}
								return textView;
							}

						});

		aq.id(R.id.label_grid_view).getGridView()
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						itempos = position;
						label_view = view;
						if (!((TextView) view).getText().toString()
								.equalsIgnoreCase("new")) {
							aq.id(R.id.spinner_labels_appoin).text(
									((TextView) view).getText().toString());
							aq.id(R.id.spinner_labels_appoin).getTextView()
									.setBackground(view.getBackground());
							aq.id(R.id.spinner_labels_appoin).getTextView()
									.setTextColor(Color.WHITE);
						} else {
							add_new_label_alert.show();
						}

					}

				});

		aq.id(R.id.label_grid_view).getGridView()
				.setOnItemLongClickListener(new LabelEditClickListener());
		aq_del.id(R.id.edit_cencel).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				location_del.dismiss();
			}
		});

		aq_del.id(R.id.edit_del).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Remove(viewl.getId() + "" + itempos);
				((TextView) viewl).setText("New");
				GradientDrawable mDrawable = (GradientDrawable) getResources()
						.getDrawable(R.drawable.label_simple);
				viewl.setBackground(mDrawable);
				((TextView) viewl).setTextColor(R.color.mountain_mist);

				location_del.dismiss();
			}
		});

		aq_edit.id(R.id.add_task_delete).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				label_edit.dismiss();
				location_del.show();
			}
		});

		aq_edit.id(R.id.add_task_edit).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				aqd.id(R.id.label_title).text("Edit");
				aqd.id(R.id.save).text("Save");
				label_view = viewl;
				label_edit.dismiss();
				add_new_label_alert.show();
			}
		});

		aq.id(R.id.spinner_label_layout).clicked(new GeneralOnClickListner());

		View switchView = aq.id(R.id.add_sub_appoinment).getView();
		toggleCheckList(switchView);

		//to edit a task
		if(isEditMode){
			populateValues();
		}
	
	}

	// ***************Main End**********************

	private void toggleCheckList(View switchView) {
		View newView;
		try {
			ChecklistManager mChecklistManager = ChecklistManager
					.getInstance(getActivity());
			mChecklistManager.setNewEntryHint("Add a subtask");
			mChecklistManager.setMoveCheckedOnBottom(1);
			mChecklistManager.setKeepChecked(true);
			mChecklistManager.setShowChecks(true);
			newView = mChecklistManager.convert(switchView);
			mChecklistManager.replaceViews(switchView, newView);

		} catch (ViewNotSupportedException e) {
			// This exception is fired if the source view class is
			// not supported
			e.printStackTrace();
		}
	}

	public static void inflateLayouts() {
		GridLayout gridLayout = (GridLayout) allView
				.findViewById(R.id.inner_container_appoinment);
		gridLayout.removeAllViews();
		for (int key : inflatingLayouts.keySet()) {
			View child = act.getLayoutInflater().inflate(
					inflatingLayouts.get(key), null);
			GridLayout.LayoutParams param = new GridLayout.LayoutParams();
			param.height = LayoutParams.WRAP_CONTENT;
			param.width = LayoutParams.MATCH_PARENT;
			param.rowSpec = GridLayout.spec(key);
			child.setId(inflatingLayouts.get(key));
			child.setLayoutParams(param);
			gridLayout.addView(child);
		}
	}

	private void hideAll() {
		for (int view : collapsingViews)
			if (aq.id(view).getView() != null
					&& aq.id(view).getView().getVisibility() == View.VISIBLE)
				aq.id(view)
						.getView()
						.startAnimation(
								new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
										200, aq.id(view).getView(), true));
		aq.id(R.id.spinner_label_layout).background(
				R.drawable.input_fields_gray);
		aq.id(R.id.before_appoinment_layout).background(
				R.drawable.input_fields_gray);
		aq.id(R.id.appoinment_time_date).background(
				R.drawable.input_fields_gray);

	}

	private void showCurrentView(View v) {

		hideAll();

		switch (v.getId()) {
		case R.id.time_date_appoinment:
			if (aq.id(R.id.date_time_include_appoinment).getView()
					.getVisibility() == View.GONE) {
				aq.id(R.id.date_time_include_appoinment)
						.getView()
						.startAnimation(
								new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f, 200,
										aq.id(R.id.date_time_include_appoinment)
												.getView(), true));
				aq.id(R.id.appoinment_time_date).background(
						R.drawable.input_fields_blue);
			}

			break;
		case R.id.before_appoinment_lay:

			if (aq.id(R.id.before_grid_view_linear_appoinment).getView()
					.getVisibility() == View.GONE) {
				if (aq.id(R.id.before_appoinment).getText().toString().isEmpty()) {
					aq.id(R.id.before_appoinment).text(
							Constants.beforeArray[1]+" Before").visibility(View.VISIBLE);

				}
				aq.id(R.id.before_grid_view_linear_appoinment)
						.getView()
						.startAnimation(
								new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f, 200,
										aq.id(R.id.before_grid_view_linear_appoinment)
												.getView(), true));
				aq.id(R.id.before_appoinment_layout).background(
						R.drawable.input_fields_blue);

			}
		
			break;
		case R.id.spinner_label_layout:
			if (aq.id(R.id.label_grid_view3).getView().getVisibility() == View.GONE) {
				aq.id(R.id.label_grid_view3)
						.getView()
						.startAnimation(
								new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
										200, aq.id(R.id.label_grid_view3)
												.getView(), true));
				aq.id(R.id.spinner_label_layout).background(
						R.drawable.input_fields_blue);

			}
	
		default:
		}
		}

    private class GeneralOnClickListner implements OnClickListener {

		@Override
		public void onClick(View v) {
			v.setFocusableInTouchMode(true);
			v.requestFocus();
			showCurrentView(v);
			setAllOtherFocusableFalse(v);
			if (v.getId() == R.id.appoinment_title)
				Utils.showKeyboard(getActivity());
			else
				Utils.hidKeyboard(getActivity());
		}

	}

	private void setAllOtherFocusableFalse(View v) {
		for (int id : allViews)
			if (v.getId() != id) {
				try {
					aq.id(id).getView().setFocusableInTouchMode(false);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
	}



	private void showRightDateAndTime() {
		String tempCurrentDayDigit = String.format("%02d", currentDayDigit);
		String tempCurrentHours = String.format("%02d", currentHours);
		String tempCurrentMins = String.format("%02d", currentMin);
		aq.id(R.id.da_appoinment).text("Due");
		if (dayPosition == 0) {
			aq.id(R.id.day_field_appoinment).text("");
			aq.id(R.id.da_appoinment);
			aq.id(R.id.day_field_appoinment).text("Today");
		} else if (dayPosition == 1) {
			aq.id(R.id.day_field_appoinment).text("");
			aq.id(R.id.day_field_appoinment).text("Tomorrow");
		} else {
			aq.id(R.id.day_field_appoinment).text(currentDay);
			aq.id(R.id.month_year_field_appoinment).text(
					tempCurrentDayDigit
							+ Utils.getDayOfMonthSuffix(currentDayDigit) + " "
							+ currentMon);
		}
		aq.id(R.id.time_field_appoinment).text(
				tempCurrentHours + ":" + tempCurrentMins);

	}

	public class Appoinmentbeforefragment extends FragmentStatePagerAdapter {

		public Appoinmentbeforefragment(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 2; // just Add Task & Add Event
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "By time";
			case 1:
				return "At location";
			default:
				return "";// not the case

			}
		}

		@Override
		public Fragment getItem(int position) {
			return AddAppointmentBeforeFragment.newInstance(position);
		}
	}

	private class LabelEditClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
				int position, long arg3) {
			if (((TextView) arg1).getText().toString().equals("New")
					|| position < 3) {

			} else {
				aqd.id(R.id.add_label_text).text(
						((TextView) arg1).getText().toString());
				aq_del.id(R.id.body).text(
						"Label " + ((TextView) arg1).getText().toString()
								+ " will be deleted");
				aq_edit.id(R.id.add_task_edit_title).text(
						"Label: " + ((TextView) arg1).getText().toString());
				viewl = arg1;
				itempos = position;
				label_edit.show();
			}
			return false;
		}
	}

	public class LabelImageAdapter extends BaseAdapter {
		private Context mContext;

		public LabelImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return 10;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(Utils
						.convertDpToPixel(40, mContext), Utils
						.convertDpToPixel(40, mContext)));
			} else {
				imageView = (ImageView) convertView;
			}

			GradientDrawable mDrawable = (GradientDrawable) getResources()
					.getDrawable(R.drawable.label_background_dialog);
			if (mDrawable != null) {
				mDrawable.setColor(Color
                        .parseColor(Constants.label_colors_dialog[position]));
			}
			imageView.setBackground(mDrawable);
			return imageView;
		}

	}

	public void Save(String id, String name, int label_position) {
		// 0 - for private mode
		editor.putString(4 + "key_label" + id, name); // Storing integer
		editor.putInt(4 + "key_color_position" + id, label_position); // Storing
																		// float
		editor.commit();
	}

	public void Load(String id) {
		plabel = null;
		plabel = App.label.getString(4 + "key_label" + id, null); // getting
																		// String
		Log.v("View id= ", id + "| " + plabel + " | " + pposition);

		pposition = App.label.getInt(4 + "key_color_position" + id, 0); // getting
																			// String
	}

	public void Remove(String id) {
		editor.remove(4 + "key_label" + id); // will delete key name
		editor.remove(4 + "key_color_position" + id); // will delete key email
		editor.commit();
	}
    private void saveAppointment() {

        if (!(aq.id(R.id.appoinment_title).getText().toString().equals(""))) {

//            MaxId = App.attach.getInt("4Max", 0);
            String title = aq.id(R.id.appoinment_title).getText().toString();

            boolean is_allday = false;

            String start_date = AddAppointmentFragment.currentYear  + "-"
                    + (AddAppointmentFragment.currentMonDigit + 1) + "-"
                    + AddAppointmentFragment.currentDayDigit+ " "
                    + AddAppointmentFragment.currentHours + ":"
                    + AddAppointmentFragment.currentMin + ":00";
            String end_date = "";

            String location = "";

            String before = aq.id(R.id.before_appoinment).getText().toString();

            boolean is_time = true, is_location = false;
            String r_location = "", location_tag = "";
            if (before.contains("On Arrive") || before.contains("On Leave")) {
                is_time = false;
                is_location = true;
                r_location = aq.id(R.id.location_before_appoin).getText()
                        .toString();

                location_tag = ((TextView) AddAppointmentBeforeFragment.viewP)
                        .getText().toString() + "";
            }

            boolean is_alertEmail = false, is_alertNotification = false;
            if (!(aq.id(R.id.before_appoinment).getText().toString()
                    .equals(""))) {
                is_alertEmail = aq.id(R.id.email_radio_appoin)
                        .getCheckBox().isChecked();
                is_alertNotification = aq
                        .id(R.id.notification_radio_appoin).getCheckBox()
                        .isChecked();
            }
            String repeat = "";

            String repeatdate = "";

            String label_name = aq.id(R.id.spinner_labels_appoin).getText()
                    .toString();

            if(!(aq.id(R.id.add_sub_appoinment).getView() instanceof EditText))
                toggleCheckList(aq.id(R.id.add_sub_appoinment).getView());
            String checklist_data = aq.id(R.id.add_sub_appoinment).getEditText()
                    .getText().toString();

            String notes = aq.id(R.id.notes_appoinment).getText().toString();

            Date startDate, endDate;
            long startDateInMilli = 0, endDateInMilli = 0;
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = sdf1.parse(start_date);
                startDateInMilli = startDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                endDate = sdf1.parse(end_date);
                endDateInMilli = endDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                end_date = null;
            }
            int reminderTime = 0, is_locationtype = 0;
			String locationType = null;
			if (is_time) {
				reminderTime = Utils.getReminderTime(before);
			} else {
				if (before.contains("On Arrive")) {
					is_locationtype = 0;
					locationType = "On Arrive";
				} else if (before.contains("On Leave")) {
					is_locationtype = 1;
					locationType = "On Leave";
				}
			}
			db_initialize();
            todo = new ToDo();
            todo.setUser_id(Constants.user_id);
            todo.setTodo_type_id(4);
            todo.setTitle(title);
            todo.setStart_date(startDateInMilli);
            todo.setEnd_date(endDateInMilli);
            todo.setIs_allday(is_allday);
            todo.setLocation(location);
            todo.setNotes(notes);
            todo.setIs_checked(false);

            Label label = new Label();
            label.setLabel_name(label_name);
            labeldao.insert(label);
            todo.setLabel(label);

            Reminder reminder = new Reminder();
            reminder.setIs_alertEmail(is_alertEmail);
            reminder.setIs_alertNotification(is_alertNotification);
            reminder.setIs_time_location(is_location);
            reminder.setLocation(r_location);
            reminder.setLocation_type(is_locationtype);
            if ((!location_tag.equals("New"))) {
                reminder.setLocation_tag(location_tag);
            }

            reminder.setTime((long) reminderTime);
            reminderdao.insert(reminder);
            todo.setReminder(reminder);


            CheckList checklist = new CheckList();
            checklist.setTitle(checklist_data);
            checklistdao.insert(checklist);
            todo.setCheckList(checklist);


			alarm = new AlarmManagerBroadcastReceiver();
			geoFence = new Geofences(getActivity());
            // ********************* Data add hit Asyntask
			if(isEditMode){
				todo.setId(todoId);
				tododao.update(todo);
			}else {
				AddToServer asyn = new AddToServer(title, 2, start_date, end_date, is_allday, is_location, r_location, location_tag,
						locationType, location, notes, repeatdate, false, 0,
						AddTaskComment.comment, AddTaskComment.commenttime, checklist_data, new ArrayList<String>(), repeat, reminderTime, label_name, "", before, "", AddAppointmentFragment.this);
				asyn.execute();
			}

        }else
            Toast.makeText(getActivity(), "Please enter title",
                    Toast.LENGTH_SHORT).show();

        getActivity().getSupportFragmentManager().popBackStack();
    }
    private void db_initialize() {
        checklistdao = App.daoSession.getCheckListDao();
        labeldao = App.daoSession.getLabelDao();
        tododao = App.daoSession.getToDoDao();
        reminderdao = App.daoSession.getReminderDao();
    }
    @Override
    public void taskAdded() {
		todo.setTodo_server_id(TaskAdded.getInstance().id);
		tododao.insert(todo);

		//have to refresh them all
		TaskListFragment.setAdapter(MainActivity.act, 0, null);
		TaskListFragment.setAdapter(MainActivity.act, 1, null);
		TaskListFragment.setAdapter(MainActivity.act, 2, null);

		App.updateTaskList(getActivity());
		if (!todo.getReminder().getLocation().isEmpty())
			addGeofence(todo);
		setAlarm();
    }

	private void setAlarm(){
		if(todo.getReminder().getTime() != 0){
			alarm.setReminderAlarm(MainActivity.act, todo.getStart_date() - todo.getReminder().getTime(), title, todo.getLocation());
			alarm.SetNormalAlarm(MainActivity.act);
		} else{
			alarm.SetNormalAlarm(MainActivity.act);
		}
	}

	private void addGeofence(ToDo todo){

		LatLong location = App.gpsTracker.getLocationFromAddress(todo.getReminder().getLocation());
		geoFence.addGeofence(location.latitude, location.longitude, 200, getEnterOrExit(todo.getReminder().getLocation_type()), todo.getStart_date(), todo.getId().intValue());
	}

	private int getEnterOrExit(int type){
		if(type == 0)
			return Geofence.GEOFENCE_TRANSITION_ENTER;
		else return Geofence.GEOFENCE_TRANSITION_EXIT;
	}

	private void populateValues() {


		todo = tododao.load(todoId);
		aq.id(R.id.appoinment_title).text(todo.getTitle());
		long reminder = todo.getReminder().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(reminder);
		currentYear = cal.get(Calendar.YEAR);
		currentMonDigit = cal.get(Calendar.MONTH) + 1;
		currentDayDigit = cal.get(Calendar.DAY_OF_MONTH);
		currentHours = cal.get(Calendar.HOUR_OF_DAY);
		currentMin = cal.get(Calendar.MINUTE);
		showRightDateAndTime();
		if(todo.getReminder().getShowable_format() != null){
			aq.id(R.id.before_appoinment).text(todo.getReminder().getShowable_format()).visible();
		}


		if(todo.getLabel().getLabel_color() != null) {
			aq.id(R.id.spinner_labels_appoin).text(todo.getLabel().getLabel_name());
			GradientDrawable mDrawable = (GradientDrawable) getResources()
					.getDrawable(R.drawable.label_background);
			if (mDrawable != null) {
				mDrawable.setColor(Color.parseColor(todo.getLabel().getLabel_color()));
			}
			aq.id(R.id.spinner_labels_appoin).getView().setBackground(mDrawable);
			aq.id(R.id.spinner_labels_appoin).getTextView().setTextColor(Color.WHITE);
		}

		toggleCheckList(aq.id(R.id.add_sub_appoinment).getView());
		aq.id(R.id.add_sub_appoinment).text(todo.getCheckList().getTitle());
		toggleCheckList(aq.id(R.id.add_sub_appoinment).getView());

		aq.id(R.id.notes_appoinment).text(todo.getNotes());
	}
}