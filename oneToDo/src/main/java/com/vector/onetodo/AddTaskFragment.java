package com.vector.onetodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.astuetz.PagerSlidingTabStrip;
import com.dd.CircularProgressButton;
import com.devspark.appmsg.AppMsg;
import com.google.android.gms.location.Geofence;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.vector.model.TaskAdded;
import com.vector.model.TaskData;
import com.vector.onetodo.db.gen.Attach;
import com.vector.onetodo.db.gen.CheckList;
import com.vector.onetodo.db.gen.CheckListDao;
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
import it.feio.android.checklistview.interfaces.CheckListChangedListener;

public class AddTaskFragment extends Fragment implements onTaskAdded {

    public static final int RESULT_GALLERY = 0;
    public static final int PICK_CONTACT = 2;
    public static final int RESULT_DROPBOX = 3;
    public static final int RESULT_GOOGLEDRIVE = 4;
    private static final int TAKE_PICTURE = 1;
    public static EditText taskTitle;
    public static HashMap<Integer, Integer> inflatingLayouts = new HashMap<>();
    public static View allView;
    public static Activity act;
    public static String assignedSelectedID = "";
    static LinearLayout ll_linear;
    static int FragmentCheck = 0;
    static String repeatDate = "";
    static String title = "";
    static Dialog add_new_label_alert,
            date_time_alert, attach, location_del, label_edit;
    static int currentHours, currentMin, currentDayDigit, currentYear,
            currentMonDigit;
    private static int Tag = 0;
    private static AQuery aq;
    private static AQuery aq_edit;
    private static AQuery aqd;
    private static AQuery aq_del;
    private static AQuery aq_menu;
    private static View previousSelected;
    int labelPosition = -1;
    ImageView last;
    View label_view , viewl;
    int dayPosition;
    private Editor editor, editorAttach, editorComment;
    private String pLabel = "";
    private int mPosition = -1;
    private int itemPosition = -1;
    private int MaxId = -1;
    private PopupWindow popupWindowAttach;
    private String currentDay, currentMon, setMon;
    private int[] collapsingViews = { R.id.title_task_layout1,
            R.id.date_time_include, R.id.before_grid_view_linear,
            R.id.repeat_linear_layout, R.id.label_grid_view3 };
    private int[] allViews = { R.id.task_title1, R.id.time_date,
            R.id.location_task, R.id.before1, R.id.repeat_task_lay,
            R.id.spinner_labels_task };
    private Uri imageUri;
    private int lastCheckedId = -1;
    private ArrayAdapter<String> adapterTask;
    private PopupWindow popupWindowTask;
    private String labelColor = "";
    private boolean isProjectSubTask = false;
    private ArrayList<String> assignedId = new ArrayList<>();

    private ToDoDao tododao;
    private CheckListDao checklistdao;
    private LabelDao labeldao;
    private ReminderDao reminderdao;
    private RepeatDao repeatdao;

    private AlarmManagerBroadcastReceiver alarm;
    private Geofences geoFence;

    //in case of edit to do object is required
    private ToDo todo;
    private boolean isEditMode = false;
    private long todoId;

