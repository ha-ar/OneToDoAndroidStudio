package com.vector.onetodo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.simonvt.datepicker.DatePicker;
import net.simonvt.datepicker.DatePicker.OnDateChangedListener;
import net.simonvt.timepicker.TimePicker;
import net.simonvt.timepicker.TimePicker.OnTimeChangedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.devspark.appmsg.AppMsg;
import com.vector.onetodo.utils.Constants;
import com.vector.onetodo.utils.ScaleAnimToHide;
import com.vector.onetodo.utils.ScaleAnimToShow;
import com.vector.onetodo.utils.TypeFaces;
import com.vector.onetodo.utils.Utils;

public class AddProjectFragment extends Fragment {

	// event_attachment
	public static AQuery aq, popupAQ, aqloc, AQlabel, AQlabel_edit,
			AQlabel_del;

	View label_view, viewl;
	GradientDrawable label_color;
	int Label_postion = -1;
	private int lastCheckedId = -1;
	ImageView last;
	String plabel = null;
	int pposition = -1;
	int itempos = -1;
	int MaxId = -1;
	public static EditText taskTitle;

	static List<java.lang.Object> names;

	public static HashMap<Integer, Integer> inflatingLayouts = new HashMap<Integer, Integer>();

	private String currentDay, currentMon;
	static String checkedId2 = null;
	private Uri imageUri;
	public static final int RESULT_GALLERY = 0;

	public static final int PICK_CONTACT = 2;

	private static final int TAKE_PICTURE = 1;

	static LinearLayout lll;

	AlertDialog  add_new_label_alert, assig_alert,location_del,label_edit,
			share_alert, date_time_alert;
	static int currentHours, currentMin, currentDayDigit, currentYear,
			currentMonDigit;

	int Month, Year;

	private int[] collapsingViews = { R.id.date_time_include,
			R.id.label_project_grid_view };

	private int[] allViews = { R.id.time_date, R.id.spinner_label_layout };

	int dayPosition;
	Editor editor;
	EditText label_field = null;

	protected static final int RESULT_CODE = 123;

	public static View allView;

	public static Activity act;

	public static AddProjectFragment newInstance(int position, int dayPosition) {
		AddProjectFragment myFragment = new AddProjectFragment();
		Bundle args = new Bundle();
		args.putInt("position", position);
		args.putInt("dayPosition", dayPosition);
		myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.project_fragment, container,
				false);

		aq = new AQuery(getActivity(), view);
		act = getActivity();

		return view;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		allView = getView();
		editor = AddTask.label.edit();
		dayPosition = getArguments().getInt("dayPosition", 0);
		currentDay = Utils.getCurrentDay(dayPosition, Calendar.SHORT);
		currentYear = Utils.getCurrentYear(dayPosition);
		currentMonDigit = Utils.getCurrentMonthDigit(dayPosition);
		currentDayDigit = Utils.getCurrentDayDigit(dayPosition);
		currentDay = Utils.getCurrentDay(dayPosition, Calendar.SHORT);
		currentMon = Utils.getCurrentMonth(dayPosition, Calendar.SHORT);
		currentHours = Utils.getCurrentHours();
		currentMin = Utils.getCurrentMins();

		inflatingLayouts.put(0, R.layout.add_project_title);
		inflatingLayouts.put(1, R.layout.add_project_date);
		inflatingLayouts.put(2, R.layout.add_project_label);
		inflatingLayouts.put(3, R.layout.add_project_note);

		inflateLayouts();
		main();

		taskTitle = (EditText) aq.id(R.id.project_title).getView();

		taskTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (taskTitle.getText().length() > 0)
					AddTask.btn.setAlpha(1);

				aq.id(R.id.completed_project).textColorId(R.color.active);

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

