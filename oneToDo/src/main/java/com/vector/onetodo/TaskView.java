package com.vector.onetodo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.androidquery.AQuery;
import com.vector.model.AssignedTaskData;
import com.vector.model.ItemDetails;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Utils;

import net.appkraft.parallax.ParallaxScrollView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;

public class TaskView extends FragmentActivity {

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
        popupWindowTask = new PopupWindow(view1, Utils.getDpValue(200,
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
                getSupportFragmentManager().popBackStack();
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

        aq.id(R.id.title).text(obj.getTitle());
        aq.id(R.id.repeat).text(obj.getRepeat().getRepeat_interval());
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
        aq.id(R.id.location).text(obj.getLocation());
        aq.id(R.id.reminder).text("Remind before "+Integer.parseInt(obj.getReminder().getTime().toString())/60000+" mins");
        aq.id(R.id.repeat_view).text(obj.getRepeat().getRepeat_interval());
        aq.id(R.id.check_list).text(obj.getCheckList().getTitle());
        toggleCheckList(aq.id(R.id.check_list).getView());
        aq.id(R.id.notes).text(obj.getNotes());
    }

    private void populateAssignedTaskData(){
        AssignedTaskData obj = AssignedTaskData.getInstance();
        aq.id(R.id.title).text(obj.task.get(0).title);
        aq.id(R.id.repeat).text(obj.task.get(0).repeatUntil);
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
        aq.id(R.id.location).text(obj.task.get(0).location);
//        aq.id(R.id.reminder).text("Remind before "+obj.task.get(0)./60000+" mins");
        aq.id(R.id.repeat_view).text(obj.task.get(0).repeatUntil);
        aq.id(R.id.check_list).text(obj.task.get(0).checkListData);
        toggleCheckList(aq.id(R.id.check_list).getView());
        aq.id(R.id.notes).text(obj.task.get(0).notes);
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
            switchView = newView;

        } catch (ViewNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
