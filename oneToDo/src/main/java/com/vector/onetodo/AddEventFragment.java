package com.vector.onetodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.webkit.MimeTypeMap;
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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
import com.vector.onetodo.db.gen.Comment;
import com.vector.onetodo.db.gen.CommentDao;
import com.vector.onetodo.db.gen.Friends;
import com.vector.onetodo.db.gen.FriendsDao;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import it.feio.android.checklistview.interfaces.CheckListChangedListener;

public class AddEventFragment extends Fragment implements onTaskAdded {

	public static AQuery aq;
	private AQuery AQlabel, AQlabel_edit, AQlabel_del, aq_attach, aq_menu;
	private List<NameValuePair> pairs;
	private static int FragmentCheck = 0;
	private Uri filename;
	public static EditText taskTitle;
	private static int Tag = 0;
	private PopupWindow popupWindowAttach;
	static LinearLayout ll_iner;
	private static View previousSelected;
	static String setmon1;
	private View label_view, viewl;
	private int Label_postion = -1;
	private int dayPosition;
	private int lastCheckedId = -1;
	private ImageView last;
	private String plabel = null;
	public static String repeatdate = "";
	private int pposition = -1;
	private int itempos = -1;
	private int MaxId = -1;
	private Editor editor, editorattach;
	private AlertDialog add_new_label_alert, date_time_alert, label_edit, location_del,
			attach_alert;
	public static int currentHours, currentMin, currentDayDigit, currentYear,
			currentMonDigit, endEventHours, endEventMin, endEventDayDigit, endEventYear, endEventMonDigit;

	private String currentDay, currentMon, endEventDay, endEventMon, title;

	private int[] collapsingViews = { R.id.date_time_include_to,
			R.id.date_time_include_from, R.id.before_grid_view_linear_event,
			R.id.repeat_linear_layout, R.id.label_grid_view3 };

	private int[] allViews = { R.id.event_title, R.id.time_date_to,
			R.id.time_date_from, R.id.before_event_lay, R.id.repeat_event_lay,
			R.id.spinner_labels_event, R.id.spinner_label_layout };

	public static HashMap<Integer, Integer> inflatingLayoutsEvents = new HashMap<>();
	protected static final int RESULT_CODE = 123;
	private static final int TAKE_PICTURE = 1;
	public static final int RESULT_GALLERY = 0;
	public static final int PICK_CONTACT = 2;
	public static final int RESULT_DROPBOX = 3;
	public static final int RESULT_GOOGLEDRIVE = 4;
	private Uri imageUri;
	public static View allView;
	public static Activity act;
	private PopupWindow popupWindowEvent;
	private ArrayList<String> assignedId = new ArrayList<>();

	private ToDoDao tododao;
	private CheckListDao checklistdao;
	private FriendsDao friendsdao;
	private LabelDao labeldao;
	private ReminderDao reminderdao;
	private RepeatDao repeatdao;
	private CommentDao commentdao;

	private ToDo todo;
	private boolean isEditMode = false;
	private long todoId;
	private String labelColor;

	public static AddEventFragment newInstance(int position, boolean isEditMode, long todoId) {
		AddEventFragment myFragment = new AddEventFragment();
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
		View view = inflater.inflate(R.layout.add_event_fragment, container,
				false);
		aq = new AQuery(getActivity(), view);
		act = getActivity();
		editor = App.label.edit();
		editorattach = App.attach.edit();
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
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Event");
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
				return true;
			case R.id.action_show_hide:
				popupWindowEvent.showAtLocation(
						aq.id(R.id.content).getView(),
						Gravity.CENTER_HORIZONTAL, 0, 0);
				return true;
			case R.id.action_accept:
				saveEvent();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		dayPosition = getArguments().getInt("dayPosition", 0);
		allView = getView();

		currentYear = Utils.getCurrentYear(dayPosition);
		currentMonDigit = Utils.getCurrentMonthDigit(dayPosition);
		currentDayDigit = Utils.getCurrentDayDigit(dayPosition);
		currentDay = Utils.getCurrentDay(dayPosition, Calendar.SHORT);
		currentMon = Utils.getCurrentMonth(dayPosition, Calendar.SHORT);
		currentHours = Utils.getCurrentHours();
		currentMin = Utils.getCurrentMins();

		inflatingLayoutsEvents.put(0, R.layout.add_event_title);
		inflatingLayoutsEvents.put(1, R.layout.add_event_assign);
		inflatingLayoutsEvents.put(2, R.layout.add_event_details);
		inflatingLayoutsEvents.put(3, R.layout.add_event_time_date);
		inflatingLayoutsEvents.put(4, R.layout.add_event_location1);
		inflatingLayoutsEvents.put(5, R.layout.add_event_before);
		inflatingLayoutsEvents.put(6, R.layout.add_event_repeat);
		inflatingLayoutsEvents.put(7, R.layout.add_event_label);
		inflatingLayoutsEvents.put(8, R.layout.add_event_subtask);
		inflatingLayoutsEvents.put(9, R.layout.add_event_notes);
		inflatingLayoutsEvents.put(10, R.layout.add_event_image);

		inflateLayouts();


		aq.id(R.id.event_assign).clicked(new OnClickListener() {

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

		aq.id(R.id.cancel)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new OnClickListener() {

					@Override
					public void onClick(View v) {
						getActivity().finish();
					}
				});

		aq.id(R.id.time_date_to)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());

		aq.id(R.id.time_date_from)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());