    private ArrayList<Attach> attachArrayList = new ArrayList<>();
    private DragSortListView.DropListener onDropTask = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                String item = adapterTask.getItem(from);
                adapterTask.remove(item);
                adapterTask.insert(item, to);
                inflateLayouts();
            }
        }
    };
    private boolean isAttaching = false;

    public static AddTaskFragment newInstance(int position, boolean isEditMode, long todoId) {
        AddTaskFragment myFragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putLong("todoId", todoId);
        args.putBoolean("isEditMode", isEditMode);
        myFragment.setArguments(args);
        return myFragment;
    }

    public static void updateAssign(String name){
        aq.id(R.id.task_assign).text(name);
    }

    private void inflateLayouts() {
        GridLayout gridLayout = (GridLayout) allView
                .findViewById(R.id.inner_container);
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
            if(!App.prefs.getTaskLayout(Constants.addTaskLayouts[key]))
                child.setVisibility(View.GONE);
            gridLayout.addView(child);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_task_fragment, container,
                false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
        if (toolbar != null)
            ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);

        editor = App.label.edit();
        editorAttach = App.attach.edit();
        editorComment = App.comment.edit();
        initActionBar();
        aq = new AQuery(getActivity(), view);
        act = getActivity();
        dragAndDrop();
        db_initialize();
        isEditMode = getArguments().getBoolean("isEditMode");
        todoId = getArguments().getLong("todoId");
        setRetainInstance(true);
        return view;
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
                transaction.replace(R.id.content_task, AddTaskComment.newInstance(false, -1));
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.action_show_hide:
                popupWindowTask.showAtLocation(
                        aq.id(R.id.content_task).getView(),
                        Gravity.CENTER_HORIZONTAL, 0, 0);
                return true;
            case R.id.action_accept:
                saveTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar(){
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Task");
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (Constants.Project_task_check == 1) {
//            aq.id(R.id.addtask_header).getView().setVisibility(View.VISIBLE);
//        }
        dayPosition = getArguments().getInt("dayPosition", 0);
        isProjectSubTask = getArguments().getBoolean("is_sub_task", false);
        allView = getView();

        currentYear = Utils.getCurrentYear(dayPosition);
        currentMonDigit = Utils.getCurrentMonthDigit(dayPosition);
        currentDayDigit = Utils.getCurrentDayDigit(dayPosition);
        currentDay = Utils.getCurrentDay(dayPosition, Calendar.SHORT);
        currentMon = Utils.getCurrentMonth(dayPosition, Calendar.SHORT);
        currentHours = Utils.getCurrentHours() + 1;
        currentMin = Utils.getCurrentMins();


        inflatingLayouts.put(0, R.layout.add_task_title);
        inflatingLayouts.put(1, R.layout.add_task_assign1);
        inflatingLayouts.put(2, R.layout.add_task_details);
        inflatingLayouts.put(3, R.layout.add_task_time_date);
        inflatingLayouts.put(4, R.layout.add_task_location1);
        inflatingLayouts.put(5, R.layout.add_task_before);
        inflatingLayouts.put(6, R.layout.add_task_repeat);
        inflatingLayouts.put(7, R.layout.add_task_label);
        inflatingLayouts.put(8, R.layout.add_task_subtask);
        inflatingLayouts.put(9, R.layout.add_task_notes);
        inflatingLayouts.put(10, R.layout.add_task_attach);

        inflateLayouts();

        if(isEditMode){
            populateValues();
        }

        aq.id(R.id.task_assign).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                title = aq.id(R.id.task_title1).getText().toString();
//				Fragment fr = new AddTaskAssign();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_right,
                        R.anim.slide_left, R.anim.slide_right, R.anim.slide_left);
                transaction.replace(R.id.content_task, AddTaskAssign.newInstance(0));
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
                        if (Constants.Project_task_check == 1)
                            getFragmentManager().popBackStack();
                        else
                            getActivity().finish();
                    }
                });

        aq.id(R.id.time_date)
                .typeface(
                        TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
                .clicked(new GeneralOnClickListner());

        aq.id(R.id.task_title1)
                .typeface(
                        TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
                .clicked(new GeneralOnClickListner());

        aq.id(R.id.location_task)
                .typeface(
                        TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
                .clicked(new GeneralOnClickListner());
//                .text(App.gpsTracker.getLocality(getActivity()));
        AutoCompleteTextView locationTextView = (AutoCompleteTextView) aq.id(R.id.location_task).getView();
        locationTextView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item));

        aq.id(R.id.spinner_label_layout).clicked(new GeneralOnClickListner());
        aq.id(R.id.before1).clicked(new GeneralOnClickListner());
        aq.id(R.id.repeat_task_lay).clicked(new GeneralOnClickListner());
        aq.id(R.id.grid_text)
                .typeface(
                        TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
                .clicked(new GeneralOnClickListner());
        final View view12 = getActivity().getLayoutInflater().inflate(
                R.layout.landing_menu, null, false);
        aq_menu = new AQuery(getActivity(), view12);
        popupWindowAttach = new PopupWindow(view12, Utils.getDpValue(200,
                getActivity()), WindowManager.LayoutParams.WRAP_CONTENT, true);

        popupWindowAttach.setBackgroundDrawable(getResources().getDrawable(
                android.R.drawable.dialog_holo_light_frame));
        popupWindowAttach.setOutsideTouchable(true);

        LayoutInflater inflater5 = getActivity().getLayoutInflater();

        View dialogLayout6 = inflater5.inflate(R.layout.add_task_edit, null,
                false);
        aq_edit = new AQuery(dialogLayout6);
        AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
        builder6.setView(dialogLayout6);
        label_edit = builder6.create();

        View dialogLayout7 = inflater5.inflate(R.layout.add_task_edit_delete,
                null, false);
        aq_del = new AQuery(dialogLayout7);
        AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
        builder7.setView(dialogLayout7);
        location_del = builder7.create();
        taskTitle = (EditText) aq.id(R.id.task_title1).getView();

        taskTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                aq.id(R.id.completed_task).textColorId(R.color.active);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Date picker implementation
        DatePicker dPicker = (DatePicker) aq.id(R.id.date_picker).getView();
        int density = getResources().getDisplayMetrics().densityDpi;
        showRightDateAndTime();
        dPicker.setMinDate(System.currentTimeMillis() - 1000);
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
        tPicker.setCurrentMinute(currentMin);
        tPicker.setCurrentHour(currentHours);
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

        // ******************* label dialog
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

                labelPosition = position;

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
                    if (labelPosition != -1) {
                        GradientDrawable mDrawable = (GradientDrawable) getResources()
                                .getDrawable(R.drawable.label_background);
                        if (mDrawable != null) {
                            mDrawable.setColor(Color
                                    .parseColor(Constants.label_colors_dialog[labelPosition]));
                        }
                        Save(label_view.getId() + "" + itemPosition, label_text
                                .getText().toString(), labelPosition);
                        label_view.setBackground(mDrawable);
                        ((TextView) label_view).setText(label_text.getText()
                                .toString());
                        ((TextView) label_view).setTextColor(Color.WHITE);

                        aq.id(R.id.spinner_labels_task).text(
                                ((TextView) label_view).getText().toString());
                        aq.id(R.id.spinner_labels_task).getTextView()
                                .setBackground(label_view.getBackground());
                        aq.id(R.id.spinner_labels_task).getTextView()
                                .setTextColor(Color.WHITE);
                        labelColor = Constants.label_colors_dialog[labelPosition];
                        labelPosition = -1;

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
        aq.id(R.id.label_grid_view)
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
                                if (pLabel != null) {
                                    textView.setTextColor(Color.WHITE);
                                    textView.setText(pLabel);
                                    GradientDrawable mDrawable = (GradientDrawable) getResources()
                                            .getDrawable(
                                                    R.drawable.label_background);
                                    if (mDrawable != null) {
                                        mDrawable.setColor(Color
                                                .parseColor(Constants.label_colors_dialog[mPosition]));
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
                        itemPosition = position;
                        label_view = view;
                        if (!((TextView) view).getText().toString()
                                .equalsIgnoreCase("new")) {
                            aq.id(R.id.spinner_labels_task).text(
                                    ((TextView) view).getText().toString());
                            aq.id(R.id.spinner_labels_task).getTextView()
                                    .setBackground(view.getBackground());
                            aq.id(R.id.spinner_labels_task).getTextView()
                                    .setTextColor(Color.WHITE);
//                            GradientDrawable drawable = (GradientDrawable) view.getBackground();
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
                Remove(viewl.getId() + "" + itemPosition);
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

                add_new_label_alert.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                add_new_label_alert.show();

            }
        });

        /**
         * View pager for before and location
         *
         */
        ViewPager pager = (ViewPager) aq.id(R.id.add_task_before_pager)
                .getView();

        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new AddTaskBeforePagerFragment(getActivity()
                .getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) aq.id(
                R.id.add_task_before_tabs_task).getView();
        tabs.setDividerColorResource(android.R.color.transparent);
        tabs.setIndicatorColorResource(R.color._4d4d4d);
        tabs.setUnderlineColorResource(android.R.color.transparent);
        tabs.setTextSize(Utils.getPxFromDp(getActivity(), 16));
        tabs.setIndicatorHeight(Utils.getPxFromDp(getActivity(), 1));
        tabs.setShouldExpand(false);
        tabs.setSmoothScrollingEnabled(true);
        tabs.setAllCaps(false);
        tabs.setTypeface(
                TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE),
                Typeface.NORMAL);
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);

        aq.id(R.id.repeat_grid_view).getGridView()
                .setAdapter(new ArrayAdapter<String>(getActivity(),

                        R.layout.grid_layout_textview, Constants.repeatArray) {

                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {

                        TextView textView = (TextView) super.getView(position,
                                convertView, parent);
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
                    view.setBackgroundResource(R.drawable.round_buttons_blue);
                }

                    if (((TextView) view).getText().toString().equals("Never")) {
                    aq.id(R.id.forever_radio).checked(true);
                    aq.id(R.id.time_radio).textColor(
                            Color.parseColor("#bababa"));
                    aq.id(R.id.forever_radio).textColor(
                            (getResources().getColor(R.color._777777)));
                }
                ((TextView) view).setTextColor(Color.WHITE);
                view.setSelected(true);
                if (Constants.repeatArray[position].equals("Never")) {
                    aq.id(R.id.repeat).text(Constants.repeatArray[position]);
                } else {
                    aq.id(R.id.repeat).text(Constants.repeatArray[position]);
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

        aq.id(R.id.time_radio).textColor(Color.parseColor("#bababa"));
        final TextView set = (TextView) dateTimePickerDialog
                .findViewById(R.id.set);
        set.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogDatePicker.clearFocus();
                set.requestFocus();
                date_time_alert.dismiss();

                aq.id(R.id.repeat).text(
                        ((TextView) previousSelected).getText().toString()
                                + " until " + setMon);
                RadioButton rb = (RadioButton) aq.id(R.id.time_radio).getView();
                rb.setText(setMon);
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
        aq.id(R.id.forever_radio).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!aq.id(R.id.repeat).getText().toString().equals("Never")) {
                    aq.id(R.id.repeat).text(
                            ((TextView) previousSelected).getText().toString());
                }

                aq.id(R.id.time_radio).textColor(Color.parseColor("#bababa"));
                aq.id(R.id.forever_radio).textColor(
                        getResources().getColor(R.color._777777));
            }
        });

        aq.id(R.id.time_radio).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (aq.id(R.id.repeat).getText().toString().equals("Never")) {
                    Toast.makeText(getActivity(), "Please Select ...",
                            Toast.LENGTH_SHORT).show();
                    aq.id(R.id.time_radio).checked(false);
                    aq.id(R.id.forever_radio).checked(true);
                    aq.id(R.id.time_radio).textColor(
                            Color.parseColor("#bababa"));
                    aq.id(R.id.forever_radio).textColor(
                            getResources().getColor(R.color._777777));
                } else {

                    aq.id(R.id.time_radio).textColor(
                            getResources().getColor(R.color._777777));
                    aq.id(R.id.forever_radio).textColor(
                            Color.parseColor("#bababa"));
                    date_time_alert.show();
                }
            }
        });
        // ***************************** Attachment
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View attachment = inflater
                .inflate(R.layout.add_attachment, null, false);
        AQuery att = new AQuery(attachment);

        LinearLayout ll = (LinearLayout) aq.id(R.id.added_image_outer)
                .getView();
        ll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                LinearLayout lll = (LinearLayout) arg0;
                Toast.makeText(getActivity(), lll.getChildCount() + "",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Gallery and Camera intent
        att.id(R.id.gallery1)
                .typeface(
                        TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
                .clicked(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        attach.dismiss();
                        Intent galleryIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_GALLERY);
                    }
                });
        att.id(R.id.camera1)
                .typeface(
                        TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
                .clicked(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        attach.dismiss();
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
        // DropBox and Google Drive intent
        att.id(R.id.add_attachment_dropbox)
                .typeface(
                        TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
                .clicked(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attach.dismiss();
                        Intent dropboxIntent = new Intent(Intent.ACTION_GET_CONTENT );
                        dropboxIntent.setType("*/*");
                        startActivityForResult(Intent
                                .createChooser(dropboxIntent
                                        .setPackage("com.dropbox.android"), "DropBox"), RESULT_DROPBOX);

                    }
                });

        att.id(R.id.add_attachment_google)
                .typeface(TypeFaces.get(getActivity(), Constants.ROMAN_TYPEFACE))
                .clicked(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attach.dismiss();
                        Intent dropboxIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        dropboxIntent.setType("*/*");
                        startActivityForResult(Intent
                                .createChooser(dropboxIntent
                                        .setPackage("com.google.android.apps.docs"), "Google Drive"), RESULT_GOOGLEDRIVE);

                    }
                });
        AlertDialog.Builder attach_builder = new AlertDialog.Builder(
                getActivity());
        attach_builder.setView(attachment);
        attach = attach_builder.create();

        aq.id(R.id.task_attachment).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                attach.show();
            }
        });

        // ***************************** Priority
        lastCheckedId = ((RadioGroup) aq.id(R.id.priority_radio_buttons)
                .getView()).getCheckedRadioButtonId();
        ((RadioGroup) aq.id(R.id.priority_radio_buttons).getView())
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        ((RadioButton) group.findViewById(lastCheckedId))
                                .setTextColor(getResources().getColor(
                                        R.color.deep_sky_blue));
                        ((RadioButton) group.findViewById(checkedId))
                                .setTextColor(getResources().getColor(
                                        android.R.color.white));
                        String abc = ((RadioButton) group
                                .findViewById(checkedId)).getText().toString();
                        switch (abc) {
                            case "None":
                                AddTask.priority = 0;
                                break;
                            case "!":
                                AddTask.priority = 1;
                                break;
                            case "! !":
                                AddTask.priority = 2;
                                break;
                            case "! ! !":
                                AddTask.priority = 3;
                                break;
                        }
                        lastCheckedId = checkedId;
                    }
                });

        View switchView = aq.id(R.id.add_sub_task).getView();
        toggleCheckList(switchView);

    }

    private void toggleCheckList(View switchView) {
        View newView;
        try {
            // Getting instance
            ChecklistManager mChecklistManager = ChecklistManager
                    .getInstance(getActivity());

            mChecklistManager.setNewEntryHint("Add a subtask");
            mChecklistManager.setMoveCheckedOnBottom(1);
            mChecklistManager
                    .setCheckListChangedListener(new CheckListChangedListener() {

                        @Override
                        public void onCheckListChanged() {

                        }
                    });

            mChecklistManager.setKeepChecked(true);
            mChecklistManager.setShowChecks(true);
            newView = mChecklistManager.convert(switchView);
            mChecklistManager.replaceViews(switchView, newView);
            switchView = newView;

        } catch (ViewNotSupportedException e) {
            // This exception is fired if the source view class is
            // not supported
            e.printStackTrace();
        }
    }

    private void showRightDateAndTimeForDialog() {
        String fff = String.valueOf(currentDayDigit).replace("th", "");
        setMon = fff + " " + currentMon + " " + currentYear;
        repeatDate = currentYear + "-" + (currentMonDigit + 1) + "-"
                + currentDayDigit + " 00:00:00";
    }

    private void setAllOtherFocusableFalse(View v) {
        for (int id : allViews)
            if (v.getId() != id) {
                try {
                    aq.id(id).getView().setFocusableInTouchMode(false);
                } catch (Exception e) {

                }
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
                if(null != data){
                    showImageURI(data.getData());
                }
                break;
            case RESULT_GOOGLEDRIVE:
                if( null != data){
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

    private void showImageURI(final Uri selectedImage) {

        if(selectedImage == null){
            Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
            return;
        }

        getActivity().getContentResolver().notifyChange(selectedImage, null);
        ContentResolver cr = getActivity().getContentResolver();

        Bitmap bitmap;
        if (FragmentCheck == 0) {
            try {
                bitmap = Utils.getBitmap(selectedImage, getActivity(), cr);
                final LinearLayout item = (LinearLayout) aq
                        .id(R.id.added_image_outer).visible().getView();

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
                        ll_linear = (LinearLayout) item.findViewById(Integer
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
                                if (ll_linear != null){
                                    item.removeView(ll_linear);
                                    Attach attach = new Attach();
                                    attach.setAttach_path(selectedImage.getPath());
                                    attach.setAttach_type("image");
                                    attachArrayList.remove(attach);
                                }
                                popupWindowAttach.dismiss();
                            }
                        });

                aq.id(image).image(Utils.getRoundedCornerBitmap(bitmap, 8));
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

                Attach attachObj = new Attach();
                attachObj.setAttach_path(selectedImage.getPath());
                attachObj.setAttach_type("image");
                attachArrayList.add(attachObj);
            } catch (Exception e) {
                e.printStackTrace();
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

        aq.id(R.id.calender_layout).background(R.drawable.input_fields_gray);
        aq.id(R.id.before_layout).background(R.drawable.input_fields_gray);
        aq.id(R.id.repeat_layout).background(R.drawable.input_fields_gray);
        aq.id(R.id.spinner_label_layout).background(
                R.drawable.input_fields_gray);
    }


    private void showCurrentView(View v) {

        hideAll();

        switch (v.getId()) {
            case R.id.time_date:
                if (aq.id(R.id.date_time_include).getView().getVisibility() == View.GONE) {
                    aq.id(R.id.date_time_include)
                            .getView()
                            .startAnimation(
                                    new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
                                            200, aq.id(R.id.date_time_include)
                                            .getView(), true));
                    aq.id(R.id.calender_layout).background(
                            R.drawable.input_fields_blue);
                }
                break;
            case R.id.before1:

                if (aq.id(R.id.before_grid_view_linear).getView().getVisibility() == View.GONE) {
                    if (!aq.id(R.id.before).getText().toString().isEmpty()) {
                        aq.id(R.id.before).text(Constants.beforeArray[1] + " Before").visibility(View.VISIBLE);
                    }
                    aq.id(R.id.before_grid_view_linear)
                            .getView()
                            .startAnimation(
                                    new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
                                            200,
                                            aq.id(R.id.before_grid_view_linear)
                                                    .getView(), true));

                    aq.id(R.id.before_layout).background(
                            R.drawable.input_fields_blue);
                }

                break;
            case R.id.repeat_task_lay:
                if (aq.id(R.id.repeat_linear_layout).getView().getVisibility() == View.GONE) {
                    if (aq.id(R.id.repeat).getText().toString() == "") {
                        aq.id(R.id.repeat).text(Constants.repeatArray[2])
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



    public void Save(String id, String name, int label_position) {
        // 0 - for private mode
        editor.putString(1 + "key_label" + id, name); // Storing integer
        editor.putInt(1 + "key_color_position" + id, label_position); // Storing
        // float
        editor.commit();
    }

    public void Load(String id) {
        pLabel = null;
        pLabel = App.label.getString(1 + "key_label" + id, null); // getting
        // String
        Log.v("View id= ", id + "| " + pLabel + " | " + mPosition);

        mPosition = App.label.getInt(1 + "key_color_position" + id, 0); // getting
        // String
    }

    public void Remove(String id) {
        editor.remove(1 + "key_label" + id); // will delete key name
        editor.remove(1 + "key_color_position" + id); // will delete key email
        editor.commit();
    }

    public void SaveAttach(int id, String path, String type) {
        // 0 - for private mode
        editorAttach.putInt("Max", id);
        editorAttach.putString(1 + "path" + id, path);
        editorAttach.putString(1 + "type" + id, type); // Storing float
        editorAttach.commit();
    }

    public void LoadAttachmax() {
        MaxId = App.attach.getInt("Max", 0);
    }
    private void uploadAttachments(AQuery aq, byte[] byteArray) {

        HttpEntity entity = null;
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("image", encoded));
        final CircularProgressButton progressButton = (CircularProgressButton) aq.id(R.id.task_attachment).getView();
        progressButton.setIndeterminateProgressMode(true);
        progressButton.setProgress(10);
        isAttaching = true;

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
                            boolean error = obj1.getBoolean("error");
                            if(error) {
                                Toast.makeText(getActivity(), "Error uploading attachment.", Toast.LENGTH_SHORT).show();
                                progressButton.setProgress(-1);
                            } else {
                                LoadAttachmax();
                                if (MaxId == 0) {
                                    MaxId = 1;
                                } else {
                                    MaxId = MaxId + 1;
                                }
                                SaveAttach(MaxId, path, "type");
                                progressButton.setProgress(0);
                            }
                            isAttaching = false;
                        } catch (Exception e) {

                        }

                    }
                });
    }

    private void dragAndDrop(){

        final LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.popup_menu, null, false);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView ok = (TextView) view.findViewById(R.id.ok);

        TextView tx = (TextView) view.findViewById(R.id.show_hid_text);
        tx.setTypeface(TypeFaces.get(getActivity(), Constants.MED_TYPEFACE));

        popupWindowTask = new PopupWindow(view, Utils.getDpValue(270, getActivity()),
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindowTask.setBackgroundDrawable(new BitmapDrawable());
        popupWindowTask.setOutsideTouchable(true);

        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindowTask.dismiss();
            }
        });
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindowTask.dismiss();
            }
        });
        ArrayList<String> arrayList = new ArrayList<>(
                Arrays.asList(Constants.layoutsName));
        DragSortListView listViewTask = (DragSortListView) view.findViewById(R.id.list);
        adapterTask = new ArrayAdapter<>(getActivity(),
                R.layout.popup_menu_items, R.id.text, arrayList);

        listViewTask.setAdapter(adapterTask);
        listViewTask.setDropListener(onDropTask);
        for (int i = 0; i < Constants.layoutsName.length; i++){
            listViewTask.setItemChecked(i, App.prefs.getTaskLayout(Constants.layoutsName[i]));
        }


        DragSortController controllerTask = new DragSortController(listViewTask);
        controllerTask.setDragHandleId(R.id.drag_handle);

        listViewTask.setOnTouchListener(controllerTask);
        listViewTask.setOnItemClickListener(new ListClickListenerTask());
    }

    private void saveTask(){
        if(isAttaching){
            Toast.makeText(getActivity(), "Please wait...uploading attachment.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (aq.id(R.id.task_title1).getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter title",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        assignedId.clear();
        if(!TextUtils.isEmpty(assignedSelectedID))
            assignedId.add(assignedSelectedID);

        MaxId = App.attach.getInt("Max", 0);
        title = aq.id(R.id.task_title1).getText().toString();

        boolean isAllDay = false;
        String start_date = currentYear + "-"
                + (currentMonDigit + 1) + "-"
                + currentDayDigit + " "
                + currentHours + ":"
                + currentMin +":00";

        String location = aq.id(R.id.location_task).getText().toString();
        boolean is_time = true, is_location = false;
        String r_location = "", location_tag = "";
        String before = aq.id(R.id.before).getText().toString();
        if (before.contains("On Arrive") || before.contains("On Leave")) {
            is_time = false;
            is_location = true;
            r_location = aq.id(R.id.location_before).getText()
                    .toString();
            location_tag = ((TextView) AddTaskBeforeFragment.viewP)
                    .getText().toString();
        }

        boolean is_alertEmail = false, is_alertNotification = false;
        if (!aq.id(R.id.before).getText().toString().isEmpty()) {
            try{
                is_alertEmail = aq.id(R.id.email_radio).getCheckBox()
                        .isChecked();
                is_alertNotification = aq.id(R.id.notification_radio)
                        .getCheckBox().isChecked();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        boolean repeat_forever = aq.id(R.id.forever_radio).isChecked();
        String repeat = aq.id(R.id.repeat).getText().toString();

        String label_name = aq.id(R.id.spinner_labels_task).getText()
                .toString();
        if(label_name.equals("Label"))
            label_name = "";

        if(!(aq.id(R.id.add_sub_task).getView() instanceof EditText))
            toggleCheckList(aq.id(R.id.add_sub_task).getView());
        String checklist_data = aq.id(R.id.add_sub_task).getEditText()
                .getText().toString();

        String notes = aq.id(R.id.notes_task).getText().toString();

        Date startDate;
        long startDateInMilli = 0, endDateInMilli = 0;
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = sdf1.parse(start_date);
            startDateInMilli = startDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int reminderTime = 0, is_locationtype = 0;
        String locationType = "";
        Log.e("before", before);
        if (!before.isEmpty()) {
            if (is_time) {
                reminderTime = Utils.getReminderTime(before);
                Log.e("reminder time", reminderTime+"");
            } else {
                if (before.contains("On Arrive")) {
                    is_locationtype = 0;
                    locationType = "On Arrive";
                } else if (before.contains("On Leave")) {
                    is_locationtype = 1;
                    locationType = "On Leave";
                }
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
        if(isProjectSubTask){
            SubTask subTask = new SubTask();
            subTask.title = title;
            subTask.startDate = start_date;
            subTask.endDate = ""; // nothing required
            subTask.location = location;
            subTask.isLocationBased = is_location;
            subTask.reminderLocation = r_location;
            subTask.reminderTime = reminderTime;
            subTask.repeatInterval = repeat;
            subTask.repeatTime = r_repeat;
            subTask.labelName = label_name;
            subTask.labelColor = labelColor;
            subTask.subTask = checklist_data;
            subTask.comments = AddTaskComment.comment;
            AddProjectFragment.projectsSubTaskArray.add(subTask);
            getActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        todo = new ToDo();
        todo.setUser_id(App.prefs.getUserId());
        todo.setTodo_type_id(1);
        todo.setTitle(title);
        todo.setStart_date(startDateInMilli);
        todo.setEnd_date(endDateInMilli);
        todo.setIs_allday(isAllDay);
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
        reminder.setShowable_format(before);
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
        repeaT.setShowable_format(repeat);
        repeatdao.insert(repeaT);
        todo.setRepeat(repeaT);

        CheckList checklist = new CheckList();
        checklist.setTitle(checklist_data);
        checklistdao.insert(checklist);
        todo.setCheckList(checklist);

//        if (AddTaskComment.comment != null && AddTaskComment.comment.size() > 0) {
//            for (int i = 0; i < AddTaskComment.comment.size(); i++) {
//                Comment commenT = new Comment();
//                commenT.setComment(AddTaskComment.comment.get(i));
//                commentdao.insert(commenT);
//                commenT.setToDo(todo);
//            }
//        }

        alarm = new AlarmManagerBroadcastReceiver();
        geoFence = new Geofences(getActivity());

        if(isEditMode){
            todo.setId(todoId);
            tododao.update(todo);
        }else {
            // ********************* Data add hit Async task ******************//
            AddToServer aSync = new AddToServer(title, 1, start_date, "", isAllDay, is_location, r_location, location_tag,
                    locationType, location, notes, repeatDate, repeat_forever, MaxId,
                    AddTaskComment.commment, AddTaskComment.commenttime, checklist_data, assignedId, repeat, reminderTime, label_name, "", before, "", AddTaskFragment.this);
            aSync.execute();
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void db_initialize() {
        checklistdao = App.daoSession.getCheckListDao();
        labeldao = App.daoSession.getLabelDao();
        tododao = App.daoSession.getToDoDao();
        repeatdao = App.daoSession.getRepeatDao();
        reminderdao = App.daoSession.getReminderDao();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.Project_task_check = 0;
    }

    private void populateValues() {

        todo = tododao.load(todoId);
        aq.id(R.id.task_title1).text(todo.getTitle());
        long startDate = todo.getStart_date();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startDate);
        currentYear = cal.get(Calendar.YEAR);
        currentMonDigit = cal.get(Calendar.MONTH) + 1;
        currentDayDigit = cal.get(Calendar.DAY_OF_MONTH);
        currentHours = cal.get(Calendar.HOUR_OF_DAY);
        currentMin = cal.get(Calendar.MINUTE);
        showRightDateAndTime();
        aq.id(R.id.location_task).text(todo.getLocation());

        if(todo.getReminder().getShowable_format() != null){
            aq.id(R.id.before).text(todo.getReminder().getShowable_format()).visible();
        }

        if(todo.getRepeat().getShowable_format() != null) {
            int selectedPosition = 0;
            for (int i = 0; i < Constants.repeatArray.length; i++) {
                if (Constants.repeatArray[i].equalsIgnoreCase(todo.getRepeat().getShowable_format())) {
                    selectedPosition = i;
                    break;
                }
            }
            aq.id(R.id.repeat_grid_view).getGridView().setItemChecked(selectedPosition, true);
            aq.id(R.id.repeat).text(todo.getRepeat().getShowable_format()).visible();
        }

        if(todo.getLabel().getLabel_color() != null) {
            aq.id(R.id.spinner_labels_task).text(todo.getLabel().getLabel_name());
            GradientDrawable mDrawable = (GradientDrawable) getResources()
                    .getDrawable(R.drawable.label_background);
            if ((mDrawable != null)  &&(!todo.getLabel().getLabel_color().isEmpty())){
                mDrawable.setColor(Color.parseColor(todo.getLabel().getLabel_color()));
            }

            aq.id(R.id.spinner_labels_task).getView().setBackground(mDrawable);
            aq.id(R.id.spinner_labels_task).getTextView().setTextColor(Color.WHITE);
        }
        toggleCheckList(aq.id(R.id.add_sub_task).getView());
        aq.id(R.id.add_sub_task).text(todo.getCheckList().getTitle());
        toggleCheckList(aq.id(R.id.add_sub_task).getView());

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

    private void showRightDateAndTime() {
        String tempCurrentDayDigit = String.format("%02d", currentDayDigit);
        String tempCurrentHours = String.format("%02d", currentHours);
        String tempCurrentMins = String.format("%02d", currentMin);
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

        if(isEditMode){
            aq.id(R.id.day_field).text(currentDay);
            aq.id(R.id.month_year_field).text(
                    tempCurrentDayDigit
                            + Utils.getDayOfMonthSuffix(currentDayDigit) + " "
                            + currentMon);
        }
        aq.id(R.id.time_field).text(tempCurrentHours + ":" + tempCurrentMins);

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

        App.updateTaskList(MainActivity.act);

        if (!todo.getReminder().getLocation().isEmpty())
            addGeofence(todo);

        setAlarm(); //set alarm accordingly
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
                    .id(R.id.added_image_outer).visible().getView();

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
                    ll_linear = (LinearLayout) item.findViewById(Integer
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
                            if (ll_linear != null){
                                item.removeView(ll_linear);
                                attachArrayList.remove(attach);
                            }
                            popupWindowAttach.dismiss();
                        }
                    });

            ImageOptions options = new ImageOptions();
            options.round = 20;

            AQuery aq = new AQuery(child);

            final int tint = 0x77AA0000;

            aq.id(image).image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback(){

                @Override
                public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
                    iv.setImageBitmap(bm);
                    //do something to the bitmap
                    iv.setColorFilter(tint, PorterDuff.Mode.SRC_ATOP);
                    bm.getByteCount();
                    TextView size = (TextView) child
                            .findViewById(R.id.image_added_size);
                    size.setText("( "+bm.getByteCount() / 1024 + " KB)");

                }

            });
            aq.id(image).image(imageUrl, options);
            child.findViewById(R.id.image_menu);
            TextView by = (TextView) child
                    .findViewById(R.id.image_added_by);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            by.setText("By " + App.prefs.getUserName()+" on " + sdf.format(cal.getTime()));
            item.addView(child);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GeneralOnClickListner implements OnClickListener {

        @Override
        public void onClick(View v) {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            showCurrentView(v);
            setAllOtherFocusableFalse(v);
            if (v.getId() == R.id.location_before
                    || v.getId() == R.id.task_title1
                    || v.getId() == R.id.location_task)
                Utils.showKeyboard(getActivity());
            else
                Utils.hidKeyboard(getActivity());
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
                    return "By time";
                case 1:
                    return "At location";
                default:
                    return "";// not the case

            }
        }

        @Override
        public Fragment getItem(int position) {

            return AddTaskBeforeFragment.newInstance(position, isEditMode, todo != null ? todo.getReminder().getShowable_format() : "");
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
                itemPosition = position;
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

    public class ListClickListenerTask implements OnItemClickListener {

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
                App.prefs.setTaskLayout(Constants.addTaskLayouts[position], true);
            } else {
                aq.id(layoutId).gone();
                App.prefs.setTaskLayout(Constants.addTaskLayouts[position], false);
            }

        }

    }
}