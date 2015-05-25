package com.vector.onetodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.ImageOptions;
import com.astuetz.PagerSlidingTabStrip;
import com.devspark.appmsg.AppMsg;
import com.google.android.gms.location.Geofence;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.vector.model.TaskAdded;
import com.vector.model.TaskData;
import com.vector.onetodo.db.gen.CheckList;
import com.vector.onetodo.db.gen.CheckListDao;
import com.vector.onetodo.db.gen.CommentDao;
import com.vector.onetodo.db.gen.Label;
import com.vector.onetodo.db.gen.LabelDao;
import com.vector.onetodo.db.gen.Reminder;
import com.vector.onetodo.db.gen.ReminderDao;
import com.vector.onetodo.db.gen.Repeat;
import com.vector.onetodo.db.gen.RepeatDao;
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

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;

public class AddScheduleFragment extends Fragment implements onTaskAdded {

	public static AQuery aq, aqloc, aq_label, aq_label_edit, aq_label_del,
			aq_menu, aq_attach;
	Uri filename;
	List<NameValuePair> pairs;
	static LinearLayout ll_iner;
	static int FragmentCheck = 0;
	int Tag = 0;
	private PopupWindow popupWindowAttach;
	int Label_postion = -1;
	View label_view;
	GradientDrawable label_color;
	String color,title=null;
	public static EditText taskTitle;
	private Uri imageUri;
	ImageView last;
	String plabel = null;
	int pposition = -1;
	int itempos = -1;
	int MaxId = -1;
	private static final int TAKE_PICTURE = 1;
	private static View previousSelected;
	static LinearLayout lll;
	static int currentHours, currentMin, currentDayDigit, currentYear,
			currentMonDigit, endEventHours, endEventMin, endEventDayDigit, endEventYear, endEventMonDigit;
	private String currentDay, currentMon, endEventDay, endEventMon;
	private int[] collapsingViews = { R.id.sch_time_date_to_include,
			R.id.sch_time_date_from_include, R.id.sch_repeat_grid_layout,
			R.id.sch_label_grid, R.id.before_grid_view_linear_schedule };
	public static String repeatdate = "", setmon1;
	private int[] allViews = { R.id.sch_time_to_layout,
			R.id.sch_time_from_layout, R.id.sch_title_layout,
			R.id.sch_location, R.id.repeat_schedule_lay, R.id.sch_label_layout,
			R.id.before_schedule_lay, R.id.schedule_all_lay };
	public static HashMap<Integer, Integer> inflatingLayouts = new HashMap<Integer, Integer>();
	Editor editor, editorAttach, editorComment;
	AlertDialog date_time_alert, add_new_label_alert, date_time,label_edit,location_del,attach_alert;
	protected static final int RESULT_CODE = 123;
	public static final int RESULT_GALLERY = 0;
	public static final int PICK_CONTACT = 2;
	public static final int RESULT_DROPBOX = 3;
	public static final int RESULT_GOOGLE = 4;
	public static View allView, viewl;
	public static Activity act;
	private ArrayList<String> assignedId = new ArrayList<>();
	private PopupWindow popupWindowSchedule;
	private ToDoDao tododao;
	private CheckListDao checklistdao;
	private LabelDao labeldao;
	private ReminderDao reminderdao;
	private RepeatDao repeatdao;
	private CommentDao commentdao;

	private ToDo todo;
	private boolean isEditMode = false;
	private long todoId;
	private AlarmManagerBroadcastReceiver alarm;
	private Geofences geoFence;


