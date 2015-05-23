package com.vector.onetodo;

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
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.devspark.appmsg.AppMsg;
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

public class AddProjectFragment extends Fragment {

    // event_attachment
    private AQuery AQlabel, AQlabel_edit,
            AQlabel_del;
    public static AQuery aq;
    private View label_view;
    private int Label_postion = -1;
    private ImageView last;
    private String plabel = null;
    private int pposition = -1;
    private int itempos = -1;
    public static EditText taskTitle;
    public static HashMap<Integer, Integer> inflatingLayouts = new HashMap<Integer, Integer>();
    private String currentDay, currentMon;
    private Uri imageUri;
    public static final int RESULT_GALLERY = 0;
    public static final int PICK_CONTACT = 2;
    private static final int TAKE_PICTURE = 1;
    private AlertDialog add_new_label_alert, label_edit;
    public static int currentHours, currentMin, currentDayDigit, currentYear,
            currentMonDigit;
    private int[] collapsingViews = {R.id.date_time_include,
            R.id.label_project_grid_view};

    private int[] allViews = {R.id.time_date, R.id.spinner_label_layout};
    public static ArrayList<SubTask> projectsSubTaskArray = new ArrayList<>();
    private SubTaskAdapter subTaskAdapter;

    int dayPosition;
    Editor editor;

    protected static final int RESULT_CODE = 123;

    public static View allView;

    public static Activity act;
    private AlertDialog location_del;