	public void main() {

		aq.id(R.id.time_date)
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
				aq.id(R.id.time_field).visible();
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

		// **********************END DATE TIME

		// ****************************** Label Dialog

		aq.id(R.id.time_date).clicked(new GeneralOnClickListner());
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
				// TODO Auto-generated method stub

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
				// TODO Auto-generated method stub

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

						aq.id(R.id.spinner_labels_project).text(
								((TextView) label_view).getText().toString());
						aq.id(R.id.spinner_labels_project).getTextView()
								.setBackground(label_view.getBackground());
						aq.id(R.id.spinner_labels_project).getTextView()
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
		aq.id(R.id.label_project_grid_view)
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
									mDrawable.setColor(Color
											.parseColor(Constants.label_colors[position]));
									textView.setBackground(mDrawable);
								}
								if (plabel != null) {
									textView.setTextColor(Color.WHITE);
									textView.setText(plabel);
									GradientDrawable mDrawable = (GradientDrawable) getResources()
											.getDrawable(
													R.drawable.label_background);
									mDrawable.setColor(Color
											.parseColor(Constants.label_colors_dialog[pposition]));
									textView.setBackground(mDrawable);
								}
								return textView;
							}

						});
		aq.id(R.id.label_project_grid_view).getGridView()
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						itempos = position;
						label_view = view;
						if (!((TextView) view).getText().toString()
								.equalsIgnoreCase("new")) {
							aq.id(R.id.spinner_labels_project).text(
									((TextView) view).getText().toString());
							aq.id(R.id.spinner_labels_project).getTextView()
									.setBackground(view.getBackground());
							aq.id(R.id.spinner_labels_project).getTextView()
									.setTextColor(Color.WHITE);
						} else {
							add_new_label_alert.show();
						}

					}

				});

		aq.id(R.id.label_project_grid_view).getGridView()
				.setOnItemLongClickListener(new LabelEditClickListener());
		LayoutInflater inflater5 = getActivity().getLayoutInflater();

		View dialoglayout6 = inflater5.inflate(R.layout.add_task_edit, null,
				false);
		AQlabel_edit = new AQuery(dialoglayout6);
		AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
		builder6.setView(dialoglayout6);
		label_edit = builder6.create();

		View dialoglayout7 = inflater5.inflate(R.layout.add_task_edit_delete,
				null, false);
		AQlabel_del = new AQuery(dialoglayout7);
		AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
		builder7.setView(dialoglayout7);
		location_del = builder7.create();
		aq.id(R.id.spinner_label_layout).clicked(new GeneralOnClickListner());

		// ********************************* Label END

		aq.id(R.id.projec_subtask_lay).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				//PageIndicator mIndicator= (CirclePageIndicator) getActivity().findViewById(R.id.indicator);
				//aq.id(R.id.indicator).getView().setVisibility(View.GONE);

				Constants.Project_task_check=1;
				Fragment fr=new AddTaskFragment();
				FragmentManager manager=getFragmentManager();
				FragmentTransaction trans=manager.beginTransaction();
				Bundle b=new Bundle();
				b.putInt("dayPosition",dayPosition );
				fr.setArguments(b);
				trans.replace(R.id.main_container, fr);
				
				trans.addToBackStack("ADDPROJECT");
				
				trans.commit();
			}
		});
	}

	public static void inflateLayouts() {
		GridLayout gridLayout = (GridLayout) allView
				.findViewById(R.id.inner_event_container);
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

	private void hideAll() {
		for (int view : collapsingViews)
			if (aq.id(view).getView() != null
					&& aq.id(view).getView().getVisibility() == View.VISIBLE)
				aq.id(view)
						.getView()
						.startAnimation(
								new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
										200, aq.id(view).getView(), true));
		aq.id(R.id.spinner_label_layout).background(R.drawable.input_fields_gray);
		aq.id(R.id.project_time_date).background(R.drawable.input_fields_gray);
		
	}

	private void showCurrentView(View v) {

		hideAll();

		switch (v.getId()) {
		case R.id.time_date:
			if (aq.id(R.id.date_time_include).getView().getVisibility() == View.GONE){
				aq.id(R.id.date_time_include)
						.getView()
						.startAnimation(
								new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
										200, aq.id(R.id.date_time_include)
												.getView(), true));

				aq.id(R.id.project_time_date).background(R.drawable.input_fields_blue);
			}
			break;
		case R.id.spinner_label_layout:
			if (aq.id(R.id.label_project_grid_view).getView().getVisibility() == View.GONE)
				{
				aq.id(R.id.label_project_grid_view)
						.getView()
						.startAnimation(
								new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
										200, aq.id(R.id.label_project_grid_view)
												.getView(), true));

				aq.id(R.id.spinner_label_layout).background(R.drawable.input_fields_blue);
			}
			break;

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
			if (v.getId() == R.id.time_date
					|| v.getId() == R.id.spinner_label_layout)
				Utils.showKeyboard(getActivity());
			else
				Utils.hidKeyboard(getActivity());
		}

	}

	public void slideUpDown(final View view) {
		if (!isPanelShown(view)) {
			// Show the panel
			Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
					R.anim.bottom_up);

			view.startAnimation(bottomUp);
			view.setVisibility(View.VISIBLE);
			Drawable tintColor = new ColorDrawable(getResources().getColor(
					R.color.dim_background));
		 
		} else {
			// Hide the Panel
			Animation bottomDown = AnimationUtils.loadAnimation(getActivity(),
					R.anim.bottom_down);

			view.startAnimation(bottomDown);
			view.setVisibility(View.GONE);
			Drawable tintColor = new ColorDrawable(getResources().getColor(
					android.R.color.transparent));
		 
		}
	}

	private boolean isPanelShown(View view) {
		return view.getVisibility() == View.VISIBLE;
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
		try {
			bitmap = Utils.getBitmap(selectedImage, getActivity(), cr);
			final LinearLayout item = (LinearLayout) aq
					.id(R.id.added_image_outer).visible().getView();

			final View child = getActivity().getLayoutInflater().inflate(
					R.layout.image_added_layout, null);
			ImageView image = (ImageView) child.findViewById(R.id.image_added);
			aq.id(image).image(Utils.getRoundedCornerBitmap(bitmap, 20));
			TextView text = (TextView) child
					.findViewById(R.id.image_added_text);
			String filename = selectedImage.getPath();
			text.setText(filename);
			child.findViewById(R.id.image_cancel).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							item.removeView(child); 
						}
					});
			item.addView(child);
		} catch (Exception e) {
			Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT)
					.show();
			Log.e("Camera", e.toString());
		}
	}

	private void showRightDateAndTime() {
		String tempCurrentDayDigit = String.format("%02d", currentDayDigit);
		String tempCurrentHours = String.format("%02d", currentHours);
		String tempCurrentMins = String.format("%02d", currentMin);
		String tempYear = String.valueOf(currentYear).substring(2, 4);
		aq.id(R.id.da).text("Due");
		if (dayPosition == 0) {
			aq.id(R.id.day_field).text("");
			aq.id(R.id.day_field).text("Today");
		} else if (dayPosition == 1) {
			aq.id(R.id.day_field).text("");
			aq.id(R.id.day_field).text("Tomorrow");
		} else {
			aq.id(R.id.day_field).text(currentDay);
			aq.id(R.id.month_year_field).text(
					tempCurrentDayDigit
							+ Utils.getDayOfMonthSuffix(currentDayDigit) + " "
							+ currentMon);
		}
		aq.id(R.id.time_field).text(tempCurrentHours + ":" + tempCurrentMins);
	}
	private class LabelEditClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
				int position, long arg3) {
			// TODO Auto-generated method stub
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

	public void Save(String id, String name, int label_position) {
		// 0 - for private mode
		editor.putString(5 + "key_label" + id, name); // Storing integer
		editor.putInt(5 + "key_color_position" + id, label_position); // Storing
																		// float
		editor.commit();
	}

	public void Load(String id) {
		plabel = null;
		plabel = AddTask.label.getString(5 + "key_label" + id, null); // getting
																		// String
		Log.v("View id= ", id + "| " + plabel + " | " + pposition);

		pposition = AddTask.label.getInt(5 + "key_color_position" + id, 0); // getting
																			// String
	}

	public void Remove(String id) {
		editor.remove(5 + "key_label" + id); // will delete key name
		editor.remove(5 + "key_color_position" + id); // will delete key email
		editor.commit();
	}
}