	public static AddScheduleFragment newInstance(int position, boolean isEditMode, long todoId) {
		AddScheduleFragment myFragment = new AddScheduleFragment();
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
		setRetainInstance(true);
		View view = inflater.inflate(R.layout.scheduale_fragment, container,
				false);
		aq = new AQuery(getActivity(), view);
		act = getActivity();
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
		if (toolbar != null)
			((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
		initActionBar();
		db_initialize();
		dragAndDrop();
		isEditMode = getArguments().getBoolean("isEditMode");
		todoId = getArguments().getLong("todoId");
		return view;
	}
	private void initActionBar(){
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Schedule");
		((ActionBarActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
		((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.todo_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				getActivity().getSupportFragmentManager().popBackStack();
				return true;
			case R.id.action_save_new:

				return true;
			case R.id.action_comment:
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_right,
						R.anim.slide_left, R.anim.slide_right, R.anim.slide_left);
				transaction.replace(R.id.content, AddTaskComment.newInstance(false, -1));
				transaction.addToBackStack(null);
				transaction.commit();
				return true;
			case R.id.action_show_hide:
				popupWindowSchedule.showAtLocation(
						aq.id(R.id.content).getView(),
						Gravity.CENTER_HORIZONTAL, 0, 0);
				return true;
			case R.id.action_accept:
				saveSchedule();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		editor = App.label.edit();
		editorAttach = App.attach.edit();
		editorComment = App.comment.edit();
		final int dayPosition = getArguments().getInt("dayPosition", 0);

		currentYear = Utils.getCurrentYear(dayPosition);
		currentMonDigit = Utils.getCurrentMonthDigit(dayPosition);
		currentDayDigit = Utils.getCurrentDayDigit(dayPosition);
		currentDay = Utils.getCurrentDay(dayPosition, Calendar.SHORT);
		currentMon = Utils.getCurrentMonth(dayPosition, Calendar.SHORT);
		currentHours = Utils.getCurrentHours() + 1;
		currentMin = Utils.getCurrentMins();

		endEventYear = Utils.getCurrentYear(dayPosition);
		endEventMonDigit = Utils.getCurrentMonthDigit(dayPosition);
		endEventDayDigit = Utils.getCurrentDayDigit(dayPosition) + 1;
		endEventDay = Utils.getCurrentDay(dayPosition + 1, Calendar.SHORT);
		endEventMon = Utils.getCurrentMonth(dayPosition, Calendar.SHORT);
		endEventHours = Utils.getCurrentHours();
		endEventMin = Utils.getCurrentMins();

		allView = getView();

		inflatingLayouts.put(0, R.layout.add_schedule_title);
		inflatingLayouts.put(1, R.layout.add_schedule);
		inflatingLayouts.put(2, R.layout.add_schedule_details);
		inflatingLayouts.put(3, R.layout.add_schedule_date);
		inflatingLayouts.put(4, R.layout.add_schedule_location);
		inflatingLayouts.put(5, R.layout.add_schedule_before);
		inflatingLayouts.put(6, R.layout.add_schedule_repeat);
		inflatingLayouts.put(7, R.layout.scheduale_label);
		inflatingLayouts.put(8, R.layout.add_schedule_subtask);
		inflatingLayouts.put(9, R.layout.add_schedule_notes);
		inflatingLayouts.put(10, R.layout.add_schedule_attachment);

		inflateLayouts();

		main();
		//to edit a task
		if(isEditMode){
			populateValues();
		}

	}

	public static void updateAssign(HashMap<String, String> names){
		StringBuilder builder = new StringBuilder();
		for(String key : names.keySet()){
			builder.append(names.get(key)).append(", ");
		}
		aq.id(R.id.schedule_assign).text(builder);
	}

	void main() {

		// ****************Title
		aq.id(R.id.sch_title)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());
		taskTitle = (EditText) aq.id(R.id.sch_title).getView();

		taskTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {

//				if (taskTitle.getText().length() > 0)
//					AddTask.btn.setAlpha(1);

				for (String words : Constants.CONTACTS_EVOKING_WORDS) {
					String[] typedWords = s.toString().split(" ");

					try {
						String name = typedWords[typedWords.length - 1];

					} catch (ArrayIndexOutOfBoundsException aiobe) {

					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		aq.id(R.id.schedule_assign).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in1,
						R.anim.slide_out1);
				transaction.replace(R.id.content, AddTaskAssign.newInstance(1));
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		aq.id(R.id.sch_time_to_layout)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());

		aq.id(R.id.sch_time_from_layout)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());

		ToggleButton toggle = (ToggleButton) getActivity().findViewById(
				R.id.switch_sch);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				if (isChecked) {
					aq.id(R.id.sch_time_from).getTextView()
							.setVisibility(View.GONE);
					aq.id(R.id.sch_time_to).getTextView()
							.setVisibility(View.GONE);

					aq.id(R.id.time_picker).getView().setVisibility(View.GONE);
					aq.id(R.id.time_picker_event_end).getView()
							.setVisibility(View.GONE);
				} else {

					aq.id(R.id.time_picker).getView()
							.setVisibility(View.VISIBLE);
					aq.id(R.id.time_picker_event_end).getView()
							.setVisibility(View.VISIBLE);
					aq.id(R.id.sch_allday_img).background(R.drawable.allday);
					aq.id(R.id.sch_time_from).getTextView()
							.setVisibility(View.VISIBLE);

					aq.id(R.id.sch_time_to).getTextView()
							.setVisibility(View.VISIBLE);
				}
			}
		});


		// Date picker implementation
		final DatePicker dPicker = (DatePicker) aq.id(R.id.date_picker)
				.getView();
		int density = getResources().getDisplayMetrics().densityDpi;
		showRightDateAndTime();
		dPicker.setMinDate(System.currentTimeMillis() - 1000);
		dPicker.init(currentYear, currentMonDigit, currentDayDigit,
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
											  int monthOfYear, int dayOfMonth) {
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
				showRightDateAndTime();
			}
		});