		aq.id(R.id.event_title)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());

		aq.id(R.id.location_event)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());
		AutoCompleteTextView locationTextView = (AutoCompleteTextView) aq.id(
				R.id.location_event).getView();
		locationTextView.setAdapter(new PlacesAutoCompleteAdapter(
				getActivity(), android.R.layout.simple_spinner_dropdown_item));

		aq.id(R.id.before_event_lay).clicked(new GeneralOnClickListner());
		aq.id(R.id.repeat_event_lay).clicked(new GeneralOnClickListner());

		aq.id(R.id.grid_text)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new GeneralOnClickListner());

		// **************************************TypeFaces

		// *****************Title
		taskTitle = (EditText) aq.id(R.id.event_title).getView();

		TextView taskassign = (TextView) aq.id(R.id.event_assign).getView();

		taskassign.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				aq.id(R.id.event_assign).textColorId(R.color.active);
				aq.id(R.id.assign_event_button).background(
						R.drawable.assign_blue);
				aq.id(R.id.imageView1).background(R.drawable.next_arrow_black);

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		taskTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {

				if (taskTitle.getText().length() > 0)
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

		// *********************** Title End

		// ******************************ALL DAY sWITCH

		ToggleButton toggle = (ToggleButton) getActivity().findViewById(
				R.id.switch_event);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				if (isChecked) {
					aq.id(R.id.time_from).getTextView()
							.setVisibility(View.GONE);
					aq.id(R.id.time_to).getTextView().setVisibility(View.GONE);

					aq.id(R.id.time_picker).getView().setVisibility(View.GONE);
					aq.id(R.id.time_picker_event_end).getView()
							.setVisibility(View.GONE);

				} else {

					aq.id(R.id.time_picker).getView()
							.setVisibility(View.VISIBLE);
					aq.id(R.id.time_picker_event_end).getView()
							.setVisibility(View.VISIBLE);
					aq.id(R.id.all_day_image).background(R.drawable.allday);
					aq.id(R.id.time_from).getTextView()
							.setVisibility(View.VISIBLE);

					aq.id(R.id.time_to).getTextView()
							.setVisibility(View.VISIBLE);

				}
			}
		});

		// Date picker implementation
		final DatePicker dPicker = (DatePicker) aq.id(R.id.date_picker)
				.getView();
		int density = getResources().getDisplayMetrics().densityDpi;
		showRightDateAndTime();
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
		dPickerEvent.init(currentYear, currentMonDigit, currentDayDigit,
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

		// ************************** Date TIme END

		// **************************** Repeat
		aq.id(R.id.repeat_grid_view)
				.getGridView()
				.setAdapter(
						new ArrayAdapter<String>(getActivity(),
								R.layout.grid_layout_textview,
								Constants.repeatArray) {

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

		aq.id(R.id.repeat_grid_view).itemClicked(new OnItemClickListener() {

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
					view
							.setBackgroundResource(R.drawable.round_buttons_blue);
				}

				if (((TextView) view).getText().toString().equals("Never")) {
					aq.id(R.id.repeat_forever_radio).checked(true);
					aq.id(R.id.repeat_time_radio).textColor(
							Color.parseColor("#bababa"));
					aq.id(R.id.repeat_forever_radio).textColor(
							(getResources().getColor(R.color._777777)));
				}
				((TextView) view).setTextColor(Color.WHITE);
				view.setSelected(true);
				if (Constants.repeatArray[position] == "Never") {
					aq.id(R.id.repeat_event).text(
							Constants.repeatArray[position]);
				} else {
					aq.id(R.id.repeat_event).text(
							Constants.repeatArray[position]);
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

		aq.id(R.id.repeat_time_radio).textColor(Color.parseColor("#bababa"));
		final TextView set = (TextView) dateTimePickerDialog
				.findViewById(R.id.set);
		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDatePicker.clearFocus();
				set.requestFocus();
				date_time_alert.dismiss();

				aq.id(R.id.repeat_event).text(
						((TextView) previousSelected).getText().toString()
								+ " until " + setmon1);
				RadioButton rb = (RadioButton) aq.id(R.id.repeat_time_radio)
						.getView();
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
		aq.id(R.id.repeat_forever_radio).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (aq.id(R.id.repeat_event).getText().toString() == "Never") {
				} else {
					aq.id(R.id.repeat_event).text(
							((TextView) previousSelected).getText().toString());
				}

				aq.id(R.id.repeat_time_radio).textColor(
						Color.parseColor("#bababa"));
				aq.id(R.id.repeat_forever_radio).textColor(
						getResources().getColor(R.color._777777));
			}
		});

		aq.id(R.id.repeat_time_radio).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (aq.id(R.id.repeat_event).getText().toString()
						.equals("Never")) {
					Toast.makeText(getActivity(), "Please Select ...",
							Toast.LENGTH_SHORT).show();
					aq.id(R.id.repeat_time_radio).checked(false);
					aq.id(R.id.repeat_forever_radio).checked(true);
					aq.id(R.id.repeat_time_radio).textColor(
							Color.parseColor("#bababa"));
					aq.id(R.id.repeat_forever_radio).textColor(
							getResources().getColor(R.color._777777));
				} else {

					aq.id(R.id.repeat_time_radio).textColor(
							getResources().getColor(R.color._777777));
					aq.id(R.id.repeat_forever_radio).textColor(
							Color.parseColor("#bababa"));
					date_time_alert.show();
				}
			}
		});

		// ****************************** Label Dialog
		GridView gridView;

		View vie = getActivity().getLayoutInflater().inflate(
				R.layout.add_label_event, null, false);

		AQlabel = new AQuery(vie);
		final TextView label_text = (TextView) vie
				.findViewById(R.id.add_label_text_event);
		gridView = (GridView) vie.findViewById(R.id.add_label_grid_event);

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

		add_new_label_alert
				.setOnDismissListener(new DialogInterface.OnDismissListener() {

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
						mDrawable.setColor(Color
								.parseColor(Constants.label_colors_dialog[Label_postion]));
						Save(label_view.getId() + "" + itempos, label_text
								.getText().toString(), Label_postion);
						Label_postion = -1;
						((TextView) label_view).setBackground(mDrawable);
						((TextView) label_view).setText(label_text.getText()
								.toString());
						((TextView) label_view).setTextColor(Color.WHITE);

						aq.id(R.id.spinner_labels_event).text(
								((TextView) label_view).getText().toString());
						aq.id(R.id.spinner_labels_event).getTextView()
								.setBackground(label_view.getBackground());
						aq.id(R.id.spinner_labels_event).getTextView()
								.setTextColor(Color.WHITE);

						labelColor = Constants.label_colors_dialog[Label_postion];

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

		aq.id(R.id.label_event_grid_view)
				.getGridView()
				.setAdapter(
						new ArrayAdapter<String>(getActivity(),
								R.layout.grid_layout_label_text_view,
								Constants.labels_array) {

							@Override
							public View getView(int position, View convertView,
												ViewGroup parent) {
								TextView textView = (TextView) super.getView(
										position, convertView, parent);

								Load(textView.getId() + "" + position);
								Log.v("View id= ", textView.getId() + position
										+ "| " + plabel + " | " + pposition);

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
		aq.id(R.id.label_event_grid_view).getGridView()
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {

						itempos = position;
						label_view = view;
						if (!((TextView) view).getText().toString()
								.equalsIgnoreCase("new")) {
							aq.id(R.id.spinner_labels_event).text(
									((TextView) view).getText().toString());
							aq.id(R.id.spinner_labels_event).getTextView()
									.setBackground(view.getBackground());
							aq.id(R.id.spinner_labels_event).getTextView()
									.setTextColor(Color.WHITE);
							labelColor = Constants.label_colors_dialog[position];
						} else {
							add_new_label_alert
									.getWindow()
									.setBackgroundDrawable(
											new ColorDrawable(
													android.graphics.Color.TRANSPARENT));
							add_new_label_alert.show();
						}

					}

				});

		aq.id(R.id.label_event_grid_view).getGridView()
				.setOnItemLongClickListener(new LabelEditClickListener());
		LayoutInflater inflater5 = getActivity().getLayoutInflater();

		View dialogLayout6 = inflater5.inflate(R.layout.add_task_edit, null,
				false);
		AQlabel_edit = new AQuery(dialogLayout6);
		AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
		builder6.setView(dialogLayout6);
		label_edit = builder6.create();

		View dialoglayout7 = inflater5.inflate(R.layout.add_task_edit_delete,
				null, false);
		AQlabel_del = new AQuery(dialoglayout7);
		AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
		builder7.setView(dialoglayout7);
		location_del = builder7.create();
		AQlabel_del.id(R.id.edit_cencel).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				location_del.dismiss();
			}
		});

		AQlabel_del.id(R.id.edit_del).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Remove(viewl.getId() + "" + itempos);
				((TextView) viewl).setText("New");
				GradientDrawable mDrawable = (GradientDrawable) getResources()
						.getDrawable(R.drawable.label_simple);
				((TextView) viewl).setBackground(mDrawable);
				((TextView) viewl).setTextColor(R.color.mountain_mist);

				location_del.dismiss();
			}
		});

		AQlabel_edit.id(R.id.add_task_delete).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				label_edit.dismiss();
				location_del.show();
			}
		});

		AQlabel_edit.id(R.id.add_task_edit).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// aqd.id(R.id.add_label_text).text(((TextView)
				// viewl).getText().)
				AQlabel.id(R.id.label_title_event).text("Edit");
				AQlabel.id(R.id.save).text("Save");
				label_view = viewl;
				label_edit.dismiss();
				add_new_label_alert.show();
			}
		});
		aq.id(R.id.spinner_label_layout).clicked(new GeneralOnClickListner());

		// ********************************* Label END

		// +++++++++++++++Attachment
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
								+ System.currentTimeMillis() + ".JPG");
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(photo));
						imageUri = Uri.fromFile(photo);
						startActivityForResult(intent, TAKE_PICTURE);
					}
				});
		aq_attach
				.id(R.id.add_attachment_dropbox)
				.typeface(
						TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new OnClickListener() {
					@Override
					public void onClick(View v) {
						attach_alert.dismiss();
						Intent dropboxintent = new Intent(Intent.ACTION_GET_CONTENT);
						dropboxintent.setType("*/*");
						startActivityForResult(Intent
								.createChooser(dropboxintent
										.setPackage("com.dropbox.android"), "DropBox") , RESULT_DROPBOX);
					}
				});
		aq_attach
				.id(R.id.add_attachment_google)
				.typeface(TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
				.clicked(new OnClickListener() {
					@Override
					public void onClick(View v) {
						attach_alert.dismiss();
						Intent googleIntent = new Intent(Intent.ACTION_GET_CONTENT);
						googleIntent.setType("*/*");
						startActivityForResult(Intent
								.createChooser(googleIntent.setPackage("com.google.android.apps.docs"), "Google Drive") , RESULT_GOOGLEDRIVE);
					}
				});
		AlertDialog.Builder attach_builder = new AlertDialog.Builder(
				getActivity());
		attach_builder.setView(attachment);
		attach_alert = attach_builder.create();
		// Show image choose options

		// Gallery and Camera intent
		aq.id(R.id.event_attachment).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				attach_alert.show();
			}
		});

		/**
		 * View pager for before and location
		 *
		 */
		ViewPager pager = (ViewPager) aq.id(R.id.add_event_before_pager)
				.getView();

		pager.setAdapter(new AddEventBeforePagerFragment(getActivity()
				.getSupportFragmentManager()));

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) aq.id(
				R.id.add_task_before_tabs).getView();
		tabs.setDividerColorResource(android.R.color.transparent);
		tabs.setIndicatorColorResource(R.color._4d4d4d);
		tabs.setUnderlineColorResource(android.R.color.transparent);
		tabs.setTextSize(Utils.getPxFromDp(getActivity(), 16));
		tabs.setIndicatorHeight(Utils.getPxFromDp(getActivity(), 1));
		tabs.setAllCaps(false);
		tabs.setTypeface(TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE),Typeface.NORMAL);
		tabs.setShouldExpand(true);
		tabs.setViewPager(pager);

		builder4.setView(dateTimePickerDialog);
		date_time_alert = builder4.create();
		final TextView dayField = (TextView) dateTimePickerDialog
				.findViewById(R.id.day_field);
		final TextView monthField = (TextView) dateTimePickerDialog
				.findViewById(R.id.month_year_field);

		// Date picker implementation for forever dialog

		showRightDateAndTimeForDialog(dayField, monthField);
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
						showRightDateAndTimeForDialog(dayField, monthField);
					}

				});

		// *************************************** Check List

		lastCheckedId = ((RadioGroup) aq.id(R.id.priority_radio_buttons_event)
				.getView()).getCheckedRadioButtonId();
		((RadioGroup) aq.id(R.id.priority_radio_buttons_event).getView())
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						((RadioButton) group.findViewById(lastCheckedId))
								.setTextColor(getResources().getColor(
										R.color.deep_sky_blue));
						((RadioButton) group.findViewById(checkedId))
								.setTextColor(getResources().getColor(
										android.R.color.white));
						lastCheckedId = checkedId;
					}
				});

		View switchView = aq.id(R.id.add_sub_event).getView();
		toggleCheckList(switchView);

		if(isEditMode){
			populateValues();
		}

	}

	private void showRightDateAndTime() {
		String tempCurrentDayDigit = String.format("%02d", currentDayDigit);
		String tempCurrentHours = String.format("%02d", currentHours);
		String tempCurrentMins = String.format("%02d", currentMin);

		if (aq.id(R.id.date_time_include_to).getView().getVisibility() == View.VISIBLE) {
			aq.id(R.id.day_field_to).text(currentDay)
			/* .textColor(getResources().getColor(R.color.deep_sky_blue)) */;
			aq.id(R.id.date_field_to).text(
					tempCurrentDayDigit
							+ Utils.getDayOfMonthSuffix(currentDayDigit))
			/* .textColor(getResources().getColor(R.color.deep_sky_blue)) */;
			aq.id(R.id.month_year_field_to).text(currentMon);
			aq.id(R.id.time_to)
					.text(tempCurrentHours + " : " + tempCurrentMins);

		}

		if (aq.id(R.id.date_time_include_from).getView().getVisibility() == View.VISIBLE) {
			aq.id(R.id.day_field_from).text(currentDay);
			aq.id(R.id.date_field_from).text(
					tempCurrentDayDigit
							+ Utils.getDayOfMonthSuffix(currentDayDigit));
			aq.id(R.id.month_year_field_from).text(currentMon);
			aq.id(R.id.time_from).text(
					tempCurrentHours + " : " + tempCurrentMins);
		}
	}

	private void showRightDateAndTimeForDialog(View day, View month) {
		String tempCurrentDayDigit = String.format("%02d", currentDayDigit);
		String tempYear = String.valueOf(currentYear).substring(2, 4);
		aq.id(day).text(currentDay);
		aq.id(month).text(
				tempCurrentDayDigit
						+ Utils.getDayOfMonthSuffix(currentDayDigit) + " "
						+ currentMon + "," + tempYear);
	}

	public static void inflateLayouts() {
		GridLayout gridLayout = (GridLayout) allView
				.findViewById(R.id.inner_event_container);
		gridLayout.removeAllViews();
		for (int key : inflatingLayoutsEvents.keySet()) {
			View child = act.getLayoutInflater().inflate(
					inflatingLayoutsEvents.get(key), null);
			GridLayout.LayoutParams param = new GridLayout.LayoutParams();
			param.height = LayoutParams.WRAP_CONTENT;
			param.width = LayoutParams.MATCH_PARENT;
			param.rowSpec = GridLayout.spec(key);
			child.setId(inflatingLayoutsEvents.get(key));
			child.setLayoutParams(param);
			gridLayout.addView(child);
		}
	}

	/**
	 * Checlists added counter
	 */

	private class GeneralOnClickListner implements OnClickListener {

		@Override
		public void onClick(View v) {
			v.setFocusableInTouchMode(true);
			v.requestFocus();
			showCurrentView(v);
			setAllOtherFocusableFalse(v);
			if (v.getId() == R.id.event_title)
				Utils.showKeyboard(getActivity());
			else
				Utils.hidKeyboard(getActivity());
		}

	}

	private void setAllOtherFocusableFalse(View v) {
		for (int id : allViews)
			if (v.getId() != id) {
				aq.id(id).getView().setFocusableInTouchMode(false);
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
				if (null != data) {
					showImageURI(data.getData());
				}
				break;
			case RESULT_DROPBOX:
				if(null != data){
					showImageURI(data.getData());
				}
				break;
			case RESULT_GOOGLEDRIVE:
				if(null != data){
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


	public static void updateAssign(HashMap<String, String> names){
		StringBuilder builder = new StringBuilder();
		for(String key : names.keySet()){
			builder.append(names.get(key)+", ");
		}
		aq.id(R.id.event_assign).text(builder);
	}

	private void showImageURI(Uri selectedImage) {

		getActivity().getContentResolver().notifyChange(selectedImage, null);
		ContentResolver cr = getActivity().getContentResolver();
		String type = MimeTypeMap.getFileExtensionFromUrl(selectedImage
				.toString());

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
				ImageView imagemenu = (ImageView) child
						.findViewById(R.id.image_menu);
				Tag = Tag + 1;
				imagemenu.setTag(Tag);
				child.setId(Tag);

				imagemenu.setOnClickListener(new OnClickListener() {

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
				by.setText("By "+App.prefs.getUserName()+" on " + sdf.format(cal.getTime()));
				filename = selectedImage;
				File myFile = new File(selectedImage.toString());

				myFile.getAbsolutePath();
				imageupload();
				if (selectedImage.getLastPathSegment().contains(".")) {
					text.setText(selectedImage.getLastPathSegment());

				} else {
					text.setText(selectedImage.getLastPathSegment() + "."
							+ type);

				}

				size.setText("(" + (new File(selectedImage.getPath()).length())
						/ 1024 + " KB)");
				child.findViewById(R.id.image_cancel).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								item.removeView(child);
							}
						});

				item.addView(child);
			} catch (Exception e) {
				Toast.makeText(getActivity(), "Failed to load",
						Toast.LENGTH_SHORT).show();
				Log.e("Camera", e.toString());
			}
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
		aq.id(R.id.to_layout).background(R.drawable.input_fields_gray);
		aq.id(R.id.from_layout).background(R.drawable.input_fields_gray);
		aq.id(R.id.before_event_layout)
				.background(R.drawable.input_fields_gray);
		aq.id(R.id.repeat_layout).background(R.drawable.input_fields_gray);
		aq.id(R.id.spinner_label_layout).background(
				R.drawable.input_fields_gray);
	}

	private void showCurrentView(View v) {

		hideAll();

		switch (v.getId()) {
			case R.id.time_date_to:
				if (aq.id(R.id.date_time_include_to).getView().getVisibility() == View.GONE) {
					aq.id(R.id.date_time_include_to)
							.getView()
							.startAnimation(
									new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
											200, aq.id(R.id.date_time_include_to)
											.getView(), true));

					aq.id(R.id.to_layout).background(R.drawable.input_fields_blue);

				}
				break;
			case R.id.time_date_from:
				if (aq.id(R.id.date_time_include_from).getView().getVisibility() == View.GONE) {
					aq.id(R.id.date_time_include_from)
							.getView()
							.startAnimation(
									new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
											200, aq.id(R.id.date_time_include_from)
											.getView(), true));
					aq.id(R.id.from_layout)
							.background(R.drawable.input_fields_blue);

				}
				break;

			case R.id.before_event_lay:
				if (aq.id(R.id.before_grid_view_linear_event).getView()
						.getVisibility() == View.GONE) {
					if (aq.id(R.id.before_event).getText().toString() == "") {
						aq.id(R.id.before_event)
								.text(Constants.beforeArray[1] + " Before")
								.visibility(View.VISIBLE);

					}
					aq.id(R.id.before_grid_view_linear_event)
							.getView()
							.startAnimation(
									new ScaleAnimToShow(1.0f,1.0f,1.0f,0.0f,200,
											aq.id(R.id.before_grid_view_linear_event)
													.getView(), true));

					aq.id(R.id.before_event_layout).background(
							R.drawable.input_fields_blue);

				}
				break;
			case R.id.repeat_event_lay:
				if (aq.id(R.id.repeat_linear_layout).getView().getVisibility() == View.GONE) {
					if (aq.id(R.id.repeat_event).getText().toString() == "") {
						aq.id(R.id.repeat_event).text(Constants.repeatArray[2])
								.visibility(View.VISIBLE);

					}
					aq.id(R.id.repeat_linear_layout)
							.getView()
							.startAnimation(
									new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
											200, aq.id(R.id.repeat_linear_layout)
											.getView(), true));
					aq.id(R.id.repeat_layout).background(
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
				break;
		}

	}

	public class AddEventBeforePagerFragment extends FragmentStatePagerAdapter {

		public AddEventBeforePagerFragment(FragmentManager fm) {
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
					return "By time";
				case 1:
					return "At location";
				default:
					return "";// not the case

			}
		}

		@Override
		public Fragment getItem(int position) {
			return AddEventBeforeFragment.newInstance(position, isEditMode, todo != null ? todo.getReminder().getShowable_format() : "");
		}
	}

	private void toggleCheckList(View switchView) {
		View newView;

		/*
		 * Here is where the job is done. By simply calling an instance of the
		 * ChecklistManager we can call its methods.
		 */
		try {
			// Getting instance
			ChecklistManager mChecklistManager = ChecklistManager
					.getInstance(getActivity());

			/*
			 * These method are useful when converting from EditText to
			 * ChecklistView (but can be set anytime, they'll be used at
			 * appropriate moment)
			 */

			// Setting new entries hint text (if not set no hint
			// will be used)
			mChecklistManager.setNewEntryHint("Add a subtask...");
			// Let checked items are moved on bottom

			mChecklistManager.setMoveCheckedOnBottom(1);

			mChecklistManager
					.setCheckListChangedListener(new CheckListChangedListener() {

						@Override
						public void onCheckListChanged() {

						}
					});

			// Decide if keep or remove checked items when converting
			// back to simple text from checklist
			mChecklistManager.setKeepChecked(true);

			// I want to make checks symbols visible when converting
			// back to simple text from checklist
			mChecklistManager.setShowChecks(true);

			// Converting actual EditText into a View that can
			// replace the source or viceversa
			newView = mChecklistManager.convert(switchView);

			// Replacing view in the layout
			mChecklistManager.replaceViews(switchView, newView);

			// Updating the instance of the pointed view for
			// eventual reverse conversion
			switchView = newView;

		} catch (ViewNotSupportedException e) {
			// This exception is fired if the source view class is
			// not supported
			e.printStackTrace();
		}
	}

	private class LabelEditClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
									   int position, long arg3) {
			if (((TextView) arg1).getText().toString().equals("New")
					|| position < 3) {

			} else {
				AQlabel.id(R.id.add_label_text_event).text(
						((TextView) arg1).getText().toString());
				AQlabel_del.id(R.id.body).text(
						"Label " + ((TextView) arg1).getText().toString()
								+ " will be deleted");
				AQlabel_edit.id(R.id.add_task_edit_title).text(
						"Label: " + ((TextView) arg1).getText().toString());
				viewl = arg1;
				itempos = position;
				label_edit.show();
			}
			return false;
		}
	}

	public void Save(String id, String name, int label_position) {
		// 0 - for private mode
		editor.putString(2 + "key_label" + id, name); // Storing integer
		editor.putInt(2 + "key_color_position" + id, label_position); // Storing
		// float
		editor.commit();
	}

	public void Load(String id) {
		plabel = null;
		plabel = App.label.getString(2 + "key_label" + id, null); // getting
		// String
		Log.v("View id= ", id + "| " + plabel + " | " + pposition);

		pposition = App.label.getInt(2 + "key_color_position" + id, 0); // getting
		// String
	}

	public void Remove(String id) {
		editor.remove(2 + "key_label" + id); // will delete key name
		editor.remove(2 + "key_color_position" + id); // will delete key email
		editor.commit();
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
			if (convertView == null) { // if it's not recycled, initialize some
				// attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(Utils
						.convertDpToPixel(40, mContext), Utils
						.convertDpToPixel(40, mContext)));
			} else {
				imageView = (ImageView) convertView;
			}

			GradientDrawable mDrawable = (GradientDrawable) getResources()
					.getDrawable(R.drawable.label_background_dialog);
			mDrawable.setColor(Color
					.parseColor(Constants.label_colors_dialog[position]));
			imageView.setBackground(mDrawable);

			return imageView;
		}

	}

	private void showRightDateAndTimeForDialog() {
		String fff = String.valueOf(currentDayDigit).replace("th", "");
		setmon1 = fff + " " + currentMon + " " + currentYear;
		repeatdate = currentYear + "-" + (currentMonDigit + 1) + "-"
				+ currentDayDigit + " 00:00:00";
	}

	public void imageupload() {

		HttpEntity entity = null;

		Bitmap bm = null;
		try {
			bm = MediaStore.Images.Media.getBitmap(getActivity()
					.getContentResolver(), filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bm != null) {
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		}
		byte[] byteArray = baos.toByteArray();
		String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
		pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("image", encoded));

		try {
			entity = new UrlEncodedFormEntity(pairs, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Map<String, HttpEntity> param = new HashMap<String, HttpEntity>();
		param.put(AQuery.POST_ENTITY, entity);

		aq.ajax("http://api.heuristix.net/one_todo/v1/upload.php", param,
				JSONObject.class, new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject json,
										 AjaxStatus status) {
						String path = null;
						try {

							JSONObject obj1 = new JSONObject(json.toString());
							path = obj1.getString("path");

						} catch (Exception e) {
						}

						Loadattachmax();
						if (MaxId == 0) {
							MaxId = 1;
						} else {
							MaxId = MaxId + 1;
						}
						Saveattach(MaxId, path, "type");
						Log.v("Response", json.toString());

					}
				});
	}

	public void Saveattach(int id, String path, String type) {
		// 0 - for private mode
		editorattach.putInt("2Max", id);
		editorattach.putString(2 + "path" + id, path);
		editorattach.putString(2 + "type" + id, type); // Storing float
		editorattach.commit();
	}

	public void Loadattachmax() {
		MaxId = App.attach.getInt("2Max", 0);
	}

	public void Loadattach(int id) {
		App.attach.getString(2 + "path" + id, null);
		App.attach.getString(2 + "type" + id, null); // getting String
	}

	public void Removeattach(int id) {
		editorattach.remove(2 + "path" + id); // will delete key name
		editorattach.remove(2 + "type" + id); // will delete key email
		editorattach.commit();
	}

	private void dragAndDrop(){
		ArrayList<String> arrayListEvent = new ArrayList<>(Arrays.asList(Constants.layoutsName));

		final LayoutInflater inflater2 = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view2 = inflater2.inflate(R.layout.popup_menu_event, null,
				false);
		TextView cancel_event = (TextView) view2
				.findViewById(R.id.cancel_event);
		TextView ok_event = (TextView) view2.findViewById(R.id.ok_event);

		popupWindowEvent = new PopupWindow(view2, Utils.getDpValue(270, getActivity()),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		DragSortListView listViewEvent = (DragSortListView) view2.findViewById(R.id.list_event);
		ArrayAdapter<String> adapterEvent = new ArrayAdapter<>(getActivity(),
				R.layout.popup_menu_items_schedule, R.id.text, arrayListEvent);

		listViewEvent.setAdapter(adapterEvent);
		for (int i = 0; i < arrayListEvent.size(); i++)
			listViewEvent.setItemChecked(i, true);

		cancel_event.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindowEvent.dismiss();
			}
		});
		ok_event.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindowEvent.dismiss();
			}
		});

		DragSortController controllerEvent = new DragSortController(
				listViewEvent);
		controllerEvent.setDragHandleId(R.id.drag_handle);

		listViewEvent.setOnTouchListener(controllerEvent);
		listViewEvent.setOnItemClickListener(new ListClickListenerEvent());

		popupWindowEvent = new PopupWindow(view2, Utils.getDpValue(270, getActivity()),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindowEvent.setBackgroundDrawable(new BitmapDrawable());
		popupWindowEvent.setOutsideTouchable(true);
		popupWindowEvent.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
//                layout_MainMenu.getForeground().setAlpha(0);
			}
		});
	}
	public class ListClickListenerEvent implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			position = position + 1;
			if (position == 2) {
				position = position + 1;
			} else if (position > 1) {
				position = position + 1;
			}
			int layoutId = inflatingLayoutsEvents
					.get(position);
			CheckedTextView checkedTextView = (CheckedTextView) view
					.findViewById(R.id.checkbox);
			if (checkedTextView.isChecked()) {
				aq.id(layoutId).visible();
			} else {
				aq.id(layoutId).gone();
			}
		}

	}

	private void saveEvent(){
		assignedId.clear();

		if (!(aq.id(R.id.event_title).getText().toString().equals(""))) {

			MaxId = App.attach.getInt("2Max", 0);

			title = aq.id(R.id.event_title).getText().toString();

			ToggleButton switCh = (ToggleButton) aq.id(R.id.switch_event).getView();
			boolean is_allday = switCh.isChecked();

			String start_date = AddEventFragment.currentYear+ "-"
					+ (AddEventFragment.currentMonDigit + 1) + "-"
					+  AddEventFragment.currentDayDigit + " "
					+ AddEventFragment.currentHours + ":"
					+ AddEventFragment.currentMin +":00";
			String end_date = AddEventFragment.endEventYear + "-"
					+ (AddEventFragment.endEventMonDigit + 1) + "-"
					+ AddEventFragment.endEventDayDigit + " "
					+ AddEventFragment.endEventHours + ":"
					+ AddEventFragment.endEventMin +":00";

			String location = aq.id(R.id.location_event).getText().toString();
			String r_location= "", location_tag="";
			boolean is_time = false, is_location = false;
			String before = aq.id(R.id.before_event).getText().toString();
			if (before.contains("On Arrive") || before.contains("On Leave")) {
				is_time = false;
				is_location = true;
				r_location = aq.id(R.id.location_before_event).getText()
						.toString();
				location_tag = ((TextView) AddEventBeforeFragment.viewP)
						.getText().toString() + "";
			}

			boolean is_alertEmail = false, is_alertNotification = false;
			if (!(aq.id(R.id.before_event).getText().toString().equals("") || aq
					.id(R.id.before_event).getText().toString() == null)) {
				is_alertEmail = aq.id(R.id.email_radio_event).getCheckBox()
						.isChecked();
				is_alertNotification = aq.id(R.id.notification_radio_event)
						.getCheckBox().isChecked();
			}

			boolean repeat_forever = aq.id(R.id.repeat_forever_radio).isChecked();
			String repeat = aq.id(R.id.repeat_event).getText().toString();
			repeatdate = AddEventFragment.repeatdate;

			String label_name = aq.id(R.id.spinner_labels_event).getText()
					.toString();
			if(!(aq.id(R.id.add_sub_event).getView() instanceof EditText))
				toggleCheckList(aq.id(R.id.add_sub_event).getView());
			String checklist_data = aq.id(R.id.add_sub_event).getEditText()
					.getText().toString();

			String notes = aq.id(R.id.notes_event).getText().toString();
			for (String key : AssignMultipleFragment.selectedInvitees.keySet())
				assignedId.add(key);

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
			Log.e("before", before);
			if (before != null || before.isEmpty()) {
				if (is_time) {
					reminderTime = Utils.getReminderTime(before);
					Log.e("reminder time", reminderTime+"");
				} else {
					//TODO set notification by GEO fence
					Geofences geoFence = new Geofences(getActivity());
					if (before.contains("On Arrive")) {
						is_locationtype = 0;
						locationType = "On Arrive";
						geoFence.addGeofence(App.gpsTracker.getLatitude(),App.gpsTracker.getLongitude(), 100, Geofence.GEOFENCE_TRANSITION_ENTER, Geofence.GEOFENCE_TRANSITION_ENTER);
					} else if (before.contains("On Leave")) {
						is_locationtype = 1;
						locationType = "On Leave";
						geoFence.addGeofence(App.gpsTracker.getLatitude(),App.gpsTracker.getLongitude(), 100, Geofence.GEOFENCE_TRANSITION_EXIT, Geofence.GEOFENCE_TRANSITION_EXIT);
					}
				}
			}
			long r_repeat = 0;
			if (repeat != null) {
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
			}
			AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();

			todo = new ToDo();
			todo.setUser_id(Constants.user_id);
			todo.setTodo_type_id(2);
			todo.setTitle(title);
			todo.setStart_date(startDateInMilli);
			todo.setEnd_date(endDateInMilli);
			todo.setIs_allday(is_allday);
			todo.setLocation(location);
			todo.setNotes(notes);
			todo.setIs_done(false);
			todo.setIs_delete(false);

			Label label = new Label();
			label.setLabel_name(label_name);
			label.setLabel_color(labelColor);
			labeldao.insert(label);
			todo.setLabel(label);


			Reminder reminder = new Reminder();
			reminder.setIs_alertEmail(is_alertEmail);
			reminder.setIs_alertNotification(is_alertNotification);
			reminder.setIs_time_location(is_location);
			reminder.setLocation(r_location);
			reminder.setLocation_type(is_locationtype);
			if ((!location_tag.equals("New")) && location_tag != null) {
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

			if (AddTaskComment.comment != null && AddTaskComment.comment.size() > 0) {
				for (int i = 0; i < AddTaskComment.comment.size(); i++) {

					Comment commenT = new Comment();
					commenT.setComment(AddTaskComment.comment.get(i));
					commenT.setToDo(todo);
					commentdao.insert(commenT);
				}
			}

			Friends friend = new Friends();
			friend.setEmail("email");
			friendsdao.insert(friend);
			todo.setReminder(reminder);

			TaskListFragment.setAdapter(getActivity(), TaskListFragment.position);

			if(reminderTime != 0){
				alarm.setReminderAlarm(getActivity(), startDateInMilli - reminderTime, title, location);
				alarm.SetNormalAlarm(getActivity());
			}
			if(r_repeat != 0){
				alarm.setRepeatAlarm(getActivity(), r_repeat);
			}
			else{
				alarm.SetNormalAlarm(getActivity());
			}


			AddToServer asyn = new AddToServer(title, 2, start_date, end_date, is_location, r_location, location_tag,
					locationType, notes, repeatdate,repeat_forever, MaxId,
					AddTaskComment.comment, null, checklist_data, assignedId, repeat, label_name, "", before, "", AddEventFragment.this);
			asyn.execute();
			getActivity().getSupportFragmentManager().popBackStack();

		}else
			Toast.makeText(getActivity(), "Please enter title",
					Toast.LENGTH_SHORT).show();
	}
	private void db_initialize() {
		checklistdao = App.daoSession.getCheckListDao();
		friendsdao = App.daoSession.getFriendsDao();
		labeldao = App.daoSession.getLabelDao();
		tododao = App.daoSession.getToDoDao();
		commentdao = App.daoSession.getCommentDao();
		repeatdao = App.daoSession.getRepeatDao();
		reminderdao = App.daoSession.getReminderDao();
	}

	private void populateValues() {


		todo = tododao.load(todoId);
		aq.id(R.id.event_title).text(todo.getTitle());
		long reminder = todo.getReminder().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(reminder);
		currentYear = cal.get(Calendar.YEAR);
		currentMonDigit = cal.get(Calendar.MONTH) + 1;
		currentDayDigit = cal.get(Calendar.DAY_OF_MONTH);
		currentHours = cal.get(Calendar.HOUR_OF_DAY);
		currentMin = cal.get(Calendar.MINUTE);
		showRightDateAndTime();
		aq.id(R.id.location_event).text(todo.getLocation());
		if(todo.getReminder().getShowable_format() != null){
			aq.id(R.id.before_event).text(todo.getReminder().getShowable_format()).visible();
		}

		if(todo.getRepeat().getShowable_format() != null) {
			int selectedPosition = 0;
			for(int i = 0; i < Constants.repeatArray.length; i++){
				if(Constants.repeatArray[i].equalsIgnoreCase(todo.getRepeat().getShowable_format())){
					selectedPosition = i;
					break;
				}
			}
			aq.id(R.id.repeat_grid_view).getGridView().setItemChecked(selectedPosition, true);
			aq.id(R.id.repeat_event).text(todo.getRepeat().getShowable_format()).visible();
		}

		if(todo.getLabel().getLabel_color() != null) {
			aq.id(R.id.spinner_labels_event).text(todo.getLabel().getLabel_name());
			GradientDrawable mDrawable = (GradientDrawable) getResources()
					.getDrawable(R.drawable.label_background);
			mDrawable.setColor(Color.parseColor(todo.getLabel().getLabel_color()));
			aq.id(R.id.spinner_labels_event).getView().setBackground(mDrawable);
			aq.id(R.id.spinner_labels_event).getTextView().setTextColor(Color.WHITE);
		}

		toggleCheckList(aq.id(R.id.add_sub_event).getView());
		aq.id(R.id.add_sub_event).text(todo.getCheckList().getTitle());
		toggleCheckList(aq.id(R.id.add_sub_event).getView());

		aq.id(R.id.notes_event).text(todo.getNotes());
		int position = 0;
		aq.id(R.id.notes_task).text(todo.getNotes());
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

	@Override
	public void taskAdded() {
		todo.setTodo_server_id(TaskAdded.getInstance().id);
		tododao.insert(todo);
		App.updateTaskList(getActivity());
		setAlarm();
	}

	private void setAlarm(){
		AlarmManagerBroadcastReceiver alarm=new AlarmManagerBroadcastReceiver();
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

//            if (selectedImage.getLastPathSegment().contains(".")) {
//                text.setText(selectedImage.getLastPathSegment());
//            } else {
//                text.setText(selectedImage.getLastPathSegment() + "." + type);
//            }
			size.setText("(7024 KB)");
			item.addView(child);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}