    private ToDoDao tododao;
    private LabelDao labeldao;
    private CommentDao commentdao;
    private RepeatDao repeatDao;
    private ReminderDao reminderDao;
    private ArrayList<String> assignedId = new ArrayList<>();
    private CheckListDao checklistDao;


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
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
        if (toolbar != null)
            ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        initActionBar();
        return view;
    }

    private void initActionBar() {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Project");
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.todo_menu_project, menu);
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
            case R.id.action_accept_project:
                saveProject();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allView = getView();
        editor = App.label.edit();
        projectsSubTaskArray.clear();
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
        inflateLayouts();
        main();
    }

    public void main() {

        taskTitle = (EditText) aq.id(R.id.project_title).getView();

        taskTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
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

        aq.id(R.id.invite_project).clicked(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in1,
                        R.anim.slide_out1);
                transaction.replace(R.id.content, AddTaskAssign.newInstance(5));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

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
                                Constants.labels_array_task) {

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

        View dialogLayout7 = inflater5.inflate(R.layout.add_task_edit_delete,
                null, false);
        AQlabel_del = new AQuery(dialogLayout7);
        AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
        builder7.setView(dialogLayout7);
        location_del = builder7.create();
        aq.id(R.id.spinner_label_layout).clicked(new GeneralOnClickListner());

        // ********************************* Label END

        // subtask adapter

        subTaskAdapter = new SubTaskAdapter(getActivity());
        aq.id(R.id.subtask_listview).getListView().setAdapter(subTaskAdapter);
        View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.add_project_note_footer, null, false);
        ((TextView)footerView.findViewById(R.id.project_subtask)).setText("Add Task");
        footerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fr = new AddTaskFragment();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                Bundle b = new Bundle();
                b.putInt("dayPosition", dayPosition);
                b.putBoolean("is_sub_task", true);
                fr.setArguments(b);
                trans.add(R.id.content, fr);
                trans.addToBackStack("ADD_PROJECT");
                trans.commit();
            }
        });
        aq.id(R.id.subtask_listview).getListView().addFooterView(footerView);
        aq.id(R.id.subtask_listview).itemClicked(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });

    }

    public static void updateAssign(HashMap<String, String> names){
        StringBuilder builder = new StringBuilder();
        for(String key : names.keySet()){
            builder.append(names.get(key)+", ");
        }
        aq.id(R.id.event_assign).text(builder);
    }



    private void inflateLayouts() {
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

    private void addNewSubTaskLayout(int taskCount) {
        GridLayout gridLayout = (GridLayout) allView
                .findViewById(R.id.inner_event_container);
        View child = act.getLayoutInflater().inflate(
                R.layout.add_project_note, null);
        AQuery aq = new AQuery(child);
        aq.id(R.id.sub_task_heading).gone();
        child.setTag(taskCount + "_task");
        gridLayout.addView(child);
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
                if (aq.id(R.id.date_time_include).getView().getVisibility() == View.GONE) {
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
                if (aq.id(R.id.label_project_grid_view).getView().getVisibility() == View.GONE) {
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

    public class SubTaskAdapter extends BaseSwipeAdapter {
        private Context mContext;

        public SubTaskAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return projectsSubTaskArray.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }
        @Override
        public View generateView(int position, ViewGroup parent) {
            return LayoutInflater.from(mContext).inflate(R.layout.add_project_note, null);
        }

        @Override
        public void fillValues(int position, View convertView) {

            final int mPosition = position;
            TextView t = (TextView) convertView.findViewById(R.id.project_subtask);
            t.setText(projectsSubTaskArray.get(position).title);

            convertView.findViewById(R.id.trash).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    projectsSubTaskArray.remove(mPosition);
                    subTaskAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void saveProject() {

        assignedId.clear();
        if (!(aq.id(R.id.project_title).getText().toString().equals(""))) {

//            MaxId = App.attach.getInt("4Max", 0);
            String title = aq.id(R.id.project_title).getText().toString();

            boolean is_allday = false;

            String start_date = currentYear + "-"
                    + (currentMonDigit + 1) + "-"
                    + currentDayDigit + " "
                    + currentHours + ":"
                    + currentMin + ":00";
            String end_date = "";
//            endEventYear + "-"
//                    + (endEventMonDigit + 1) + "-"
//                    + endEventDayDigit + " "
//                    + endEventHours + ":"
//                    + endEventMin + ":00";


            String label_name = aq.id(R.id.spinner_labels_project).getText()
                    .toString();

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

            for (String key : AssignMultipleFragment.selectedInvitees.keySet())
                assignedId.add(key);

            db_initialize();
            AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();

            ToDo todo = new ToDo();
            todo.setUser_id(Constants.user_id);
            todo.setTodo_type_id(5);
            todo.setTitle(title);
            todo.setParent(0);
            todo.setStart_date(startDateInMilli);
            todo.setEnd_date(endDateInMilli);
            todo.setIs_allday(is_allday);
            todo.setIs_done(false);
            todo.setIs_delete(false);

            Label label = new Label();
            label.setLabel_name(label_name);
            labeldao.insert(label);
            todo.setLabel(label);

            long todo_id = tododao.insert(todo);
            alarm.SetNormalAlarm(getActivity());

            for(int i = 0; i < projectsSubTaskArray.size(); i++){
                ToDo todoSubTask = new ToDo();
                todoSubTask.setUser_id(Constants.user_id);
                todoSubTask.setTodo_type_id(1);
                todoSubTask.setTitle(projectsSubTaskArray.get(i).title);
                todoSubTask.setParent((int) todo_id);
                long subTaskStartDate = 0;
                try {
                    startDate = sdf1.parse(projectsSubTaskArray.get(i).startDate);
                    subTaskStartDate = startDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                todoSubTask.setStart_date(subTaskStartDate);
                todoSubTask.setEnd_date(0l);
                todoSubTask.setIs_allday(false);
                todoSubTask.setIs_done(false);
                todoSubTask.setIs_delete(false);

                Label labelSubTask = new Label();
                labelSubTask.setLabel_name(label_name);
                labeldao.insert(labelSubTask);

                Reminder reminder = new Reminder();
                reminder.setIs_alertEmail(false);
                reminder.setIs_alertNotification(false);
                reminder.setIs_time_location(projectsSubTaskArray.get(i).isLocationBased);
                reminder.setLocation(projectsSubTaskArray.get(i).reminderLocation);
                reminder.setLocation_type(0);
                if ((!projectsSubTaskArray.get(i).location.equals("New")) && projectsSubTaskArray.get(i).location != null) {
                    reminder.setLocation_tag(projectsSubTaskArray.get(i).location);
                }

                reminder.setTime((long) projectsSubTaskArray.get(i).reminderTime);
                reminderDao.insert(reminder);
                todo.setReminder(reminder);

                Repeat repeaT = new Repeat();
                repeaT.setRepeat_interval(projectsSubTaskArray.get(i).repeatInterval);
                repeaT.setRepeat_until(0l);
                repeaT.setIs_forever(false);
                repeatDao.insert(repeaT);
                todo.setRepeat(repeaT);

                CheckList checklist = new CheckList();
                checklist.setTitle(projectsSubTaskArray.get(i).subTask);
                checklistDao.insert(checklist);
                todo.setCheckList(checklist);

                tododao.insert(todoSubTask);

                if(projectsSubTaskArray.get(i).reminderTime != 0){
                    alarm.setReminderAlarm(getActivity(), startDateInMilli - projectsSubTaskArray.get(i).reminderTime, projectsSubTaskArray.get(i).title, projectsSubTaskArray.get(i).location);
                    alarm.SetNormalAlarm(getActivity());
                }
                if(projectsSubTaskArray.get(i).repeatTime != 0){
                    alarm.setRepeatAlarm(getActivity(), projectsSubTaskArray.get(i).repeatTime);
                }
                else{
                    alarm.SetNormalAlarm(getActivity());
                }
            }

            TaskListFragment.setAdapter(getActivity(), TaskListFragment.position, null);

            // ********************* Data add hit Asyntask

            AddToServerProject asyn = new AddToServerProject(title, start_date, end_date, assignedId, label_name, "");
            asyn.execute();

            getActivity().getSupportFragmentManager().popBackStack();

        } else
            Toast.makeText(getActivity(), "Please enter title",
                    Toast.LENGTH_SHORT).show();
    }

    void Save(String id, String name, int label_position) {
        // 0 - for private mode
        editor.putString(5 + "key_label" + id, name); // Storing integer
        editor.putInt(5 + "key_color_position" + id, label_position); // Storing
        // float
        editor.commit();
    }

    void Load(String id) {
        plabel = null;
        plabel = App.label.getString(5 + "key_label" + id, null); // getting
        // String
        Log.v("View id= ", id + "| " + plabel + " | " + pposition);

        pposition = App.label.getInt(5 + "key_color_position" + id, 0); // getting
        // String
    }

    private void db_initialize() {
        labeldao = App.daoSession.getLabelDao();
        tododao = App.daoSession.getToDoDao();
        commentdao = App.daoSession.getCommentDao();
        reminderDao = App.daoSession.getReminderDao();
        repeatDao = App.daoSession.getRepeatDao();
        checklistDao = App.daoSession.getCheckListDao();
    }

}