		// Date picker implementation
		DatePicker dPickerEvent = (DatePicker) aq
				.id(R.id.date_picker_event_end).getView();
		showRightDateAndTime();
		dPickerEvent.setMinDate(System.currentTimeMillis() - 1000);
		dPickerEvent.init(endEventYear, endEventMonDigit, endEventDayDigit,
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
											  int monthOfYear, int dayOfMonth) {
						endEventYear = year;
						endEventMonDigit = monthOfYear;
						endEventDayDigit = dayOfMonth;

						Calendar cal = Calendar.getInstance();
						cal.set(year, monthOfYear, dayOfMonth);
						endEventDay = cal.getDisplayName(Calendar.DAY_OF_WEEK,
								Calendar.SHORT, Locale.US);
						endEventMon = cal.getDisplayName(Calendar.MONTH,
								Calendar.SHORT, Locale.US);
						showRightDateAndTime();

					}

				});

		// Time picker implementation
		TimePicker tPickerEvent = (TimePicker) aq
				.id(R.id.time_picker_event_end).getView();
		tPickerEvent.setIs24HourView(true);
		tPickerEvent.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				endEventHours = hourOfDay;
				endEventMin = minute;
				showRightDateAndTime();
			}
		});

		if (density == DisplayMetrics.DENSITY_HIGH) {

			aq.id(R.id.date_picker).margin(-50, -20, -60, -40);
			aq.id(R.id.time_picker).margin(0, -36, -40, -40);
			aq.id(R.id.date_picker_event_end).margin(-50, -20, -60, -40);
			aq.id(R.id.time_picker_event_end).margin(0, -36, -40, -40);
			dPicker.setScaleX(0.7f);
			dPicker.setScaleY(0.7f);
			tPicker.setScaleX(0.7f);
			tPicker.setScaleY(0.7f);

			dPickerEvent.setScaleX(0.7f);
			dPickerEvent.setScaleY(0.7f);
			tPickerEvent.setScaleX(0.7f);
			tPickerEvent.setScaleY(0.7f);
		}

		// ***************** Location

		aq.id(R.id.sch_location)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());
		AutoCompleteTextView locationTextView = (AutoCompleteTextView) aq.id(R.id.sch_location)
				.getView();
		PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(
				getActivity(), android.R.layout.simple_spinner_dropdown_item);
		locationTextView.setAdapter(adapter);
		locationTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


		// *******************Repeat

		aq.id(R.id.repeat_schedule_lay).clicked(new GeneralOnClickListner());

		aq.id(R.id.sch_repeat_grid)
				.getGridView()
				.setAdapter(
						new ArrayAdapter<String>(getActivity(),
								R.layout.grid_layout_textview, Constants.repeatArray) {

							@Override
							public View getView(int position, View convertView,
												ViewGroup parent) {

								TextView textView = (TextView) super.getView(
										position, convertView, parent);
								if(!isEditMode){
									if (position == 2) {
										previousSelected = textView;
										textView.setBackgroundResource(R.drawable.round_buttons_blue);
										textView.setTextColor(Color.WHITE);
									}
									else
										textView.setTextColor(getResources().getColor(R.color._4d4d4d));
								}else{
									int selectedPosition = 0;
									for(int i = 0; i < Constants.repeatArray.length; i++){
										if(Constants.repeatArray[i].equalsIgnoreCase(todo.getRepeat().getShowable_format())){
											selectedPosition = i;
											break;
										}
									}
									if (position == selectedPosition) {
										previousSelected = textView;
										textView.setBackgroundResource(R.drawable.round_buttons_blue);
										textView.setTextColor(Color.WHITE);
									}
								}
								return textView;
							}

						});

		aq.id(R.id.sch_repeat_grid).itemClicked(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				if (((TextView) previousSelected).getText().toString()
						.equals("Weekly")) {

					previousSelected
							.setBackgroundResource(R.drawable.round_buttons_white);
					((TextView) previousSelected).setTextColor(getResources()
							.getColor(R.color._4d4d4d));
				} else if (previousSelected != null) {
					((TextView) previousSelected).setTextColor(getResources()
							.getColor(R.color._4d4d4d));
				}
				if (((TextView) view).getText().toString().equals("Weekly")) {
					view.setBackgroundResource(R.drawable.round_buttons_blue);
				}

				if (((TextView) view).getText().toString().equals("Never")) {
					aq.id(R.id.sch_forever_radio).checked(true);
					aq.id(R.id.sch_time_radio).textColor(
							Color.parseColor("#bababa"));
					aq.id(R.id.sch_forever_radio).textColor(
							getResources().getColor(R.color._4d4d4d));
				}
				((TextView) view).setTextColor(Color.WHITE);
				view.setSelected(true);
				if (Constants.repeatArray[position].equals( "Never")) {
					aq.id(R.id.sch_repeat_txt).text(Constants.repeatArray[position]);
				} else {
					aq.id(R.id.sch_repeat_txt).text(Constants.repeatArray[position]);
				}
				previousSelected = view;

			}

		});
		LayoutInflater inflater4 = getActivity().getLayoutInflater();
		View dateTimePickerDialog = inflater4.inflate(
				R.layout.date_time_layout_dialog, null, false);
		AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity());
		builder4.setView(dateTimePickerDialog);
		date_time_alert = builder4.create();


		// Date picker implementation for forever dialogdate_picker
		final DatePicker dialogDatePicker = (DatePicker) dateTimePickerDialog
				.findViewById(R.id.date_picker_dialog);
		showRightDateAndTimeForDialog();
		dPicker.setMinDate(System.currentTimeMillis() - 1000);
		dialogDatePicker.init(currentYear, currentMonDigit, currentDayDigit,
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
											  int monthOfYear, int dayOfMonth) {
						currentYear = year;
						currentMonDigit = monthOfYear;
						currentDayDigit = dayOfMonth;

						Calendar cal = Calendar.getInstance();
						cal.set(year, monthOfYear, dayOfMonth);
						currentDay = cal.getDisplayName(Calendar.DAY_OF_WEEK,
								Calendar.SHORT, Locale.US);
						currentMon = cal.getDisplayName(Calendar.MONTH,
								Calendar.SHORT, Locale.US);
						showRightDateAndTimeForDialog();
					}

				});


		aq.id(R.id.sch_time_radio).textColor(Color.parseColor("#bababa"));

		final TextView set = (TextView) dateTimePickerDialog
				.findViewById(R.id.set);
		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDatePicker.clearFocus();
				set.requestFocus();
				date_time_alert.dismiss();

				aq.id(R.id.sch_repeat_txt).text(((TextView) previousSelected)
						.getText().toString() + " until " + setmon1);

				RadioButton rb = (RadioButton) aq
						.id(R.id.sch_time_radio).getView();

				rb.setText(setmon1);
			}
		});
		TextView cancel = (TextView) dateTimePickerDialog
				.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				date_time_alert.cancel();
			}
		});

		aq.id(R.id.sch_forever_radio).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (aq.id(R.id.sch_repeat_txt).getText().toString() == "Never") {
				} else {
					aq.id(R.id.sch_repeat_txt).text(
							((TextView) previousSelected).getText().toString());
				}

				aq.id(R.id.sch_time_radio).textColor(
						Color.parseColor("#bababa"));
				aq.id(R.id.sch_forever_radio).textColor(
						getResources().getColor(R.color._4d4d4d));
			}
		});

		aq.id(R.id.sch_time_radio).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (aq.id(R.id.sch_repeat_txt).getText().toString()
						.equals("Never")) {
					Toast.makeText(getActivity(), "Please Select ...",
							Toast.LENGTH_SHORT).show();
					aq.id(R.id.sch_time_radio).checked(false);
					aq.id(R.id.sch_forever_radio).checked(true);
					aq.id(R.id.sch_time_radio).textColor(
							Color.parseColor("#bababa"));
					aq.id(R.id.sch_forever_radio).textColor(
							getResources().getColor(R.color._4d4d4d));
				} else {

					aq.id(R.id.sch_time_radio).textColor(
							getResources().getColor(R.color._4d4d4d));
					aq.id(R.id.sch_forever_radio).textColor(

							getResources().getColor(R.color._4d4d4d));
					date_time_alert.show();
				}
			}
		});


		// ******************* label dialog
		LayoutInflater inflater5 = getActivity().getLayoutInflater();

		View dialoglayout6 = inflater5.inflate(R.layout.add_task_edit, null,
				false);
		aq_label_edit = new AQuery(dialoglayout6);
		AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
		builder6.setView(dialoglayout6);
		label_edit = builder6.create();

		View dialoglayout7 = inflater5.inflate(R.layout.add_task_edit_delete,
				null, false);
		aq_label_del = new AQuery(dialoglayout7);
		AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
		builder7.setView(dialoglayout7);
		location_del = builder7.create();
		aq.id(R.id.sch_label_layout).clicked(new GeneralOnClickListner());

		GridView gridView;

		View vie = getActivity().getLayoutInflater().inflate(
				R.layout.add_label, null, false);

		aq_label = new AQuery(vie);
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

						aq.id(R.id.sch_label_txt).text(
								((TextView) label_view).getText().toString());
						aq.id(R.id.sch_label_txt).getTextView()
								.setBackground(label_view.getBackground());
						aq.id(R.id.sch_label_txt).getTextView()
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
		aq.id(R.id.sch_label_grid)
				.getGridView()
				.setAdapter(
						new ArrayAdapter<String>(getActivity(),
								R.layout.grid_layout_label_text_view,
								Constants.labels_array_event) {

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
                                                .parseColor(Constants.label_colors[position]));
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

		aq.id(R.id.sch_label_grid).getGridView()
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						itempos = position;
						label_view = view;
						if (!((TextView) view).getText().toString()
								.equalsIgnoreCase("new")) {
							aq.id(R.id.sch_label_txt).text(
									((TextView) view).getText().toString());
							aq.id(R.id.sch_label_txt).getTextView()
									.setBackground(view.getBackground());
							aq.id(R.id.sch_label_txt).getTextView()
									.setTextColor(Color.WHITE);

						} else {
							add_new_label_alert.show();
						}

					}

				});

		aq.id(R.id.sch_label_grid).getGridView()
				.setOnItemLongClickListener(new LabelEditClickListener());
		aq_label_del.id(R.id.edit_cencel).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				location_del.dismiss();
			}
		});

		aq_label_del.id(R.id.edit_del).clicked(new OnClickListener() {

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

		aq_label_edit.id(R.id.add_task_delete).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				label_edit.dismiss();
				location_del.show();
			}
		});

		aq_label_edit.id(R.id.add_task_edit).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				aq_label.id(R.id.label_title).text("Edit");
				aq_label.id(R.id.save).text("Save");
				label_view = viewl;
				label_edit.dismiss();
				add_new_label_alert.show();
			}
		});

		// ******************** END Label

		aq.id(R.id.before_schedule_lay).clicked(new GeneralOnClickListner());

		/**
		 * View pager for before and location
		 *
		 */
		ViewPager pager = (ViewPager) aq
				.id(R.id.add_task_before_pager_schedule).getView();

		pager.setAdapter(new AddTaskBeforePagerFragment(getActivity()
				.getSupportFragmentManager()));
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) aq.id(
				R.id.add_task_before_tabs_schedule).getView();
		tabs.setDividerColorResource(android.R.color.transparent);
		tabs.setIndicatorColorResource(R.color._4d4d4d);
		tabs.setUnderlineColorResource(android.R.color.transparent);
		tabs.setTextSize(Utils.getPxFromDp(getActivity(), 16));
		tabs.setIndicatorHeight(Utils.getPxFromDp(getActivity(), 1));
		tabs.setAllCaps(false);
		tabs.setTypeface(
				TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE),
				Typeface.NORMAL);
		tabs.setShouldExpand(true);
		tabs.setViewPager(pager);

		// ************************** Attachment

		final View view12 = getActivity().getLayoutInflater().inflate(
				R.layout.landing_menu, null, false);
		aq_menu = new AQuery(getActivity(), view12);
		popupWindowAttach = new PopupWindow(view12, Utils.getDpValue(200,
				getActivity()), WindowManager.LayoutParams.WRAP_CONTENT, true);

		popupWindowAttach.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.dialog_holo_light_frame));
		popupWindowAttach.setOutsideTouchable(true);
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View attachment = inflater
				.inflate(R.layout.add_attachment, null, false);
		aq_attach = new AQuery(attachment);

		// Gallery and Camera intent
		aq_attach
				.id(R.id.gallery1)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new OnClickListener() {

					@Override
					public void onClick(View v) {
						attach_alert.dismiss();
						Intent galleryIntent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(galleryIntent, RESULT_GALLERY);
					}
				});
		aq_attach
				.id(R.id.camera1)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new OnClickListener() {

					@Override
					public void onClick(View v) {
						attach_alert.dismiss();
						Intent intent = new Intent(
								"android.media.action.IMAGE_CAPTURE");

						String path = Environment.getExternalStorageDirectory()
								.toString();
						File makeDirectory = new File(path + File.separator
								+ "OneTodo");
						makeDirectory.mkdir();
						File photo = new File(Environment
								.getExternalStorageDirectory()
								+ File.separator
								+ "OneToDo" + File.separator, "OneToDo_"
								+ System.currentTimeMillis() + ".png");
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(photo));
						imageUri = Uri.fromFile(photo);
						startActivityForResult(intent, TAKE_PICTURE);
					}
				});
		aq_attach
				.id(R.id.add_attachment_dropbox)
				.typeface(TypeFaces
						.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new OnClickListener() {
					@Override
					public void onClick(View v) {
						attach_alert.dismiss();
						Intent dropboxIntent = new Intent(Intent.ACTION_GET_CONTENT);
						dropboxIntent.setType("*/*");
						startActivityForResult(Intent
								.createChooser(dropboxIntent.setPackage("com.dropbox.android"), "DropBox") , RESULT_DROPBOX);
					}
				});
		aq_attach
				.id(R.id.add_attachment_google)
				.typeface(TypeFaces
						.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new OnClickListener() {
					@Override
					public void onClick(View v) {
						attach_alert.dismiss();
						Intent googleIntent = new Intent(Intent.ACTION_GET_CONTENT);
						googleIntent.setType("*/*");
						startActivityForResult(Intent
								.createChooser(googleIntent
										.setPackage("com.google.android.apps.docs"), "Google Drive") , RESULT_GOOGLE);
					}
				});
		AlertDialog.Builder attach_builder = new AlertDialog.Builder(
				getActivity());
		attach_builder.setView(attachment);
		attach_alert = attach_builder.create();

		aq.id(R.id.event_attachment).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				attach_alert.show();
			}
		});




		View switchView = aq.id(R.id.add_sub_sch).getView();
		toggleCheckList(switchView);

	}

	// private ChecklistManager mChecklistManager;

	private void toggleCheckList(View switchView) {
		View newView;
		try {
			ChecklistManager mChecklistManager = ChecklistManager
					.getInstance(getActivity());
			mChecklistManager.setNewEntryHint("Add a subtask...");
			mChecklistManager.setMoveCheckedOnBottom(1);
			mChecklistManager.setKeepChecked(true);
			mChecklistManager.setShowChecks(true);
			newView = mChecklistManager.convert(switchView);
			mChecklistManager.replaceViews(switchView, newView);

		} catch (ViewNotSupportedException e) {
			e.printStackTrace();
		}
	}


	public static void inflateLayouts() {
		GridLayout gridLayout = (GridLayout) allView
				.findViewById(R.id.inner_container_scheduale);
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
			if(!App.prefs.getScheduleLayout(Constants.addTaskLayouts[key]))
				child.setVisibility(View.GONE);
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

		aq.id(R.id.schedule_time_to_layout).background(
				R.drawable.input_fields_gray);
		aq.id(R.id.schedule_time_from_layout).background(
				R.drawable.input_fields_gray);
		aq.id(R.id.sch_repeat).background(R.drawable.input_fields_gray);
		aq.id(R.id.before_schedule_layout).background(
				R.drawable.input_fields_gray);
		aq.id(R.id.sch_label_layout).background(R.drawable.input_fields_gray);
	}

	private void showCurrentView(View v) {

		hideAll();

		switch (v.getId()) {

			case R.id.sch_time_to_layout:
				if (aq.id(R.id.sch_time_date_to_include).getView().getVisibility() == View.GONE) {
					aq.id(R.id.sch_time_date_to_include)
							.getView()
							.startAnimation(
									new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
											200, aq.id(
											R.id.sch_time_date_to_include)
											.getView(), true));

					aq.id(R.id.schedule_time_to_layout).background(
							R.drawable.input_fields_blue);

				}
				break;
			case R.id.sch_time_from_layout:
				if (aq.id(R.id.sch_time_date_from_include).getView()
						.getVisibility() == View.GONE) {
					aq.id(R.id.sch_time_date_from_include)
							.getView()
							.startAnimation(
									new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
											200,
											aq.id(R.id.sch_time_date_from_include)
													.getView(), true));

					aq.id(R.id.schedule_time_from_layout).background(
							R.drawable.input_fields_blue);

				}
				break;

			case R.id.repeat_schedule_lay:
				if (aq.id(R.id.sch_repeat_grid_layout).getView().getVisibility() == View.GONE) {
					if (aq.id(R.id.sch_repeat_txt).getText().toString().isEmpty()) {
						aq.id(R.id.sch_repeat_txt).text(Constants.repeatArray[2])
								.visibility(View.VISIBLE);
					}
					aq.id(R.id.sch_repeat_grid_layout).getView()
							.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f, 200, aq.id(R.id.sch_repeat_grid_layout).getView(), true));
					aq.id(R.id.sch_repeat).background(R.drawable.input_fields_blue);

				}
				break;
			case R.id.before_schedule_lay:
				if (aq.id(R.id.before_grid_view_linear_schedule).getView()
						.getVisibility() == View.GONE) {
					if (aq.id(R.id.before_schedule).getText().toString().isEmpty()) {
						aq.id(R.id.before_schedule)
								.text(Constants.beforeArray[1]
										+ " Before").visibility(View.VISIBLE);
					}
					aq.id(R.id.before_grid_view_linear_schedule)
							.getView()
							.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f, 200, aq.id(R.id.before_grid_view_linear_schedule).getView(), true));
					aq.id(R.id.before_schedule_layout).background(
							R.drawable.input_fields_blue);

				}
				break;
			case R.id.sch_label_layout:
				if (aq.id(R.id.sch_label_grid).getView().getVisibility() == View.GONE) {
					aq.id(R.id.sch_label_grid)
							.getView()
							.startAnimation(
									new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
											200, aq.id(R.id.sch_label_grid)
											.getView(), true));

					aq.id(R.id.sch_label_layout).background(
							R.drawable.input_fields_blue);
				}
			default:
				break;
		}

	}

	private class GeneralOnClickListner implements OnClickListener {

		@Override
		public void onClick(View v) {
			v.setFocusableInTouchMode(true);
			v.requestFocus();
			showCurrentView(v);
			setAllOtherFocusableFalse(v);
			if (v.getId() == R.id.sch_location
					|| v.getId() == R.id.repeat_schedule_lay
					|| v.getId() == R.id.before_schedule_lay
					|| v.getId() == R.id.sch_label_layout)
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

	public class AddTaskBeforePagerFragment extends FragmentStatePagerAdapter {

		public AddTaskBeforePagerFragment(FragmentManager fm) {
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
					return "Time";
				case 1:
					return "Location";
				default:
					return "";// not the case

			}
		}

		@Override
		public Fragment getItem(int position) {
			return AddScheduleBeforeFragment.newInstance(position, isEditMode, todo != null ? todo.getReminder().getShowable_format() : "");
		}
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case TAKE_PICTURE:
				if (resultCode == Activity.RESULT_OK)
					showImageURI(imageUri);
			case RESULT_GALLERY:
				if (data != null) {
					showImageURI(data.getData());
				}
				break;
			case RESULT_DROPBOX:
				if(data != null){
					showImageURI(data.getData());
				}
				break;
			case RESULT_GOOGLE:
				if(data != null){
					showImageURI(data.getData());
				}
				break;
			case PICK_CONTACT:
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();
					Cursor c = getActivity().getContentResolver().query(
							contactData, null, null, null, null);
					if (c.moveToFirst()) {
						String name = c
								.getString(c
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						AppMsg.makeText(getActivity(), name + " is selected",
								AppMsg.STYLE_CONFIRM).show();
					}
				}
				break;
			default:
				break;
		}
	}

	private void showImageURI(Uri selectedImage) {

		getActivity().getContentResolver().notifyChange(selectedImage, null);
		ContentResolver cr = getActivity().getContentResolver();
		Bitmap bitmap;
		if (FragmentCheck == 0) {
			try {
				bitmap = Utils.getBitmap(selectedImage, getActivity(), cr);
				final LinearLayout item = (LinearLayout) aq
						.id(R.id.added_image_outer_event).visible().getView();

				final View child = getActivity().getLayoutInflater().inflate(
						R.layout.image_added_layout, null);

				ImageView image = (ImageView) child
						.findViewById(R.id.image_added);
				ImageView imageMenu = (ImageView) child
						.findViewById(R.id.image_menu);
				Tag = Tag + 1;
				imageMenu.setTag(Tag);
				child.setId(Tag);

				imageMenu.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Toast.makeText(getActivity(),
								arg0.getId() + "     " + arg0.getTag(),
								Toast.LENGTH_LONG).show();
						ll_iner = (LinearLayout) item.findViewById(Integer
								.parseInt(arg0.getTag().toString()));

						if (popupWindowAttach.isShowing()) {
							popupWindowAttach.dismiss();

						} else {
							popupWindowAttach.showAsDropDown(arg0, 5, 0);
						}
					}
				});

				aq_menu.id(R.id.menu_item1).text("Save file")
						.clicked(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								popupWindowAttach.dismiss();
							}
						});
				aq_menu.id(R.id.menu_item2).text("Delete")
						.clicked(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (ll_iner != null)
									item.removeView(ll_iner);
								popupWindowAttach.dismiss();
							}
						});

				aq.id(image).image(Utils.getRoundedCornerBitmap(bitmap, 7));
				TextView text = (TextView) child
						.findViewById(R.id.image_added_text);
				TextView by = (TextView) child
						.findViewById(R.id.image_added_by);
				TextView size = (TextView) child
						.findViewById(R.id.image_added_size);

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
				by.setText("By " + App.prefs.getUserName()+" on " + sdf.format(cal.getTime()));
				text.setText(Utils.getImageName(getActivity(), selectedImage));
				Bitmap bm = Utils.getBitmap(getActivity(), selectedImage);
				byte[] bitmapArray = Utils.getImageByteArray(bm);
				uploadAttachments(aq, bitmapArray);
				size.setText("(" + (bm.getByteCount()) / 1024 + " KB)");
				item.addView(child);

			} catch (Exception e) {
				Toast.makeText(getActivity(), "Failed to load",
						Toast.LENGTH_SHORT).show();
				Log.e("Camera", e.toString());
			}
		}
	}
	private void uploadAttachments(AQuery aq, byte[] byteArray) {

		HttpEntity entity = null;
		String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("image", encoded));

		try {
			entity = new UrlEncodedFormEntity(pairs, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Map<String, HttpEntity> param = new HashMap<>();
		param.put(AQuery.POST_ENTITY, entity);

		aq.ajax("http://api.heuristix.net/one_todo/v1/upload.php", param,
				JSONObject.class, new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject json,
										 AjaxStatus status) {
						String path = null;
						try {
							Log.e("attachment", json.toString());
							JSONObject obj1 = new JSONObject(json.toString());
							path = obj1.getString("path");
							Loadattachmax();
							if (MaxId == 0) {
								MaxId = 1;
							} else {
								MaxId = MaxId + 1;
							}
							Saveattach(MaxId, path, "type");
						} catch (Exception e) {

						}

					}
				});
	}

	private void showRightDateAndTime() {

		String tempCurrentDayDigit = String.format("%02d", endEventDayDigit);
		String tempCurrentHours = String.format("%02d", endEventHours);
		String tempCurrentMins = String.format("%02d", endEventMin);
		aq.id(R.id.sch_time_to_day).text(endEventDay);
		aq.id(R.id.sch_time_to_day_month).text(
				tempCurrentDayDigit
						+ Utils.getDayOfMonthSuffix(endEventDayDigit));
		aq.id(R.id.sch_time_to_month).text(endEventMon);
		aq.id(R.id.sch_time_to).text(
				tempCurrentHours + " : " + tempCurrentMins);

		String tempEndDayDigit = String.format("%02d", currentDayDigit);
		String tempEndHours = String.format("%02d", currentHours);
		String tempEndMins = String.format("%02d", currentMin);
		aq.id(R.id.sch_time_from_day).text(currentDay);
		aq.id(R.id.sch_time_from_day_month).text(
				tempEndDayDigit
						+ Utils.getDayOfMonthSuffix(currentDayDigit));
		aq.id(R.id.sch_time_from_month).text(currentMon);
		aq.id(R.id.sch_time_from).text(
				tempEndHours + " : " + tempEndMins);
	}

	private void showRightDateAndTimeForDialog() {

		String fff = String.valueOf(currentDayDigit).replace("th", "");
		setmon1 = fff + " " + currentMon + " " + currentYear;
		repeatdate = currentYear + "-" + (currentMonDigit + 1) + "-"
				+ currentDayDigit + " 00:00:00";
	}

	public void Save(String id, String name, int label_position) {
		// 0 - for private mode
		editor.putString(3 + "key_label" + id, name); // Storing integer
		editor.putInt(3 + "key_color_position" + id, label_position); // Storing
		// float
		editor.commit();
	}

	public void Load(String id) {
		plabel = null;
		plabel = App.label.getString(3 + "key_label" + id, null); // getting
		// String
		Log.v("View id= ", id + "| " + plabel + " | " + pposition);

		pposition = App.label.getInt(3 + "key_color_position" + id, 0); // getting
		// String
	}

	public void Remove(String id) {
		editor.remove(3 + "key_label" + id); // will delete key name
		editor.remove(3 + "key_color_position" + id); // will delete key email
		editor.commit();
	}
	private class LabelEditClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
									   int position, long arg3) {
			if (!(((TextView) arg1).getText().toString().equals("New")) || (position < 3)) {

				aq_label.id(R.id.add_label_text).text(
						((TextView) arg1).getText().toString());
				aq_label_del.id(R.id.body).text(
						"Label " + ((TextView) arg1).getText().toString()
								+ " will be deleted");
				aq_label_edit.id(R.id.add_task_edit_title).text(
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
				mDrawable.setColor(Color.parseColor(Constants.label_colors_dialog[position]));
			}

			imageView.setBackground(mDrawable);

			return imageView;
		}

	}

	public void Saveattach(int id, String path, String type) {
		// 0 - for private mode
		editorAttach.putInt("3Max", id);
		editorAttach.putString(3 + "path" + id, path);
		editorAttach.putString(3 + "type" + id, type); // Storing float
		editorAttach.commit();
	}

	public void Loadattachmax() {
		MaxId = App.attach.getInt("3Max", 0);
	}

	public void Loadattach(int id) {
		App.attach.getString(3 + "path" + id, null);
		App.attach.getString(3 + "type" + id, null); // getting String
	}

	public void Removeattach(int id) {
		editorAttach.remove(3 + "path" + id); // will delete key name
		editorAttach.remove(3 + "type" + id); // will delete key email
		editorAttach.commit();
	}

	private void dragAndDrop(){
		final LayoutInflater inflater3 = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View viewS = inflater3.inflate(R.layout.popup_menu_schedule,
				null, false);

		popupWindowSchedule = new PopupWindow(viewS, Utils.getDpValue(270, getActivity()),
				WindowManager.LayoutParams.WRAP_CONTENT, true);

		TextView cancel_schedule = (TextView) viewS
				.findViewById(R.id.cancel_event);
		TextView ok_schedule = (TextView) viewS.findViewById(R.id.ok_event);
		ArrayList<String> arrayListSchedule = new ArrayList<>(
				Arrays.asList(Constants.layoutsName));

		DragSortListView listViewSchedule = (DragSortListView) viewS
				.findViewById(R.id.list_schedule);
		ArrayAdapter adapterSchedule = new ArrayAdapter<>(getActivity(),
				R.layout.popup_menu_items_events, R.id.text, arrayListSchedule);

		listViewSchedule.setAdapter(adapterSchedule);

		for (int i = 0; i < Constants.layoutsName.length; i++)
			listViewSchedule.setItemChecked(i, App.prefs.getScheduleLayout(Constants.layoutsName[i]));

		popupWindowSchedule = new PopupWindow(viewS,
				Utils.getDpValue(270, getActivity()),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindowSchedule.setBackgroundDrawable(new BitmapDrawable());
		popupWindowSchedule.setOutsideTouchable(true);
		popupWindowSchedule.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
//                layout_MainMenu.getForeground().setAlpha(0);
			}
		});
		cancel_schedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindowSchedule.dismiss();

			}
		});
		ok_schedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindowSchedule.dismiss();

			}
		});

		DragSortController controllerSchedule = new DragSortController(
				listViewSchedule);
		controllerSchedule.setDragHandleId(R.id.drag_handle);

		listViewSchedule.setOnTouchListener(controllerSchedule);
		listViewSchedule
				.setOnItemClickListener(new ListClickListenerSchedule());
	}

	public class ListClickListenerSchedule implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			position = position + 1;
			if (position == 2) {
				position = position + 1;
			} else if (position > 1) {
				position = position + 1;
			}
			int layoutId = inflatingLayouts.get(position);
			CheckedTextView checkedTextView = (CheckedTextView) view
					.findViewById(R.id.checkbox);
			if (checkedTextView.isChecked()) {
				aq.id(layoutId).visible();
				App.prefs.setScheduleLayout(Constants.addTaskLayouts[position], true);
			} else {
				aq.id(layoutId).gone();
				App.prefs.setScheduleLayout(Constants.addTaskLayouts[position], false);
			}
		}

	}

	private void saveSchedule(){
		if (aq.id(R.id.sch_title).getText().toString().equals("")) {
			Toast.makeText(getActivity(), "Please enter title",
					Toast.LENGTH_SHORT).show();
			return;
		}

		assignedId.clear();
		for (String key : AssignMultipleFragment.selectedInvitees.keySet())
			assignedId.add(key);

		MaxId = App.attach.getInt("3Max", 0);
		title = aq.id(R.id.sch_title).getText().toString();
		ToggleButton switCh = (ToggleButton) aq.id(R.id.switch_sch).getView();

		boolean isAllDay = switCh.isChecked();

		if(isAllDay){
			currentHours = 0;
			currentMin = 0;
			endEventHours = 0;
			endEventMin = 0;
		}

		String start_date = currentYear + "-"
				+ (currentMonDigit + 1) + "-"
				+ currentDayDigit + " "
				+ currentHours + ":"
				+ currentMin + ":00";
		String end_date = endEventYear + "-"
				+ (endEventMonDigit + 1) + "-"
				+ endEventDayDigit + " "
				+ endEventHours + ":"
				+ endEventMin + ":00";


		String location = aq.id(R.id.sch_location).getText().toString();

		String before = aq.id(R.id.before_schedule).getText().toString();
		boolean is_time = true, is_location = false;
		String r_location = "", location_tag = "";
		if (before.contains("On Arrive") || before.contains("On Leave")) {
			is_time = false;
			is_location = true;
			r_location = aq.id(R.id.location_before_sch).getText()
					.toString();

			location_tag = ((TextView) AddScheduleBeforeFragment.viewP)
					.getText().toString() ;
		}

		boolean is_alertEmail = false, is_alertNotification = false;
		if (!(aq.id(R.id.before_schedule).getText().toString()
				.isEmpty())) {
			is_alertEmail = aq.id(R.id.email_radio_sch).getCheckBox()
					.isChecked();
			is_alertNotification = aq.id(R.id.notification_radio_sch)
					.getCheckBox().isChecked();
		}

		boolean repeat_forever = aq.id(R.id.sch_forever_radio).isChecked();
		String repeat = aq.id(R.id.sch_repeat_txt).getText().toString();

		String label_name = aq.id(R.id.sch_label_txt).getText().toString();
		if(!(aq.id(R.id.add_sub_sch).getView() instanceof EditText))
			toggleCheckList(aq.id(R.id.add_sub_sch).getView());
		String checklist_data = aq.id(R.id.add_sub_sch).getEditText()
				.getText().toString();

		String notes = aq.id(R.id.notes_schedule).getText().toString();

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
		String locationtype = null;
		if (is_time) {
			reminderTime = Utils.getReminderTime(before);
			Log.e("reminder time", reminderTime+"");
		} else {
			if (before.contains("On Arrive")) {
				is_locationtype = 0;
				locationtype = "On Arrive";
			} else if (before.contains("On Leave")) {
				is_locationtype = 1;
				locationtype = "On Leave";
			}
		}
		long r_repeat = 0;
		if (repeat.contains("once") || repeat.contains("Once")) {
			r_repeat = 0;
			repeat = "once";
		} else if (repeat.contains("daily") || repeat.contains("daily")) {
			r_repeat = Constants.DAY;
			repeat = "daily";
		} else if (repeat.contains("weekly")
				|| repeat.contains("Weekly")) {
			r_repeat = Constants.WEEK;
			repeat = "weekly";
		} else if (repeat.contains("monthly")
				|| repeat.contains("Monthly")) {
			r_repeat = Constants.MONTH;
			repeat = "monthly";
		} else if (repeat.contains("yearly")
				|| repeat.contains("Yearly")) {
			r_repeat = Constants.YEAR;
			repeat = "yearly";
		}

		todo = new ToDo();
		todo.setUser_id(Constants.user_id);
		todo.setTodo_type_id(3);
		todo.setTitle(title);
		todo.setStart_date(startDateInMilli);
		todo.setEnd_date(endDateInMilli);
		todo.setIs_allday(isAllDay);
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

		Repeat repeaT = new Repeat();
		repeaT.setRepeat_interval(repeat);
		repeaT.setRepeat_until(r_repeat);
		repeaT.setIs_forever(repeat_forever);
		repeatdao.insert(repeaT);
		todo.setRepeat(repeaT);

		CheckList checklist = new CheckList();
		checklist.setTitle(checklist_data);
		checklistdao.insert(checklist);
		todo.setCheckList(checklist);

		alarm = new AlarmManagerBroadcastReceiver();
		geoFence = new Geofences(getActivity());

		if(isEditMode){
			todo.setId(todoId);
			tododao.update(todo);
		}else {
			AddToServer asyn = new AddToServer(title, 3, start_date, end_date, isAllDay, is_location, r_location, location_tag,
					locationtype, location, notes, repeatdate, repeat_forever, MaxId,
					AddTaskComment.comment, AddTaskComment.commenttime, checklist_data, assignedId, repeat, reminderTime, label_name, "", before, "", AddScheduleFragment.this);
			asyn.execute();
		}
		getActivity().getSupportFragmentManager().popBackStack();


	}
	private void db_initialize() {
		checklistdao = App.daoSession.getCheckListDao();
		labeldao = App.daoSession.getLabelDao();
		tododao = App.daoSession.getToDoDao();
		commentdao = App.daoSession.getCommentDao();
		repeatdao = App.daoSession.getRepeatDao();
		reminderdao = App.daoSession.getReminderDao();
	}

	@Override
	public void taskAdded() {
		todo.setTodo_server_id(TaskAdded.getInstance().id);
		tododao.insert(todo);

		editorAttach.clear().commit();
		editorComment.clear().commit();

		//have to refresh them all
		TaskListFragment.setAdapter(MainActivity.act, 0, null);
		TaskListFragment.setAdapter(MainActivity.act, 1, null);
		TaskListFragment.setAdapter(MainActivity.act, 2, null);

		App.updateTaskList(getActivity());
		if (!todo.getReminder().getLocation().isEmpty())
			addGeofence(todo);
		setAlarm();
	}

	private void populateValues() {
		todo = tododao.load(todoId);
		aq.id(R.id.sch_title).text(todo.getTitle());

		long reminder = todo.getReminder().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(reminder);
		currentYear = cal.get(Calendar.YEAR);
		currentMonDigit = cal.get(Calendar.MONTH) + 1;
		currentDayDigit = cal.get(Calendar.DAY_OF_MONTH);
		currentHours = cal.get(Calendar.HOUR_OF_DAY);
		currentMin = cal.get(Calendar.MINUTE);
		showRightDateAndTime();

		aq.id(R.id.sch_location).text(todo.getLocation());
		if(todo.getReminder().getShowable_format() != null){
			aq.id(R.id.before_schedule).text(todo.getReminder().getShowable_format()).visible();
		}

		if(todo.getRepeat().getShowable_format() != null) {
			int selectedPosition = 0;
			for(int i = 0; i < Constants.repeatArray.length; i++){
				if(Constants.repeatArray[i].equalsIgnoreCase(todo.getRepeat().getShowable_format())){
					selectedPosition = i;
					break;
				}
			}
			aq.id(R.id.sch_repeat_grid).getGridView().setItemChecked(selectedPosition, true);
			aq.id(R.id.sch_repeat_txt).text(todo.getRepeat().getShowable_format()).visible();
		}

		if(todo.getLabel().getLabel_color() != null) {
			aq.id(R.id.sch_label_txt).text(todo.getLabel().getLabel_name());
			GradientDrawable mDrawable = (GradientDrawable) getResources()
					.getDrawable(R.drawable.label_background);
			if (mDrawable != null) {
				mDrawable.setColor(Color.parseColor(todo.getLabel().getLabel_color()));
			}
			aq.id(R.id.sch_label_txt).getView().setBackground(mDrawable);
			aq.id(R.id.sch_label_txt).getTextView().setTextColor(Color.WHITE);
		}

		toggleCheckList(aq.id(R.id.add_sub_sch).getView());
		aq.id(R.id.add_sub_sch).text(todo.getCheckList().getTitle());
		toggleCheckList(aq.id(R.id.add_sub_sch).getView());

		aq.id(R.id.notes_schedule).text(todo.getNotes());
		int position = 0;
		try {
			for (int i = 0; i < TaskData.getInstance().result.todos.size(); i++) {
				if (Integer.valueOf(TaskData.getInstance().result.todos.get(i).id).equals(todo.getTodo_server_id())) {
					position = i;
					break;
				}
			}
			for (int i = 0; i < TaskData.getInstance().result.todos.get(position).todo_attachment.size(); i++)
				showAttachments(TaskData.getInstance().result.todos.get(position).todo_attachment.get(i));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	private void setAlarm(){
		if(todo.getReminder().getTime() != 0){
			alarm.setReminderAlarm(MainActivity.act, todo.getStart_date() - todo.getReminder().getTime(), title, todo.getLocation());
			alarm.SetNormalAlarm(MainActivity.act);
		}
		if(todo.getRepeat().getRepeat_until() != 0){ // TODO change it to real value
			alarm.setRepeatAlarm(MainActivity.act,todo.getRepeat().getRepeat_until());
		}
		else{
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
	private void showAttachments(String imageUrl){
		aq.id(R.id.attachment_layout).visible();
		try {
			final LinearLayout item = (LinearLayout) aq
					.id(R.id.added_image_outer_event).visible().getView();

			final View child = getActivity().getLayoutInflater().inflate(
					R.layout.image_added_layout, null);

			ImageView image = (ImageView) child
					.findViewById(R.id.image_added);

			ImageView imageMenu = (ImageView) child
					.findViewById(R.id.image_menu);
			Tag = Tag + 1;
			imageMenu.setTag(Tag);
			child.setId(Tag);
			imageMenu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ll_iner = (LinearLayout) item.findViewById(Integer
							.parseInt(arg0.getTag().toString()));

					if (popupWindowAttach.isShowing()) {
						popupWindowAttach.dismiss();

					} else {
						popupWindowAttach.showAsDropDown(arg0, 5, 0);
					}
				}
			});

			aq_menu.id(R.id.menu_item1).text("Save file")
					.clicked(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							popupWindowAttach.dismiss();
						}
					});
			aq_menu.id(R.id.menu_item2).text("Delete")
					.clicked(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (ll_iner != null){
								item.removeView(ll_iner);
							}
							popupWindowAttach.dismiss();
						}
					});

			ImageOptions options = new ImageOptions();
			options.round = 20;

			AQuery aq = new AQuery(child);
			aq.id(image).image(imageUrl, options);
			child.findViewById(R.id.image_menu);
			TextView text = (TextView) child
					.findViewById(R.id.image_added_text);
			TextView by = (TextView) child
					.findViewById(R.id.image_added_by);
			TextView size = (TextView) child
					.findViewById(R.id.image_added_size);
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
			by.setText("By " + App.prefs.getUserName()+" on " + sdf.format(cal.getTime()));
			size.setText("(7024 KB)");
			item.addView(child);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}