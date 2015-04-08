package com.vector.onetodo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.vector.model.AssignedTaskData;
import com.vector.model.ItemDetails;
import com.vector.model.TaskData;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Utils;

import net.appkraft.parallax.ParallaxScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;

public class TaskView extends BaseActivity {

    AQuery aq, aqd, aq_menu;
    AlertDialog alert;
    ParallaxScrollView parallax;
    ImageView image;
    private PopupWindow popupWindowTask;
    long todoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtask);
        parallax = (ParallaxScrollView) findViewById(R.id.scrollView123);
        image = (ImageView) findViewById(R.id.imageView1123);
        aq = new AQuery(this);
        init();
    }

    private void init() {
        if(getIntent().getBooleanExtra("is_assigned_task", false)){
            populateAssignedTaskData();
        }else{
            todoId = getIntent().getLongExtra("todo_id", -1);
            ShowTaskViewData(todoId);
        }

        final View view1 = getLayoutInflater().inflate(
                R.layout.landing_menu, null, false);
        aq_menu = new AQuery(this, view1);
        aq_menu.id(R.id.menu_item1).text("RSVP");
        aq_menu.id(R.id.menu_item2).visibility(View.GONE);
        aq_menu.id(R.id.menu_item3).visibility(View.GONE);
        popupWindowTask = new PopupWindow(view1, Utils.getDpValue(100,
                this), WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindowTask.setBackgroundDrawable(getResources().getDrawable(
                android.R.drawable.dialog_holo_light_frame));
        popupWindowTask.setOutsideTouchable(true);

        View dialog = getLayoutInflater().inflate(R.layout.rsvp,
                null, false);
        aqd = new AQuery(dialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog);
        alert = builder.create();

        aqd.id(R.id.cancel).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alert.dismiss();
            }
        });
        aqd.id(R.id.ok).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alert.dismiss();
            }
        });

        aq_menu.id(R.id.menu_item1).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindowTask.dismiss();
                alert.show();
            }
        });
        aq.id(R.id.backview).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TaskView.this.finish();
            }
        });
        aq.id(R.id.imageView4).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (popupWindowTask.isShowing())
                    popupWindowTask.dismiss();
                else {
                    popupWindowTask.showAsDropDown(aq.id(R.id.imageView4)
                            .getView(), 5, 5);
                }
            }
        });
        parallax.setImageViewToParallax(image);

        aq.id(R.id.btn_edit).clicked(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDoDao toDoDao = App.daoSession.getToDoDao();
                ToDo obj = toDoDao.load(todoId);
                switch (obj.getTodo_type_id()){
                    case 1:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.container, AddTaskFragment.newInstance(0, true, todoId))
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.container, AddEventFragment.newInstance(0, true, todoId))
                                .commit();
                        break;
                }
            }
        });

        aq.id(R.id.backview).clicked(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskView.this.finish();
            }
        });

    }
    static int p;
    private static ArrayList<ItemDetails> GetSearchResults() {
        ArrayList<ItemDetails> results = new ArrayList<>();
        for (p = 0; p < 8; p++) {
            ItemDetails item_details = new ItemDetails();
            item_details.setName("ok");
            item_details.setImage("ok");
            results.add(item_details);
        }
        return results;
    }
    private void ShowTaskViewData(long id){
//        ArrayList<ItemDetails> items = GetSearchResults();
//        aq.id(R.id.invitee_list).adapter(new InviteeAdapter(this, items));
        ToDoDao toDoDao = App.daoSession.getToDoDao();
        ToDo obj = toDoDao.load(id);
        int serverTaskPosition = -1;
        Log.e("size", TaskData.getInstance().result.todos.size()+"");
        for(int i = 0; i < TaskData.getInstance().result.todos.size(); i++){
            try {
                if (obj.getTodo_server_id() == Integer.parseInt(TaskData.getInstance().result.todos.get(i).id)) {
                    serverTaskPosition = i;
                    break;
                }

            }catch (NullPointerException e){e.printStackTrace();}
        }

        try{
            for(int i = 0; i < TaskData.getInstance().result.todos.get(serverTaskPosition).todo_attachment.size(); i++)
                showAttachments(TaskData.getInstance().result.todos.get(serverTaskPosition).todo_attachment.get(i));
        }catch (Exception npe){
            npe.printStackTrace();
        }

        if(obj.getTodo_type_id() == 2){
            aq.id(R.id.imageView1123).image(R.drawable.view_event);
        }
        aq.id(R.id.title).text(obj.getTitle());
        try{
            if(!obj.getRepeat().getRepeat_interval().isEmpty()){
                aq.id(R.id.repeat).text(obj.getRepeat().getRepeat_interval()).visible();
                aq.id(R.id.repeat_layout).visible();
            }
        }catch (NullPointerException npe){
            //no need to do anything
        }
        String strDate = String.valueOf(obj.getStart_date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(strDate));
        strDate = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
                Locale.US)
                + " "
                + calendar.get(Calendar.DAY_OF_MONTH)
                + " "
                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                Locale.US) + " " + calendar.get(Calendar.YEAR);
        aq.id(R.id.time).text(strDate);
        try{
            if(!obj.getLocation().isEmpty()) {
                aq.id(R.id.location).text(obj.getLocation());
                aq.id(R.id.location_layout).visible();
            }
        }catch (NullPointerException npe){}
        try{
            if(obj.getReminder().getTime() == 0) {
                aq.id(R.id.reminder).text("Remind on time");
            }else{
                aq.id(R.id.reminder).text("Remind before " + Integer.parseInt(obj.getReminder().getTime().toString()) / 60000 + " mins");
            }
            aq.id(R.id.reminder_layout).visible();
        }catch (NullPointerException npe){}
        try{
            if(!obj.getRepeat().getRepeat_interval().isEmpty() ) {
                aq.id(R.id.repeat_view).text(obj.getRepeat().getRepeat_interval());
                aq.id(R.id.repeat_layout).visible();
            }
        }catch (NullPointerException npe){}
        try {
            if (!obj.getCheckList().getTitle().isEmpty()){
                aq.id(R.id.check_list).text(obj.getCheckList().getTitle());
                aq.id(R.id.check_list_layout).visible();
                toggleCheckList(aq.id(R.id.check_list).getView());
                aq.id(R.id.sub_task_header).clicked(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showHideCheckListView();
                    }
                });
                aq.id(R.id.check_list).getView().setEnabled(false);
                aq.id(R.id.arrow).clicked(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showHideCheckListView();
                    }
                });
            }
        }catch (NullPointerException npe){}
        try{
            if(!obj.getNotes().isEmpty()) {
                aq.id(R.id.notes).text(obj.getNotes());
                aq.id(R.id.notes_layout).visible();
            }
        }catch (NullPointerException npe){}
    }

    private void showHideCheckListView() {

        if(aq.id(R.id.check_list).getView().getVisibility() == View.VISIBLE){
            aq.id(R.id.check_list).gone();
            aq.id(R.id.arrow).image(R.drawable.arrow_down);
        }
        else{
            aq.id(R.id.check_list).visible();
            aq.id(R.id.arrow).image(R.drawable.arrow);
        }
    }

    private void populateAssignedTaskData(){
        AssignedTaskData obj = AssignedTaskData.getInstance();
        aq.id(R.id.title).text(obj.task.get(0).title);
        try {
            aq.id(R.id.repeat).text(obj.task.get(0).repeatUntil);
            aq.id(R.id.repeat_layout).visible();
        }catch (NullPointerException npe){}

        String strDate = String.valueOf(obj.task.get(0).startDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Utils.milliFromServerDate(strDate));
        strDate = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
                Locale.US)
                + " "
                + calendar.get(Calendar.DAY_OF_MONTH)
                + " "
                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                Locale.US) + " " + calendar.get(Calendar.YEAR);
        aq.id(R.id.time).text(strDate);
        try{
            aq.id(R.id.location).text(obj.task.get(0).location);
            aq.id(R.id.location_layout).visible();
        }catch (NullPointerException npe){}
        try {
//            aq.id(R.id.reminder).text("Remind before " + obj.task.get(0). / 60000 + " mins");
        }catch (NullPointerException npe){}
        try {
            aq.id(R.id.repeat_view).text(obj.task.get(0).repeatUntil);
            aq.id(R.id.repeat_layout).visible();
        }catch (NullPointerException npe){}
        try {
            aq.id(R.id.check_list).text(obj.task.get(0).checkListData);
            aq.id(R.id.check_list_layout).visible();
            toggleCheckList(aq.id(R.id.check_list).getView());
        }catch (NullPointerException npe){}
        try{
            aq.id(R.id.notes).text(obj.task.get(0).notes);
            aq.id(R.id.notes_layout).visible();
        }catch (NullPointerException npe){}
    }
    private void toggleCheckList(View switchView) {
        View newView;
        try {
            ChecklistManager mChecklistManager = ChecklistManager
                    .getInstance(this);
            mChecklistManager.setNewEntryHint("Add a sub task");
            mChecklistManager.setMoveCheckedOnBottom(1);
            mChecklistManager.setKeepChecked(true);
            mChecklistManager.setShowChecks(true);
            newView = mChecklistManager.convert(switchView);
            mChecklistManager.replaceViews(switchView, newView);

        } catch (ViewNotSupportedException e) {
            e.printStackTrace();
        }
    }


    private void showAttachments(String imageUrl){
        aq.id(R.id.attachment_layout).visible();
        try {
            final LinearLayout item = (LinearLayout) aq
                    .id(R.id.added_image_outer).visible().getView();

            final View child = getLayoutInflater().inflate(
                    R.layout.image_added_layout, null);

            ImageView image = (ImageView) child
                    .findViewById(R.id.image_added);

            ImageOptions options = new ImageOptions();
            options.round = 20;

            AQuery aq = new AQuery(child);
            aq.id(image).image(imageUrl, options);
            child.findViewById(R.id.image_menu).setVisibility(View.GONE);
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
